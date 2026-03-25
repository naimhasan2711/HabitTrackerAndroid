package com.nakibul.hassan.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Int,
    val icon: String
)

