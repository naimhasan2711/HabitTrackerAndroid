package com.abdur.rahman.habittracker.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import com.abdur.rahman.habittracker.domain.model.Habit
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReminderScheduler @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "ReminderScheduler"
    }
    
    private val workManager: WorkManager = WorkManager.getInstance(context)
    
    /**
     * Schedule a reminder for a habit based on its frequency
     */
    fun scheduleHabitReminder(habit: Habit) {
        Log.d(TAG, "====== SCHEDULING REMINDER START ======")
        Log.d(TAG, "Habit ID: ${habit.id}")
        Log.d(TAG, "Habit Name: ${habit.name}")
        Log.d(TAG, "Habit Frequency: ${habit.frequency}")
        Log.d(TAG, "Reminder Time: ${habit.reminderTime}")
        
        val reminderTime = habit.reminderTime
        if (reminderTime == null) {
            Log.w(TAG, "Reminder time is null, skipping schedule")
            return
        }
        
        // Cancel any existing reminder for this habit
        Log.d(TAG, "Cancelling any existing reminder for habit: ${habit.id}")
        cancelHabitReminder(habit.id)
        
        // Parse the reminder time (HH:mm format)
        val timeParts = reminderTime.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull()
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
        
        if (hour == null) {
            Log.e(TAG, "Failed to parse reminder time: $reminderTime")
            return
        }
        
        Log.d(TAG, "Parsed time - Hour: $hour, Minute: $minute")
        
        // Calculate initial delay until the reminder time
        val now = LocalDateTime.now()
        var reminderDateTime = now.toLocalDate().atTime(LocalTime.of(hour, minute))
        
        Log.d(TAG, "Current time: $now")
        Log.d(TAG, "Initial target time: $reminderDateTime")
        
        // If the time has already passed today, schedule for tomorrow
        if (reminderDateTime.isBefore(now)) {
            reminderDateTime = reminderDateTime.plusDays(1)
            Log.d(TAG, "Time already passed, scheduling for tomorrow: $reminderDateTime")
        }
        
        val initialDelay = Duration.between(now, reminderDateTime).toMillis()
        Log.d(TAG, "Initial delay: ${initialDelay}ms (${initialDelay / 1000 / 60} minutes)")
        
        val inputData = Data.Builder()
            .putString(ReminderWorker.KEY_HABIT_ID, habit.id)
            .putString(ReminderWorker.KEY_HABIT_NAME, habit.name)
            .build()
        
        // Schedule based on frequency
        when (habit.frequency) {
            "daily" -> {
                Log.d(TAG, "Scheduling DAILY reminder with 1 day interval")
                // Schedule repeating daily work
                val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    repeatInterval = 1,
                    repeatIntervalTimeUnit = TimeUnit.DAYS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiresBatteryNotLow(false)
                            .build()
                    )
                    .addTag(getWorkTag(habit.id))
                    .build()
                
                workManager.enqueueUniquePeriodicWork(
                    getUniqueWorkName(habit.id),
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
                Log.d(TAG, "Enqueued daily work request: ${getUniqueWorkName(habit.id)}")
            }
            
            "weekly" -> {
                Log.d(TAG, "Scheduling WEEKLY reminder with 7 day interval")
                // Schedule repeating weekly work
                val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    repeatInterval = 7,
                    repeatIntervalTimeUnit = TimeUnit.DAYS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiresBatteryNotLow(false)
                            .build()
                    )
                    .addTag(getWorkTag(habit.id))
                    .build()
                
                workManager.enqueueUniquePeriodicWork(
                    getUniqueWorkName(habit.id),
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
                Log.d(TAG, "Enqueued weekly work request: ${getUniqueWorkName(habit.id)}")
            }
            
            "custom" -> {
                Log.d(TAG, "Scheduling CUSTOM reminder with 1 day interval (filter by day in worker)")
                // For custom days, we schedule daily but the worker filters by day
                val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    repeatInterval = 1,
                    repeatIntervalTimeUnit = TimeUnit.DAYS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiresBatteryNotLow(false)
                            .build()
                    )
                    .addTag(getWorkTag(habit.id))
                    .build()
                
                workManager.enqueueUniquePeriodicWork(
                    getUniqueWorkName(habit.id),
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
                Log.d(TAG, "Enqueued custom work request: ${getUniqueWorkName(habit.id)}")
            }
            
            "once" -> {
                Log.d(TAG, "Skipping reminder for one-time habit")
                // No reminders for one-time habits
            }
            
            else -> {
                Log.w(TAG, "Unknown frequency: ${habit.frequency}")
            }
        }
        Log.d(TAG, "====== SCHEDULING REMINDER END ======")
    }
    
    /**
     * Cancel a scheduled reminder for a habit
     */
    fun cancelHabitReminder(habitId: String) {
        Log.d(TAG, "Cancelling reminder for habit: $habitId")
        workManager.cancelUniqueWork(getUniqueWorkName(habitId))
        workManager.cancelAllWorkByTag(getWorkTag(habitId))
        Log.d(TAG, "Cancelled work: ${getUniqueWorkName(habitId)}")
    }
    
    /**
     * Reschedule all active habit reminders (called on app startup or boot)
     */
    suspend fun rescheduleAllReminders(habits: List<Habit>) {
        Log.d(TAG, "Rescheduling all reminders for ${habits.size} habits")
        var scheduledCount = 0
        habits.forEach { habit ->
            if (habit.reminderTime != null && !habit.archived) {
                scheduleHabitReminder(habit)
                scheduledCount++
            }
        }
        Log.d(TAG, "Rescheduled $scheduledCount reminders")
    }
    
    private fun getUniqueWorkName(habitId: String): String {
        return "habit_reminder_$habitId"
    }
    
    private fun getWorkTag(habitId: String): String {
        return "reminder_tag_$habitId"
    }
}
