package com.abdur.rahman.habittracker.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abdur.rahman.habittracker.domain.repository.SettingsRepository
import com.abdur.rahman.habittracker.presentation.components.BottomNavItem
import com.abdur.rahman.habittracker.presentation.components.HabitTrackerBottomNavBar
import com.abdur.rahman.habittracker.presentation.components.bottomNavItems
import com.abdur.rahman.habittracker.presentation.navigation.AppNavHost
import com.abdur.rahman.habittracker.shared.constant.SettingsKeys
import com.abdur.rahman.habittracker.ui.theme.HabitTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Request notification permission for Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                Log.d(TAG, "POST_NOTIFICATIONS permission granted: $isGranted")
            }
            
            // Check and request notification permission on launch
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    
                    Log.d(TAG, "Checking POST_NOTIFICATIONS permission: hasPermission=$hasPermission")
                    
                    if (!hasPermission) {
                        Log.d(TAG, "Requesting POST_NOTIFICATIONS permission")
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            
            val darkModeFlow = settingsRepository.getSettingFlow(SettingsKeys.DARK_MODE)
                .map { it?.toBoolean() ?: false }
            val isDarkMode by darkModeFlow.collectAsState(initial = false)
            HabitTrackerTheme(
                darkTheme = isDarkMode,
                dynamicColor = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    // Determine if we should show the bottom nav
                    val shouldShowBottomNav by remember(currentRoute) {
                        derivedStateOf {
                            bottomNavItems.any { it.route == currentRoute }
                        }
                    }
                    Scaffold(
                        bottomBar = {
                            if (shouldShowBottomNav) {
                                HabitTrackerBottomNavBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { item ->
                                        navController.navigate(item.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
