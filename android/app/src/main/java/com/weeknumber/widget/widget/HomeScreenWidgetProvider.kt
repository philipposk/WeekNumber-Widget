package com.weeknumber.widget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import com.weeknumber.widget.R
import com.weeknumber.widget.WeekNumberCalculator

class HomeScreenWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) updateAppWidget(context, appWidgetManager, id)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle
    ) {
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (id in appWidgetIds) WidgetPreferences.deletePreferences(context, id)
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val weekNumber = WeekNumberCalculator.getCurrentWeekNumber(context, appWidgetId)
            val showLabel = WidgetPreferences.getLabelSizeOption(context, appWidgetId) != "no_title"
            val size = WidgetSizing.readSize(appWidgetManager, appWidgetId)
            val fonts = WidgetSizing.compute(size, weekNumber, showLabel)

            val views = RemoteViews(context.packageName, fonts.layoutId)
            views.setTextViewText(R.id.widget_week_number, fonts.numberText)
            views.setTextViewText(R.id.widget_label, WidgetPreferences.getWeekLabel(context, appWidgetId))

            applyColors(context, views, appWidgetId, showLabel)

            views.setTextViewTextSize(R.id.widget_week_number, TypedValue.COMPLEX_UNIT_SP, fonts.numberSp)
            if (showLabel) {
                views.setViewVisibility(R.id.widget_label, View.VISIBLE)
                views.setTextViewTextSize(R.id.widget_label, TypedValue.COMPLEX_UNIT_SP, fonts.labelSp)
            } else {
                views.setViewVisibility(R.id.widget_label, View.GONE)
            }

            views.setOnClickPendingIntent(R.id.widget_background, configPendingIntent(context, appWidgetId))
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun applyColors(context: Context, views: RemoteViews, appWidgetId: Int, showLabel: Boolean) {
            val bg = parseBackground(WidgetPreferences.getBackgroundColor(context, appWidgetId))
            val text = parseTextColor(context, WidgetPreferences.getTextColor(context, appWidgetId))
            views.setInt(R.id.widget_background, "setBackgroundColor", bg)
            views.setTextColor(R.id.widget_week_number, text)
            if (showLabel) views.setTextColor(R.id.widget_label, text)
        }

        private fun parseBackground(color: String): Int {
            return try {
                val isTransparent = color == "#00000000" ||
                    (color.length >= 8 && color.substring(1, 3).toIntOrNull(16) == 0)
                if (isTransparent) Color.TRANSPARENT else Color.parseColor(color)
            } catch (_: IllegalArgumentException) {
                Color.TRANSPARENT
            }
        }

        private fun parseTextColor(context: Context, color: String): Int {
            return try {
                Color.parseColor(color)
            } catch (_: IllegalArgumentException) {
                Color.parseColor(WidgetPreferences.getThemeAwareTextColor(context))
            }
        }

        private fun configPendingIntent(context: Context, appWidgetId: Int): PendingIntent {
            val intent = Intent(context, WidgetConfigureActivity::class.java).apply {
                action = WidgetConfigureActivity.ACTION_RECONFIGURE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val requestCode = appWidgetId and 0x7FFFFFFF
            return PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
