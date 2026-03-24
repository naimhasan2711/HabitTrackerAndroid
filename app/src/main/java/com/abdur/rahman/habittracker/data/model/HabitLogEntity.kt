package com.abdur.rahman.habittracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("habitId"),
        Index(value = ["habitId", "date"], unique = true)
    ]
)
data class HabitLogEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val date: String, // yyyy-MM-dd format
    val completed: Boolean,
    val completedAt: String? = null, // ISO datetime
    val notes: String? = null
)
