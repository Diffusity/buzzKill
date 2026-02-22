package com.example.buzzkill.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buzzkill.domain.model.notificationLogs
import com.example.buzzkill.domain.usecase.clearLogsUseCase
import com.example.buzzkill.domain.usecase.getNotificationLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class historyViewModel @Inject constructor(
    private val getLogs: getNotificationLogsUseCase,
    private val clearLogsUseCase: clearLogsUseCase
) : ViewModel() {

    val logs: Flow<List<notificationLogs>> = getLogs()

    fun clearLogs() {
        viewModelScope.launch {
            clearLogsUseCase.invoke()
        }
    }
}