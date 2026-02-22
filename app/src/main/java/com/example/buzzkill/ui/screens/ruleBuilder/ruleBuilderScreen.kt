package com.example.buzzkill.ui.screens.ruleBuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Church
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buzzkill.viewmodel.homeViewModel
import com.example.buzzkill.viewmodel.ruleBuilderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ruleBuilderScreen(
    onNavigateBack: () -> Unit,
    viewModel: ruleBuilderViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if(uiState.saveSuccess){
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Rule") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack , contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::onSaveRule,
                        enabled = !uiState.isSaving
                    ) {
                        if(uiState.isSaving){
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }else{
                            Icon(Icons.Default.Check , contentDescription = "Save Rule")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            sectionHeader("Rule Details")
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Rule Name *") },
                placeholder = { Text("e.g.: Silence WhatsApp at night") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.targetPkg,
                onValueChange = viewModel:: onPackageChanged,
                label = { Text("Keyword Filter (optional)") },
                placeholder = { Text("e.g. Mom") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            sectionHeader("Action")
            actionSelector(
                selected = uiState.selectedAction,
                onSelect = viewModel:: onActionSelected
            )

            if(uiState.selectedAction == "COOLDOWN"){
                OutlinedTextField(
                    value = uiState.coolDownTime.toString(),
                    onValueChange = { it.toIntOrNull()?.let(viewModel::onCooldownTimeChanged) },
                    label = { Text("Cooldown Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            sectionHeader("Time Window (optional)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                OutlinedTextField(
                    value = uiState.timeStart,
                    onValueChange = viewModel:: onTimeStartChanged,
                    label = { Text("Start (HH:mm)") },
                    placeholder = { Text("22:00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = uiState.timeEnd,
                    onValueChange = viewModel::onTimeEndChanged,
                    label = { Text("End (HH:mm)") },
                    placeholder = { Text("07:00") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Text(
                    "Leave empty to apply at all times. Supports overnight ranges e.g. 22:00 → 07:00",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun actionSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    val actions = listOf(
        "COOLDOWN", "DISMISS", "REMIND",
        "SNOOZE", "SPEAK", "CUSTOM_SOUND",
        "VIBRATE", "SILENT", "NONE"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        actions.chunked(3).forEach { rowActions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowActions.forEach { action ->
                    FilterChip(
                        selected = action == selected,
                        onClick = { onSelect(action) },
                        label = {
                            Text(
                                action,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun sectionHeader(title: String){
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
    HorizontalDivider()
}