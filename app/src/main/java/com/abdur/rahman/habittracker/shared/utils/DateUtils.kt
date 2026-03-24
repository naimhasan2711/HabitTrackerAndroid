package com.abdur.rahman.habittracker.shared.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val displayDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    private val displayMonthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE")
    
    fun getCurrentDate(): String {
        return LocalDate.now().format(dateFormatter)
    }
    
    fun getCurrentDateTime(): String {
        return LocalDateTime.now().format(dateTimeFormatter)
    }
    
    fun getCurrentTime(): String {
        return LocalTime.now().format(timeFormatter)
    }
    
    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }
    
    fun parseDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, dateFormatter)
    }
    
    fun formatForDisplay(dateString: String): String {
        val date = parseDate(dateString)
        return date.format(displayDateFormatter)
    }
    
    fun formatMonthYear(date: LocalDate): String {
        return date.format(displayMonthYearFormatter)
    }
    
    fun formatDayOfWeek(date: LocalDate): String {
        return date.format(dayOfWeekFormatter)
    }
    
    fun getDateMinusDays(days: Long): String {
        return LocalDate.now().minusDays(days).format(dateFormatter)
    }
    
    fun getDatePlusDays(days: Long): String {
        return LocalDate.now().plusDays(days).format(dateFormatter)
    }
    
    fun getDaysBetween(startDate: String, endDate: String): Long {
        val start = parseDate(startDate)
        val end = parseDate(endDate)
        return ChronoUnit.DAYS.between(start, end)
    }
    
    fun isToday(dateString: String): Boolean {
        return dateString == getCurrentDate()
    }
    
    fun isYesterday(dateString: String): Boolean {
        return dateString == getDateMinusDays(1)
    }
    
    fun getDayOfWeek(dateString: String): Int {
        val date = parseDate(dateString)
        return date.dayOfWeek.value // 1 = Monday, 7 = Sunday
    }
    
    fun getStartOfWeek(date: LocalDate = LocalDate.now()): LocalDate {
        return date.with(DayOfWeek.MONDAY)
    }
    
    fun getStartOfMonth(date: LocalDate = LocalDate.now()): LocalDate {
        return date.withDayOfMonth(1)
    }
    
    fun getEndOfMonth(date: LocalDate = LocalDate.now()): LocalDate {
        return date.withDayOfMonth(date.lengthOfMonth())
    }
    
    fun getDatesInRange(startDate: String, endDate: String): List<String> {
        val start = parseDate(startDate)
        val end = parseDate(endDate)
        val dates = mutableListOf<String>()
        var current = start
        while (!current.isAfter(end)) {
            dates.add(formatDate(current))
            current = current.plusDays(1)
        }
        return dates
    }
    
    fun getGreeting(): String {
        val hour = LocalTime.now().hour
        return when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
    
    fun parseTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, timeFormatter)
    }
    
    fun formatTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }
    
    fun formatTimeForDisplay(timeString: String): String {
        val time = parseTime(timeString)
        val displayFormatter = DateTimeFormatter.ofPattern("h:mm a")
        return time.format(displayFormatter)
    }
}
