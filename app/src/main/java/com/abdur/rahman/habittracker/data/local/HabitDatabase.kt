package com.abdur.rahman.habittracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abdur.rahman.habittracker.data.local.dao.*
import com.abdur.rahman.habittracker.data.model.*

@Database(
    entities = [
        CategoryEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        StreakEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class HabitDatabase : RoomDatabase() {
    
    abstract fun categoryDao(): CategoryDao
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun streakDao(): StreakDao
    abstract fun settingsDao(): SettingsDao
    
    companion object {
        const val DATABASE_NAME = "habit_tracker_db"
    }
}
