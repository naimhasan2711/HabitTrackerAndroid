package com.nakibul.hassan.habittracker.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.nakibul.hassan.habittracker.HabitTrackerApp
import com.nakibul.hassan.habittracker.R
import com.nakibul.hassan.habittracker.notification.NotificationHelper.Companion.EXTRA_HABIT_ID
import com.nakibul.hassan.habittracker.notification.NotificationHelper.Companion.EXTRA_HABIT_NAME
import com.nakibul.hassan.habittracker.presentation.ui.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra(EXTRA_HABIT_ID) ?: return
        val habitName = intent.getStringExtra(EXTRA_HABIT_NAME) ?: "Your habit"
        
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }
        
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            habitId.hashCode(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, HabitTrackerApp.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Habit Reminder")
            .setContentText("Time to complete: $habitName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(habitId.hashCode(), notification)
    }
}

