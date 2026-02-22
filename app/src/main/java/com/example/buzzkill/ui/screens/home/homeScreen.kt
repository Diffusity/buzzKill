package com.example.buzzkill.ui.screens.home

import android.icu.number.Scale
import androidx.annotation.experimental.Experimental
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Delete
import com.example.buzzkill.domain.model.notificationRules
import com.example.buzzkill.ui.navigation.screen
import com.example.buzzkill.viewmodel.homeUiState
import com.example.buzzkill.viewmodel.homeViewModel

@OptIn(ExperimentalMaterial3Api:: class)
@Composable
fun homeScreen(
    onAddRule: () -> Unit,
    onEditRule: (Long) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: homeViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("buzzKill") },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Default.History , contentDescription = "History")
                    }

                    IconButton(onClick = onOpenSettings ) {
                        Icon(Icons.Default.Settings , contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddRule,
                icon = { Icon(Icons.Default.Add , contentDescription = null) },
                text = { Text("New Rule") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            when(val state = uiState){
                is homeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is homeUiState.Empty -> {
                    emptyState(modifier = Modifier.align(Alignment.Center))
                }

                is homeUiState.Success -> {
                    ruleList(
                        rules = state.rules,
                        onToggle = { rule ->
                            viewModel.onToggleRule(rule.id , !rule.isEnabled)
                        },
                        onEdit = { rule -> onEditRule(rule.id) },
                        onDelete = { rule -> viewModel.onDeleteRule(rule) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ruleList(
    rules: List<notificationRules>,
    onToggle: (notificationRules) -> Unit,
    onEdit: (notificationRules) -> Unit,
    onDelete: (notificationRules) -> Unit
){
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = rules , key = { it.id }){ rule ->
            ruleCard(
                rule = rule,
                onToggle = { onToggle(rule) },
                onEdit = { onEdit(rule) },
                onDelete = { onDelete(rule) }
            )
        }
    }
}

@Composable
private fun ruleCard(
    rule: notificationRules,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
){
    var showDeleteConfirm by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(rule.isEnabled) {
                MaterialTheme.colorScheme.surface
            }else{
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        )
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = buildSubtitle(rule),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            rule.action,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = { onToggle() }
                )

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(13.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(32.dp)
                    ){
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if(showDeleteConfirm){
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Rule") },
            text = { Text("\"${rule.name}\" will be permanently deleted...!") },
            confirmButton = {
                TextButton(
                    onClick = { onDelete(); showDeleteConfirm = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false }
                ) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun emptyState(modifier: Modifier = Modifier){
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.NotificationsOff,
            contentDescription = null,
            modifier = modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text("No rules yet " , style = MaterialTheme.typography.titleMedium)
        Text(
            "Tap + to create your first notification rule",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun buildSubtitle(rule: notificationRules): String {
    val app = rule.targetPkg?.substringAfterLast(".")?:"All apps"
    val keyword =rule.keywordFilter?.let { ".\"$it\"" }?:""
    val time = if(rule.timeStart != null) ". ${rule.timeStart}-${rule.timeEnd}" else ""

    return listOf(app , keyword , time).filter { it.isNotEmpty() }.joinToString { " " }
}