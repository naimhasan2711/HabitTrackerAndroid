package com.nakibul.hassan.habittracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "streaks",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId"], unique = true)]
)
data class StreakEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDate: String? = null,
    val freezeUsed: Boolean = false
)

