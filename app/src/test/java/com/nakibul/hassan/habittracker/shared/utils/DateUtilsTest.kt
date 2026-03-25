package com.nakibul.hassan.habittracker.shared.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class DateUtilsTest {

    // ==================== getCurrentDate Tests ====================

    @Test
    fun `getCurrentDate returns date in correct format`() {
        val result = DateUtils.getCurrentDate()
        
        // Should match yyyy-MM-dd format
        assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `getCurrentDate returns today's date`() {
        val result = DateUtils.getCurrentDate()
        val expected = LocalDate.now().toString()
        
        assertEquals(expected, result)
    }

    // ==================== getCurrentDateTime Tests ====================

    @Test
    fun `getCurrentDateTime returns datetime in ISO format`() {
        val result = DateUtils.getCurrentDateTime()
        
        // Should contain date and time parts
        assertTrue(result.contains("T"))
        assertTrue(result.length >= 19) // yyyy-MM-ddTHH:mm:ss
    }

    // ==================== getCurrentTime Tests ====================

    @Test
    fun `getCurrentTime returns time in HHmm format`() {
        val result = DateUtils.getCurrentTime()
        
        // Should match HH:mm format
        assertTrue(result.matches(Regex("\\d{2}:\\d{2}")))
    }

    // ==================== formatDate Tests ====================

    @Test
    fun `formatDate formats LocalDate correctly`() {
        val date = LocalDate.of(2024, 1, 15)
        val result = DateUtils.formatDate(date)
        
        assertEquals("2024-01-15", result)
    }

    @Test
    fun `formatDate handles single digit month and day`() {
        val date = LocalDate.of(2024, 1, 5)
        val result = DateUtils.formatDate(date)
        
        assertEquals("2024-01-05", result)
    }

    // ==================== parseDate Tests ====================

    @Test
    fun `parseDate parses valid date string`() {
        val dateString = "2024-01-15"
        val result = DateUtils.parseDate(dateString)
        
        assertEquals(2024, result.year)
        assertEquals(1, result.monthValue)
        assertEquals(15, result.dayOfMonth)
    }

    @Test
    fun `parseDate handles leap year`() {
        val dateString = "2024-02-29"
        val result = DateUtils.parseDate(dateString)
        
        assertEquals(29, result.dayOfMonth)
    }

    @Test(expected = Exception::class)
    fun `parseDate throws exception for invalid date`() {
        DateUtils.parseDate("invalid-date")
    }

    // ==================== formatForDisplay Tests ====================

    @Test
    fun `formatForDisplay formats date for user display`() {
        val dateString = "2024-01-15"
        val result = DateUtils.formatForDisplay(dateString)
        
        assertTrue(result.contains("Jan"))
        assertTrue(result.contains("15"))
        assertTrue(result.contains("2024"))
    }

    // ==================== formatMonthYear Tests ====================

    @Test
    fun `formatMonthYear formats date to month year`() {
        val date = LocalDate.of(2024, 1, 15)
        val result = DateUtils.formatMonthYear(date)
        
        assertTrue(result.contains("January"))
        assertTrue(result.contains("2024"))
    }

    // ==================== formatDayOfWeek Tests ====================

    @Test
    fun `formatDayOfWeek returns abbreviated day name`() {
        val monday = LocalDate.of(2024, 1, 15) // Monday
        val result = DateUtils.formatDayOfWeek(monday)
        
        assertTrue(result.contains("Mon"))
    }

    // ==================== getDateMinusDays Tests ====================

    @Test
    fun `getDateMinusDays returns correct past date`() {
        val daysAgo = 7L
        val result = DateUtils.getDateMinusDays(daysAgo)
        val expected = LocalDate.now().minusDays(daysAgo).toString()
        
        assertEquals(expected, result)
    }

    @Test
    fun `getDateMinusDays with zero returns today`() {
        val result = DateUtils.getDateMinusDays(0)
        val expected = DateUtils.getCurrentDate()
        
        assertEquals(expected, result)
    }

    // ==================== getDatePlusDays Tests ====================

    @Test
    fun `getDatePlusDays returns correct future date`() {
        val daysAhead = 7L
        val result = DateUtils.getDatePlusDays(daysAhead)
        val expected = LocalDate.now().plusDays(daysAhead).toString()
        
        assertEquals(expected, result)
    }

    @Test
    fun `getDatePlusDays with zero returns today`() {
        val result = DateUtils.getDatePlusDays(0)
        val expected = DateUtils.getCurrentDate()
        
        assertEquals(expected, result)
    }

    // ==================== getDaysBetween Tests ====================

    @Test
    fun `getDaysBetween returns correct difference`() {
        val startDate = "2024-01-01"
        val endDate = "2024-01-10"
        val result = DateUtils.getDaysBetween(startDate, endDate)
        
        assertEquals(9, result)
    }

    @Test
    fun `getDaysBetween returns zero for same date`() {
        val date = "2024-01-15"
        val result = DateUtils.getDaysBetween(date, date)
        
        assertEquals(0, result)
    }

    @Test
    fun `getDaysBetween returns negative for reversed dates`() {
        val startDate = "2024-01-10"
        val endDate = "2024-01-01"
        val result = DateUtils.getDaysBetween(startDate, endDate)
        
        assertEquals(-9, result)
    }

    // ==================== isToday Tests ====================

    @Test
    fun `isToday returns true for current date`() {
        val today = DateUtils.getCurrentDate()
        
        assertTrue(DateUtils.isToday(today))
    }

    @Test
    fun `isToday returns false for past date`() {
        val pastDate = "2020-01-01"
        
        assertFalse(DateUtils.isToday(pastDate))
    }

    @Test
    fun `isToday returns false for future date`() {
        val futureDate = "2099-01-01"
        
        assertFalse(DateUtils.isToday(futureDate))
    }

    // ==================== isYesterday Tests ====================

    @Test
    fun `isYesterday returns true for yesterday's date`() {
        val yesterday = DateUtils.getDateMinusDays(1)
        
        assertTrue(DateUtils.isYesterday(yesterday))
    }

    @Test
    fun `isYesterday returns false for today`() {
        val today = DateUtils.getCurrentDate()
        
        assertFalse(DateUtils.isYesterday(today))
    }

    @Test
    fun `isYesterday returns false for two days ago`() {
        val twoDaysAgo = DateUtils.getDateMinusDays(2)
        
        assertFalse(DateUtils.isYesterday(twoDaysAgo))
    }

    // ==================== getDayOfWeek Tests ====================

    @Test
    fun `getDayOfWeek returns 1 for Monday`() {
        val monday = "2024-01-15" // This is a Monday
        val result = DateUtils.getDayOfWeek(monday)
        
        assertEquals(1, result)
    }

    @Test
    fun `getDayOfWeek returns 7 for Sunday`() {
        val sunday = "2024-01-14" // This is a Sunday
        val result = DateUtils.getDayOfWeek(sunday)
        
        assertEquals(7, result)
    }

    @Test
    fun `getDayOfWeek returns correct value for each day`() {
        // Week of Jan 15, 2024
        assertEquals(1, DateUtils.getDayOfWeek("2024-01-15")) // Monday
        assertEquals(2, DateUtils.getDayOfWeek("2024-01-16")) // Tuesday
        assertEquals(3, DateUtils.getDayOfWeek("2024-01-17")) // Wednesday
        assertEquals(4, DateUtils.getDayOfWeek("2024-01-18")) // Thursday
        assertEquals(5, DateUtils.getDayOfWeek("2024-01-19")) // Friday
        assertEquals(6, DateUtils.getDayOfWeek("2024-01-20")) // Saturday
        assertEquals(7, DateUtils.getDayOfWeek("2024-01-21")) // Sunday
    }

    // ==================== getStartOfWeek Tests ====================

    @Test
    fun `getStartOfWeek returns Monday`() {
        val wednesday = LocalDate.of(2024, 1, 17)
        val result = DateUtils.getStartOfWeek(wednesday)
        
        assertEquals(LocalDate.of(2024, 1, 15), result) // Monday
    }

    @Test
    fun `getStartOfWeek returns same date for Monday`() {
        val monday = LocalDate.of(2024, 1, 15)
        val result = DateUtils.getStartOfWeek(monday)
        
        assertEquals(monday, result)
    }

    // ==================== getStartOfMonth Tests ====================

    @Test
    fun `getStartOfMonth returns first day of month`() {
        val midMonth = LocalDate.of(2024, 1, 15)
        val result = DateUtils.getStartOfMonth(midMonth)
        
        assertEquals(LocalDate.of(2024, 1, 1), result)
    }

    @Test
    fun `getStartOfMonth returns same date for first day`() {
        val firstDay = LocalDate.of(2024, 1, 1)
        val result = DateUtils.getStartOfMonth(firstDay)
        
        assertEquals(firstDay, result)
    }

    // ==================== getEndOfMonth Tests ====================

    @Test
    fun `getEndOfMonth returns last day of month`() {
        val midMonth = LocalDate.of(2024, 1, 15)
        val result = DateUtils.getEndOfMonth(midMonth)
        
        assertEquals(LocalDate.of(2024, 1, 31), result)
    }

    @Test
    fun `getEndOfMonth handles February in leap year`() {
        val february = LocalDate.of(2024, 2, 15)
        val result = DateUtils.getEndOfMonth(february)
        
        assertEquals(LocalDate.of(2024, 2, 29), result)
    }

    @Test
    fun `getEndOfMonth handles February in non-leap year`() {
        val february = LocalDate.of(2023, 2, 15)
        val result = DateUtils.getEndOfMonth(february)
        
        assertEquals(LocalDate.of(2023, 2, 28), result)
    }

    // ==================== getDatesInRange Tests ====================

    @Test
    fun `getDatesInRange returns all dates inclusive`() {
        val startDate = "2024-01-01"
        val endDate = "2024-01-05"
        val result = DateUtils.getDatesInRange(startDate, endDate)
        
        assertEquals(5, result.size)
        assertEquals("2024-01-01", result.first())
        assertEquals("2024-01-05", result.last())
    }

    @Test
    fun `getDatesInRange returns single date for same start and end`() {
        val date = "2024-01-15"
        val result = DateUtils.getDatesInRange(date, date)
        
        assertEquals(1, result.size)
        assertEquals(date, result.first())
    }

    @Test
    fun `getDatesInRange returns dates in order`() {
        val startDate = "2024-01-01"
        val endDate = "2024-01-03"
        val result = DateUtils.getDatesInRange(startDate, endDate)
        
        assertEquals(listOf("2024-01-01", "2024-01-02", "2024-01-03"), result)
    }

    // ==================== getGreeting Tests ====================

    @Test
    fun `getGreeting returns valid greeting`() {
        val result = DateUtils.getGreeting()
        
        assertTrue(
            result == "Good Morning" || 
            result == "Good Afternoon" || 
            result == "Good Evening"
        )
    }

    // ==================== parseTime Tests ====================

    @Test
    fun `parseTime parses valid time string`() {
        val timeString = "14:30"
        val result = DateUtils.parseTime(timeString)
        
        assertEquals(14, result.hour)
        assertEquals(30, result.minute)
    }

    @Test
    fun `parseTime handles midnight`() {
        val timeString = "00:00"
        val result = DateUtils.parseTime(timeString)
        
        assertEquals(0, result.hour)
        assertEquals(0, result.minute)
    }

    @Test
    fun `parseTime handles end of day`() {
        val timeString = "23:59"
        val result = DateUtils.parseTime(timeString)
        
        assertEquals(23, result.hour)
        assertEquals(59, result.minute)
    }

    // ==================== formatTime Tests ====================

    @Test
    fun `formatTime formats LocalTime correctly`() {
        val time = LocalTime.of(14, 30)
        val result = DateUtils.formatTime(time)
        
        assertEquals("14:30", result)
    }

    @Test
    fun `formatTime includes leading zeros`() {
        val time = LocalTime.of(9, 5)
        val result = DateUtils.formatTime(time)
        
        assertEquals("09:05", result)
    }

    // ==================== formatTimeForDisplay Tests ====================

    @Test
    fun `formatTimeForDisplay formats time with AM PM`() {
        val timeString = "14:30"
        val result = DateUtils.formatTimeForDisplay(timeString)
        
        assertTrue(result.contains("PM") || result.contains("pm"))
        assertTrue(result.contains("2:30") || result.contains("2.30"))
    }

    @Test
    fun `formatTimeForDisplay handles morning time`() {
        val timeString = "09:30"
        val result = DateUtils.formatTimeForDisplay(timeString)
        
        assertTrue(result.contains("AM") || result.contains("am"))
    }

    @Test
    fun `formatTimeForDisplay handles noon`() {
        val timeString = "12:00"
        val result = DateUtils.formatTimeForDisplay(timeString)
        
        assertTrue(result.contains("PM") || result.contains("pm"))
    }

    @Test
    fun `formatTimeForDisplay handles midnight`() {
        val timeString = "00:00"
        val result = DateUtils.formatTimeForDisplay(timeString)
        
        assertTrue(result.contains("AM") || result.contains("am"))
        assertTrue(result.contains("12"))
    }
}
