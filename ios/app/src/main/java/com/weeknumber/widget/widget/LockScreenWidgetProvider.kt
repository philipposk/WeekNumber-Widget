package com.weeknumber.widget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.weeknumber.widget.R
import com.weeknumber.widget.WeekNumberCalculator

class LockScreenWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Update widget when first enabled
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, LockScreenWidgetProvider::class.java)
        )
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val weekNumber = WeekNumberCalculator.getCurrentWeekNumber(context, appWidgetId)
            val views = RemoteViews(context.packageName, R.layout.widget_lock_screen)

            views.setTextViewText(R.id.widget_week_number, weekNumber.toString())
            views.setTextViewText(R.id.widget_label, WidgetPreferences.getWeekLabel(context, appWidgetId))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

