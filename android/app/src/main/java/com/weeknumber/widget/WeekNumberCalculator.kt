package com.weeknumber.widget

import android.content.Context
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import com.weeknumber.widget.widget.WidgetPreferences
import android.appwidget.AppWidgetManager

object WeekNumberCalculator {

    /**
     * Build a proleptic-Gregorian calendar in the device time zone.
     * Using an explicit Gregorian calendar (instead of Calendar.getInstance(),
     * which honours the locale's calendar type) prevents week numbers from
     * diverging in Thai (Buddhist) or Saudi (Umm-al-Qura) locales.
     */
    private fun gregorian(weekStart: String): Calendar {
        val calendar = GregorianCalendar(Locale.US)
        if (weekStart == "sunday") {
            // US/Canada style: Sunday start, week 1 contains January 1
            calendar.firstDayOfWeek = Calendar.SUNDAY
            calendar.minimalDaysInFirstWeek = 1
        } else {
            // ISO 8601: Monday start, week 1 is first week with at least 4 days
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.minimalDaysInFirstWeek = 4
        }
        return calendar
    }

    private fun resolveWeekStart(context: Context?, widgetId: Int): String {
        return if (context != null && widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            WidgetPreferences.getWeekStart(context, widgetId)
        } else {
            "monday"
        }
    }

    /**
     * Calculates the current week number
     * @param weekStart "monday" for ISO 8601 (Monday start, week 1 = first week with 4+ days)
     *                  "sunday" for US/Canada style (Sunday start, week 1 contains Jan 1)
     */
    fun getCurrentWeekNumber(context: Context? = null, widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): Int {
        return gregorian(resolveWeekStart(context, widgetId)).get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Gets the current year
     */
    fun getCurrentYear(): Int {
        return GregorianCalendar(Locale.US).get(Calendar.YEAR)
    }

    /**
     * Formats the week number as a string
     */
    fun getFormattedWeekNumber(context: Context? = null, widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): String {
        return "Week ${getCurrentWeekNumber(context, widgetId)}"
    }

    /** Days remaining in the current calendar year, including today. */
    fun daysRemainingInYear(): Int {
        val now = GregorianCalendar(Locale.US)
        val year = now.get(Calendar.YEAR)
        val dayOfYear = now.get(Calendar.DAY_OF_YEAR)
        val lastDay = GregorianCalendar(Locale.US).apply { set(year, Calendar.DECEMBER, 31) }
            .get(Calendar.DAY_OF_YEAR)
        return (lastDay - dayOfYear).coerceAtLeast(0)
    }

    /** Year progress as a percentage 0..100. */
    fun yearProgressPercent(): Int {
        val now = GregorianCalendar(Locale.US)
        val year = now.get(Calendar.YEAR)
        val dayOfYear = now.get(Calendar.DAY_OF_YEAR)
        val totalDays = GregorianCalendar(Locale.US).apply { set(year, Calendar.DECEMBER, 31) }
            .get(Calendar.DAY_OF_YEAR)
        return ((dayOfYear.toFloat() / totalDays) * 100f).toInt().coerceIn(0, 100)
    }

    /** First and last day-of-month labels for the current week, e.g. "Jun 9 – Jun 15". */
    fun weekRangeLabel(context: Context? = null, widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): String {
        val weekStart = resolveWeekStart(context, widgetId)
        val cal = gregorian(weekStart)
        val firstDow = if (weekStart == "sunday") Calendar.SUNDAY else Calendar.MONDAY
        // Move back to the first day of this week.
        while (cal.get(Calendar.DAY_OF_WEEK) != firstDow) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        val fmt = java.text.SimpleDateFormat("MMM d", Locale.getDefault())
        val start = fmt.format(cal.time)
        cal.add(Calendar.DAY_OF_MONTH, 6)
        val end = fmt.format(cal.time)
        return "$start – $end"
    }
}

