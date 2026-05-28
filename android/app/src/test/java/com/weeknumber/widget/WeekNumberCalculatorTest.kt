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
}
