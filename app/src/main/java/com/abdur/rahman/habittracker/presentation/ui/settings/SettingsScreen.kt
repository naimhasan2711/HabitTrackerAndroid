package com.abdur.rahman.habittracker.presentation.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToArchivedHabits: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportData(context, it) }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importData(context, it) }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // Screen title
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // Appearance Section
        item {
            SettingsSection(title = "Appearance")
        }
        
        item {
            SwitchSettingItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Follow system theme or toggle manually",
                checked = uiState.darkMode,
                onCheckedChange = viewModel::toggleDarkMode
            )
            }
            
            // Behavior Section
            item {
                SettingsSection(title = "Behavior")
            }
            
            item {
                SwitchSettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Enable habit reminders",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = viewModel::toggleNotifications
                )
            }
            
            item {
                SwitchSettingItem(
                    icon = Icons.Default.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on habit completion",
                    checked = uiState.hapticFeedback,
                    onCheckedChange = viewModel::toggleHapticFeedback
                )
            }
            
            item {
                SwitchSettingItem(
                    icon = Icons.Default.AcUnit,
                    title = "Streak Freeze",
                    subtitle = "Allow 1-day grace period for streaks",
                    checked = uiState.streakFreezeEnabled,
                    onCheckedChange = viewModel::toggleStreakFreeze
                )
            }
            
            // Data Section
            item {
                SettingsSection(title = "Data")
            }
            
            item {
                ClickableSettingItem(
                    icon = Icons.Default.Archive,
                    title = "Archived Habits",
                    subtitle = "View and restore archived habits",
                    onClick = onNavigateToArchivedHabits
                )
            }
            
            item {
                ClickableSettingItem(
                    icon = Icons.Default.Upload,
                    title = "Export Data",
                    subtitle = "Save your data as JSON file",
                    onClick = { exportLauncher.launch("habit_tracker_backup.json") }
                )
            }
            
            item {
                ClickableSettingItem(
                    icon = Icons.Default.Download,
                    title = "Import Data",
                    subtitle = "Restore from JSON backup",
                    onClick = { importLauncher.launch(arrayOf("application/json")) }
                )
            }
            
            // About Section
            item {
                SettingsSection(title = "About")
            }
            
            item {
                ClickableSettingItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = { }
                )
            }
            
            item {
                ClickableSettingItem(
                    icon = Icons.Default.Security,
                    title = "Privacy",
                    subtitle = "100% offline, no tracking",
                    onClick = { }
                )
            }
        }
    
    // Show snackbar for export/import status
    uiState.message?.let { message ->
        LaunchedEffect(message) {
            viewModel.clearMessage()
        }
    }
}

@Composable
private fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SwitchSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
private fun ClickableSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}
