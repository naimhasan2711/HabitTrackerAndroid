package com.abdur.rahman.habittracker.data.enums

enum class HabitCategory(
    val displayName: String,
    val defaultIcon: String,
    val defaultColor: Int
) {
    HEALTH("Health", "favorite", 0xFFEF4444.toInt()),
    FITNESS("Fitness", "fitness_center", 0xFFF97316.toInt()),
    LEARNING("Learning", "school", 0xFF3B82F6.toInt()),
    WORK("Work", "work", 0xFF8B5CF6.toInt()),
    PERSONAL("Personal", "person", 0xFF6366F1.toInt()),
    FINANCE("Finance", "account_balance", 0xFF22C55E.toInt()),
    SOCIAL("Social", "people", 0xFFEC4899.toInt()),
    OTHER("Other", "category", 0xFF6B7280.toInt());
    
    companion object {
        fun fromDisplayName(name: String): HabitCategory {
            return entries.find { it.displayName == name } ?: OTHER
        }
    }
}
