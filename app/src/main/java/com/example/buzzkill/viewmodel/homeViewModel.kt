package com.example.buzzkill.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buzzkill.data.local.entity.rules
import com.example.buzzkill.domain.model.notificationRules
import com.example.buzzkill.domain.usecase.deleteRuleUseCase
import com.example.buzzkill.domain.usecase.getALlRulesUseCase
import com.example.buzzkill.domain.usecase.toggleRuleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class homeUiState{
    object Loading: homeUiState()
    data class Success(val rules: List<notificationRules>): homeUiState()
    object Empty: homeUiState()
}

@HiltViewModel
class homeViewModel @Inject constructor(
    private val getAllRules: getALlRulesUseCase,
    private val toggleRuleUseCase: toggleRuleUseCase,
    private val deleteRuleUseCase: deleteRuleUseCase
): ViewModel(){
    val uiState: StateFlow<homeUiState> = getAllRules().map { rules ->
        if (rules.isEmpty()) {
            homeUiState.Empty
        } else {
            homeUiState.Success(rules)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = homeUiState.Loading
    )

    fun onToggleRule(ruleID: Long , enabled: Boolean){
        viewModelScope.launch {
            toggleRuleUseCase.invoke(ruleID , enabled)
        }
    }

    fun onDeleteRule(rule: notificationRules){
        viewModelScope.launch {
            deleteRuleUseCase.invoke(rule)
        }
    }
}