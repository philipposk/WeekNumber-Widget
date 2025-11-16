package com.weeknumber.widget

import android.content.Context
import java.util.Calendar
import com.weeknumber.widget.widget.WidgetPreferences
import android.appwidget.AppWidgetManager

object WeekNumberCalculator {
    /**
     * Calculates the current week number
     * @param weekStart "monday" for ISO 8601 (Monday start, week 1 = first week with 4+ days)
     *                  "sunday" for US/Canada style (Sunday start, week 1 contains Jan 1)
     */
    fun getCurrentWeekNumber(context: Context? = null, widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): Int {
        val calendar = Calendar.getInstance()
        
        // Get week start preference if context and widgetId provided
        val weekStart = if (context != null && widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            WidgetPreferences.getWeekStart(context, widgetId)
        } else {
            "monday" // Default to Monday
        }
        
        if (weekStart == "sunday") {
            // US/Canada style: Sunday start, week 1 contains January 1
            calendar.firstDayOfWeek = Calendar.SUNDAY
            calendar.minimalDaysInFirstWeek = 1
        } else {
            // ISO 8601: Monday start, week 1 is first week with at least 4 days
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.minimalDaysInFirstWeek = 4
        }
        
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Gets the current year
     */
    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    /**
     * Formats the week number as a string
     */
    fun getFormattedWeekNumber(context: Context? = null, widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): String {
        return "Week ${getCurrentWeekNumber(context, widgetId)}"
    }
}

