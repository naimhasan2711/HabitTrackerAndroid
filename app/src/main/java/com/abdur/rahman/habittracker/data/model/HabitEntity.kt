package com.abdur.rahman.habittracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class HabitEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val icon: String,
    val color: Int,
    val categoryId: String? = null,
    val frequency: String = "daily", // daily, weekly, custom
    val customDays: String? = null, // JSON array of weekday integers 1-7
    val reminderTime: String? = null, // HH:mm format
    val createdAt: String, // ISO date format
    val archived: Boolean = false,
    val sortOrder: Int = 0
)
