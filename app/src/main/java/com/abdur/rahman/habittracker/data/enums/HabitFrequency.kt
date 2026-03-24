package com.abdur.rahman.habittracker.data.enums

enum class HabitFrequency(val value: String) {
    DAILY("daily"),
    WEEKLY("weekly"),
    CUSTOM("custom");
    
    companion object {
        fun fromValue(value: String): HabitFrequency {
            return entries.find { it.value == value } ?: DAILY
        }
    }
}
