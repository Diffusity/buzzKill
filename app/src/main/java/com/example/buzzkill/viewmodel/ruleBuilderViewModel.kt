package com.example.buzzkill.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buzzkill.domain.model.notificationRules
import com.example.buzzkill.domain.usecase.saveRuleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ruleBuilderUiState(
    val name: String = "",
    val targetPkg: String = "",
    val keywordFilter: String = "",
    val selectedAction: String = "COOLDOWN",
    val timeStart: String = "",
    val timeEnd: String = "",
    val coolDownTime: Int = 5,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ruleBuilderViewModel @Inject constructor(
    private val saveRule: saveRuleUseCase
): ViewModel(){
    private val _uiState = MutableStateFlow(ruleBuilderUiState())
    val uiState: StateFlow<ruleBuilderUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) = _uiState.update { it.copy(name = name) }
    fun onPackageChanged(pkg: String) = _uiState.update { it.copy(targetPkg = pkg) }
    fun onKeywordChanged(s: String) = _uiState.update { it.copy(keywordFilter = s) }
    fun onActionSelected(action: String) = _uiState.update { it.copy(selectedAction = action) }
    fun onTimeStartChanged(time: String) = _uiState.update { it.copy(timeStart = time) }
    fun onTimeEndChanged(time: String) = _uiState.update { it.copy(timeEnd = time) }
    fun onCooldownTimeChanged(min: Int) = _uiState.update { it.copy(coolDownTime = min) }
    fun onSaveRule(){
        val state = _uiState.value
        if(state.name.isBlank()){
            _uiState.update { it.copy(errorMessage = "Rule name cannot be empty...!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true , errorMessage = null) }
            val rule = notificationRules(
                name = state.name.trim(),
                targetPkg = state.targetPkg.trim().ifEmpty { null },
                keywordFilter = state.keywordFilter.trim().ifEmpty { null },
                action = state.selectedAction,
                timeStart = state.timeStart.ifEmpty { null },
                timeEnd = state.timeEnd.ifEmpty { null },
                coolDownTime = state.coolDownTime,
                isEnabled = true
            )

            saveRule(rule)
            _uiState.update { it.copy(isSaving = false , saveSuccess = true) }
        }
    }

    fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }
}