package com.weeknumber.widget

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class WeekNumberWidgetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Explicitly set the default night mode to follow system settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}
