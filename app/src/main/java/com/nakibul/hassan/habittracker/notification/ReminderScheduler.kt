package com.nakibul.hassan.habittracker.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import com.nakibul.hassan.habittracker.domain.model.Habit
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
        private const val EARLY_REMINDER_OFFSET_MINUTES = 5L
    }
    
    private val workManager: WorkManager = WorkManager.getInstance(context)
    
    /**
     * Schedule a reminder for a habit based on its frequency
     * This schedules both an early reminder (5 minutes before) and the main reminder
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
        
        // Cancel any existing reminders for this habit (both early and main)
        Log.d(TAG, "Cancelling any existing reminders for habit: ${habit.id}")
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
        
        // Calculate early reminder delay (5 minutes before main reminder)
        val earlyReminderDelay = initialDelay - TimeUnit.MINUTES.toMillis(EARLY_REMINDER_OFFSET_MINUTES)
        val shouldScheduleEarlyReminder = earlyReminderDelay > 0
        Log.d(TAG, "Early reminder delay: ${earlyReminderDelay}ms, shouldSchedule: $shouldScheduleEarlyReminder")
        
        // Input data for main reminder
        val mainInputData = Data.Builder()
            .putString(ReminderWorker.KEY_HABIT_ID, habit.id)
            .putString(ReminderWorker.KEY_HABIT_NAME, habit.name)
            .putBoolean(ReminderWorker.KEY_IS_EARLY_REMINDER, false)
            .build()
        
        // Input data for early reminder
        val earlyInputData = Data.Builder()
            .putString(ReminderWorker.KEY_HABIT_ID, habit.id)
            .putString(ReminderWorker.KEY_HABIT_NAME, habit.name)
            .putBoolean(ReminderWorker.KEY_IS_EARLY_REMINDER, true)
            .build()
        
        // Schedule based on frequency
        when (habit.frequency) {
            "daily" -> {
                Log.d(TAG, "Scheduling DAILY reminder with 1 day interval")
                
                // Schedule early reminder (5 minutes before)
                if (shouldScheduleEarlyReminder) {
                    val earlyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                        repeatInterval = 1,
                        repeatIntervalTimeUnit = TimeUnit.DAYS
                    )
                        .setInitialDelay(earlyReminderDelay, TimeUnit.MILLISECONDS)
                        .setInputData(earlyInputData)
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiresBatteryNotLow(false)
                                .build()
                        )
                        .addTag(getEarlyWorkTag(habit.id))
                        .build()
                    
                    workManager.enqueueUniquePeriodicWork(
                        getEarlyUniqueWorkName(habit.id),
                        ExistingPeriodicWorkPolicy.UPDATE,
                        earlyWorkRequest
                    )
                    Log.d(TAG, "Enqueued early daily work request: ${getEarlyUniqueWorkName(habit.id)}")
                }
                
                // Schedule main reminder
                val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    repeatInterval = 1,
                    repeatIntervalTimeUnit = TimeUnit.DAYS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(mainInputData)
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
                
                // Schedule early reminder (5 minutes before)
                if (shouldScheduleEarlyReminder) {
                    val earlyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                        repeatInterval = 7,
                        repeatIntervalTimeUnit = TimeUnit.DAYS
                    )
                        .setInitialDelay(earlyReminderDelay, TimeUnit.MILLISECONDS)
                        .setInputData(earlyInputData)
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiresBatteryNotLow(false)
                                .build()
                        )
                        .addTag(getEarlyWorkTag(habit.id))
                        .build()
                    
                    workManager.enqueueUniquePeriodicWork(
                        getEarlyUniqueWorkName(habit.id),
                        ExistingPeriodicWorkPolicy.UPDATE,
                        earlyWorkRequest
                    )
                    Log.d(TAG, "Enqueued early weekly work request: ${getEarlyUniqueWorkName(habit.id)}")
                }
                
                // Schedule main reminder
                val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    repeatInterval = 7,
                    repeatIntervalTimeUnit = TimeUnit.DAYS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(mainInputData)
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
                
                // Schedule early reminder (5 minutes before)
                if (shouldScheduleEarlyReminder) {
                    val earlyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                        repeatInterval = 1,
                        repeatIntervalTimeUnit = TimeUnit.DAYS
                    )
                        .setInitialDelay(earlyReminderDelay, TimeUnit.MILLISECONDS)
                        .setInputData(earlyInputData)
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiresBatteryNotLow(false)
                                .build()
                        )
                        .addTag(getEarlyWorkTag(habit.id))
                        .build()
                    
                    workManager.enqueueUniquePeriodicWork(
                        getEarlyUniqueWorkName(habit.id),
                        ExistingPeriodicWorkPolicy.UPDATE,
                        earlyWorkRequest
                    )
                    Log.d(TAG, "Enqueued early custom work request: ${getEarlyUniqueWorkName(habit.id)}")
                }
                
                // Schedule main reminder
                val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    repeatInterval = 1,
                    repeatIntervalTimeUnit = TimeUnit.DAYS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(mainInputData)
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
     * Cancel a scheduled reminder for a habit (both early and main reminders)
     */
    fun cancelHabitReminder(habitId: String) {
        Log.d(TAG, "Cancelling all reminders for habit: $habitId")
        
        // Cancel main reminder
        workManager.cancelUniqueWork(getUniqueWorkName(habitId))
        workManager.cancelAllWorkByTag(getWorkTag(habitId))
        Log.d(TAG, "Cancelled main work: ${getUniqueWorkName(habitId)}")
        
        // Cancel early reminder
        workManager.cancelUniqueWork(getEarlyUniqueWorkName(habitId))
        workManager.cancelAllWorkByTag(getEarlyWorkTag(habitId))
        Log.d(TAG, "Cancelled early work: ${getEarlyUniqueWorkName(habitId)}")
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
    
    private fun getEarlyUniqueWorkName(habitId: String): String {
        return "habit_early_reminder_$habitId"
    }
    
    private fun getEarlyWorkTag(habitId: String): String {
        return "early_reminder_tag_$habitId"
    }
}

