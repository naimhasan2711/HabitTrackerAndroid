package com.abdur.rahman.habittracker.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abdur.rahman.habittracker.HabitTrackerApp
import com.abdur.rahman.habittracker.R
import com.abdur.rahman.habittracker.data.local.HabitDatabase
import com.abdur.rahman.habittracker.presentation.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: HabitDatabase
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        const val KEY_HABIT_ID = "habit_id"
        const val KEY_HABIT_NAME = "habit_name"
        private const val TAG = "ReminderWorker"
    }
    
    override suspend fun doWork(): Result {
        Log.d(TAG, "====== REMINDER WORKER STARTED ======")
        Log.d(TAG, "Current time: ${LocalTime.now()}")
        
        val habitId = inputData.getString(KEY_HABIT_ID)
        val habitName = inputData.getString(KEY_HABIT_NAME)
        
        Log.d(TAG, "Input - Habit ID: $habitId, Habit Name: $habitName")
        
        if (habitId == null) {
            Log.e(TAG, "Habit ID is null, failing work")
            return Result.failure()
        }
        
        try {
            val habitEntity = database.habitDao().getHabitById(habitId)
            Log.d(TAG, "Loaded habit from database: ${habitEntity?.name ?: "NULL"}")
            
            if (habitEntity == null) {
                Log.w(TAG, "Habit not found in database, returning success")
                return Result.success()
            }
            
            if (habitEntity.archived) {
                Log.d(TAG, "Habit is archived, skipping reminder")
                return Result.success()
            }
            
            // Check if reminder should be shown based on frequency
            val shouldShow = shouldShowReminder(habitEntity.frequency, habitEntity.customDays)
            Log.d(TAG, "Should show reminder: $shouldShow (frequency: ${habitEntity.frequency})")
            
            if (!shouldShow) {
                Log.d(TAG, "Not showing reminder based on frequency/day check")
                return Result.success()
            }
            
            // Check if already completed today
            val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val todayLog = database.habitLogDao().getLogForHabitOnDate(habitId, todayDate)
            
            Log.d(TAG, "Today's date: $todayDate")
            Log.d(TAG, "Today's log: ${todayLog?.completed ?: "NULL"}")
            
            if (todayLog?.completed == true) {
                Log.d(TAG, "Habit already completed today, skipping reminder")
                return Result.success()
            }
            
            // Show notification
            Log.d(TAG, "Showing reminder notification for: ${habitEntity.name}")
            showReminderNotification(habitId, habitEntity.name)
            
            Log.d(TAG, "====== REMINDER WORKER COMPLETED ======")
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in reminder worker: ${e.message}", e)
            return Result.retry()
        }
    }
    
    private fun shouldShowReminder(frequency: String, customDaysJson: String?): Boolean {
        val today = LocalDate.now().dayOfWeek.value // 1 = Monday, 7 = Sunday
        Log.d(TAG, "Checking if should show reminder - Frequency: $frequency, Today: $today (1=Mon, 7=Sun)")
        
        val result = when (frequency) {
            "daily" -> {
                Log.d(TAG, "Daily habit - showing reminder")
                true
            }
            "weekly" -> {
                val show = today == 1 // Only on Mondays for weekly
                Log.d(TAG, "Weekly habit - today is Monday: $show")
                show
            }
            "custom" -> {
                if (customDaysJson.isNullOrEmpty()) {
                    Log.d(TAG, "Custom habit with no days set, showing reminder")
                    return true
                }
                try {
                    val days = customDaysJson.removeSurrounding("[", "]")
                        .split(",")
                        .map { it.trim().toInt() }
                    val contains = days.contains(today)
                    Log.d(TAG, "Custom days: $days, today ($today) is in list: $contains")
                    contains
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing custom days: $customDaysJson", e)
                    true
                }
            }
            "once" -> {
                Log.d(TAG, "One-time habit - no reminder")
                false
            }
            else -> {
                Log.d(TAG, "Unknown frequency, showing reminder")
                true
            }
        }
        return result
    }
    
    private fun showReminderNotification(habitId: String, habitName: String) {
        Log.d(TAG, "Showing notification for habit: $habitName (ID: $habitId)")
        
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted, cannot show notification")
                return
            }
        }
        
        Log.d(TAG, "Creating notification intent...")
        
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("habitId", habitId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            habitId.hashCode(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Get default alarm sound
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        Log.d(TAG, "Building notification with sound: $alarmSound")
        
        val notification = NotificationCompat.Builder(context, HabitTrackerApp.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.habit)
            .setContentTitle("Habit Reminder")
            .setContentText("Time to complete: $habitName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(habitId.hashCode(), notification)
        Log.d(TAG, "Notification posted with ID: ${habitId.hashCode()}")
    }
}
