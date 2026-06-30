package com.weeknumber.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeekNumberCalculatorTest {

    @Test
    fun `default returns plausible ISO week 1-53`() {
        val w = WeekNumberCalculator.getCurrentWeekNumber()
        assertTrue("week in 1..53 but was $w", w in 1..53)
    }

    @Test
    fun `formatted label has Week prefix`() {
        val s = WeekNumberCalculator.getFormattedWeekNumber()
        assertTrue(s.startsWith("Week "))
    }

    @Test
    fun `current year is in plausible range`() {
        val y = WeekNumberCalculator.getCurrentYear()
        assertTrue("year sane", y in 2020..2100)
    }

    // ISO 8601 (Monday start)

    @Test
    fun isoFirstWeekOf2024() {
        assertEquals(1, WeekNumberCalculator.weekNumberAt("monday", 2024, 1, 1))
    }

    @Test
    fun isoWeek1SpansYearBoundary() {
        assertEquals(53, WeekNumberCalculator.weekNumberAt("monday", 2021, 1, 1))
    }

    @Test
    fun isoMidYear() {
        assertEquals(25, WeekNumberCalculator.weekNumberAt("monday", 2026, 6, 15))
    }

    // US (Sunday start)

    @Test
    fun usFirstWeekContainsJan1() {
        assertEquals(1, WeekNumberCalculator.weekNumberAt("sunday", 2024, 1, 1))
    }

    @Test
    fun usJan1IsAlwaysWeek1() {
        assertEquals(1, WeekNumberCalculator.weekNumberAt("sunday", 2021, 1, 1))
    }
}
