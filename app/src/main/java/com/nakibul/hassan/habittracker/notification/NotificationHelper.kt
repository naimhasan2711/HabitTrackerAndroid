package com.nakibul.hassan.habittracker.notification

import android.content.Context
import android.util.Log
import com.nakibul.hassan.habittracker.domain.model.Habit
import javax.inject.Inject

/**
 * Helper class that delegates to ReminderScheduler for WorkManager-based reminders
 */
class NotificationHelper @Inject constructor(
    private val context: Context,
    private val reminderScheduler: ReminderScheduler
) {
    
    companion object {
        private const val TAG = "NotificationHelper"
        const val EXTRA_HABIT_ID = "habit_id"
        const val EXTRA_HABIT_NAME = "habit_name"
    }
    
    fun scheduleHabitReminder(habit: Habit) {
        Log.d(TAG, "scheduleHabitReminder called for: ${habit.name} (ID: ${habit.id})")
        Log.d(TAG, "Reminder time: ${habit.reminderTime}, Frequency: ${habit.frequency}")
        reminderScheduler.scheduleHabitReminder(habit)
    }
    
    fun cancelHabitReminder(habitId: String) {
        Log.d(TAG, "cancelHabitReminder called for ID: $habitId")
        reminderScheduler.cancelHabitReminder(habitId)
    }
}

