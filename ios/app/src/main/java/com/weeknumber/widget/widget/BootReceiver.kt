package com.weeknumber.widget.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.weeknumber.widget.widget.HomeScreenWidgetProvider
import com.weeknumber.widget.widget.LockScreenWidgetProvider

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_PACKAGE_REPLACED
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            
            // Update home screen widgets
            val homeWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, HomeScreenWidgetProvider::class.java)
            )
            for (widgetId in homeWidgetIds) {
                HomeScreenWidgetProvider.updateAppWidget(
                    context,
                    appWidgetManager,
                    widgetId
                )
            }

            // Update lock screen widgets
            val lockWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, LockScreenWidgetProvider::class.java)
            )
            for (widgetId in lockWidgetIds) {
                LockScreenWidgetProvider.updateAppWidget(
                    context,
                    appWidgetManager,
                    widgetId
                )
            }
        }
    }
}

