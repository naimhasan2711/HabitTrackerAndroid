package com.abdur.rahman.habittracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abdur.rahman.habittracker.data.local.HabitDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var database: HabitDatabase
    
    @Inject
    lateinit var notificationHelper: NotificationHelper
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all habit reminders after device boot
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val habitsWithReminders = database.habitDao().getHabitsWithReminders()
                    habitsWithReminders.forEach { habitEntity ->
                        val habit = com.abdur.rahman.habittracker.domain.model.Habit(
                            id = habitEntity.id,
                            name = habitEntity.name,
                            description = habitEntity.description,
                            icon = habitEntity.icon,
                            color = habitEntity.color,
                            categoryId = habitEntity.categoryId,
                            frequency = habitEntity.frequency,
                            reminderTime = habitEntity.reminderTime,
                            createdAt = habitEntity.createdAt,
                            archived = habitEntity.archived,
                            sortOrder = habitEntity.sortOrder
                        )
                        notificationHelper.scheduleHabitReminder(habit)
                    }
                } catch (e: Exception) {
                    // Log error
                }
            }
        }
    }
}
