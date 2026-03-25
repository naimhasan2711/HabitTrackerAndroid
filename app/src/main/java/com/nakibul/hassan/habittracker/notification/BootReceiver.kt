package com.nakibul.hassan.habittracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nakibul.hassan.habittracker.data.local.HabitDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    @Inject
    lateinit var database: HabitDatabase
    
    @Inject
    lateinit var reminderScheduler: ReminderScheduler
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called with action: ${intent.action}")
        
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device boot completed, starting reminder service and rescheduling")
            
            // Start the foreground service
            HabitReminderService.startService(context)
            
            // Reschedule all habit reminders after device boot
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val habitsWithReminders = database.habitDao().getHabitsWithReminders()
                    Log.d(TAG, "Found ${habitsWithReminders.size} habits with reminders to reschedule")
                    
                    habitsWithReminders.forEach { habitEntity ->
                        Log.d(TAG, "Rescheduling: ${habitEntity.name} at ${habitEntity.reminderTime}")
                        val habit = com.nakibul.hassan.habittracker.domain.model.Habit(
                            id = habitEntity.id,
                            name = habitEntity.name,
                            description = habitEntity.description,
                            icon = habitEntity.icon,
                            color = habitEntity.color,
                            categoryId = habitEntity.categoryId,
                            frequency = habitEntity.frequency,
                            customDays = habitEntity.customDays?.let { parseCustomDays(it) },
                            reminderTime = habitEntity.reminderTime,
                            createdAt = habitEntity.createdAt,
                            archived = habitEntity.archived,
                            sortOrder = habitEntity.sortOrder
                        )
                        reminderScheduler.scheduleHabitReminder(habit)
                    }
                    Log.d(TAG, "All reminders rescheduled successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error rescheduling reminders: ${e.message}", e)
                }
            }
        }
    }
    
    private fun parseCustomDays(json: String): List<Int> {
        return try {
            json.removeSurrounding("[", "]")
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing custom days: $json", e)
            emptyList()
        }
    }
}

