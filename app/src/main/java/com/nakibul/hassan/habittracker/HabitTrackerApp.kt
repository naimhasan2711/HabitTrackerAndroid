package com.nakibul.hassan.habittracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.nakibul.hassan.habittracker.notification.HabitReminderService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HabitTrackerApp : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "habit_reminders"
        const val NOTIFICATION_CHANNEL_NAME = "Habit Reminders"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // Start the foreground service for persistent reminders
        HabitReminderService.startService(this)
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for your daily habits"
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

