package com.weeknumber.widget.widget

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration

object WidgetPreferences {
    private const val PREFS_NAME = "widget_preferences"
    
    // Default values - transparent background with theme-aware text
    private const val DEFAULT_BACKGROUND_COLOR = "#00000000" // Transparent
    private const val DEFAULT_TEXT_COLOR = "#FF6200EE" // Will be overridden by theme-aware color
    private const val DEFAULT_LABEL_COLOR = "#757575"
    private const val DEFAULT_FONT_SIZE = 48
    private const val DEFAULT_LABEL_SIZE_OPTION = "small"  // Default: show title (small means visible)
    private const val DEFAULT_WEEK_START = "monday"  // Default: Monday (ISO 8601) or "sunday"
    private const val DEFAULT_FONT_FAMILY = "sans-serif"
    private const val DEFAULT_TEXT_STYLE = "normal" // normal, bold, italic, bold_italic
    
    fun getPrefs(context: Context, widgetId: Int): SharedPreferences {
        return context.getSharedPreferences("${PREFS_NAME}_$widgetId", Context.MODE_PRIVATE)
    }
    
    // Background color
    fun getBackgroundColor(context: Context, widgetId: Int): String {
        return getPrefs(context, widgetId).getString("background_color", DEFAULT_BACKGROUND_COLOR) ?: DEFAULT_BACKGROUND_COLOR
    }
    
    fun setBackgroundColor(context: Context, widgetId: Int, color: String) {
        getPrefs(context, widgetId).edit().putString("background_color", color).apply()
        // If background is transparent, automatically update text color to be theme-aware
        if (color == "#00000000" || (color.length >= 8 && color.substring(1, 3).toIntOrNull(16) == 0)) {
            val themeAwareColor = getThemeAwareTextColor(context)
            // Only set if text color hasn't been manually set
            if (getPrefs(context, widgetId).getString("text_color", null) == null) {
                setTextColor(context, widgetId, themeAwareColor)
            }
        }
    }
    
    // Text color (week number)
    fun getTextColor(context: Context, widgetId: Int): String {
        val prefs = getPrefs(context, widgetId)
        // If no preferences exist at all, use system color
        if (!prefs.contains("text_color") && prefs.all.isEmpty()) {
            return getThemeAwareTextColor(context)
        }
        val savedColor = prefs.getString("text_color", null)
        if (savedColor != null) {
            return savedColor
        }
        // Default: use theme-aware color (opposite of system theme)
        return getThemeAwareTextColor(context)
    }
    
    fun setTextColor(context: Context, widgetId: Int, color: String) {
        getPrefs(context, widgetId).edit().putString("text_color", color).apply()
    }
    
    // Detect system theme and return opposite text color
    fun getThemeAwareTextColor(context: Context): String {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // Dark theme detected - return light color (white)
                "#FFFFFFFF"
            }
            else -> {
                // Light theme detected - return dark color (black)
                "#FF000000"
            }
        }
    }
    
    // Check if background is transparent
    fun isBackgroundTransparent(context: Context, widgetId: Int): Boolean {
        val bgColor = getBackgroundColor(context, widgetId)
        // Check if alpha is 0 or color is transparent
        return bgColor == "#00000000" || bgColor == "#00FFFFFF" || 
               bgColor.length >= 8 && bgColor.substring(1, 3).toIntOrNull(16) == 0
    }
    
    // Label color ("Week" text)
    fun getLabelColor(context: Context, widgetId: Int): String {
        return getPrefs(context, widgetId).getString("label_color", DEFAULT_LABEL_COLOR) ?: DEFAULT_LABEL_COLOR
    }
    
    fun setLabelColor(context: Context, widgetId: Int, color: String) {
        getPrefs(context, widgetId).edit().putString("label_color", color).apply()
    }
    
    // Font size
    fun getFontSize(context: Context, widgetId: Int): Int {
        return getPrefs(context, widgetId).getInt("font_size", DEFAULT_FONT_SIZE)
    }
    
    fun setFontSize(context: Context, widgetId: Int, size: Int) {
        getPrefs(context, widgetId).edit().putInt("font_size", size).apply()
    }
    
    // Label size option: "no_title", "small", "big"
    fun getLabelSizeOption(context: Context, widgetId: Int): String {
        val prefs = getPrefs(context, widgetId)
        // If no preferences exist, default to showing title
        if (!prefs.contains("label_size_option") && prefs.all.isEmpty()) {
            return "small" // Show title by default
        }
        return prefs.getString("label_size_option", DEFAULT_LABEL_SIZE_OPTION) ?: DEFAULT_LABEL_SIZE_OPTION
    }
    
    fun setLabelSizeOption(context: Context, widgetId: Int, option: String) {
        getPrefs(context, widgetId).edit().putString("label_size_option", option).apply()
    }
    
    // Week start: "monday" or "sunday"
    fun getWeekStart(context: Context, widgetId: Int): String {
        val prefs = getPrefs(context, widgetId)
        // If no preferences exist, default to Monday
        if (!prefs.contains("week_start") && prefs.all.isEmpty()) {
            return DEFAULT_WEEK_START
        }
        return prefs.getString("week_start", DEFAULT_WEEK_START) ?: DEFAULT_WEEK_START
    }
    
    fun setWeekStart(context: Context, widgetId: Int, weekStart: String) {
        getPrefs(context, widgetId).edit().putString("week_start", weekStart).apply()
    }
    
    // Font family
    fun getFontFamily(context: Context, widgetId: Int): String {
        return getPrefs(context, widgetId).getString("font_family", DEFAULT_FONT_FAMILY) ?: DEFAULT_FONT_FAMILY
    }
    
    fun setFontFamily(context: Context, widgetId: Int, family: String) {
        getPrefs(context, widgetId).edit().putString("font_family", family).apply()
    }
    
    // Text style
    fun getTextStyle(context: Context, widgetId: Int): String {
        return getPrefs(context, widgetId).getString("text_style", DEFAULT_TEXT_STYLE) ?: DEFAULT_TEXT_STYLE
    }
    
    fun setTextStyle(context: Context, widgetId: Int, style: String) {
        getPrefs(context, widgetId).edit().putString("text_style", style).apply()
    }
    
    // Widget size preset
    fun getWidgetSize(context: Context, widgetId: Int): String {
        return getPrefs(context, widgetId).getString("widget_size", "medium") ?: "medium"
    }
    
    fun setWidgetSize(context: Context, widgetId: Int, size: String) {
        getPrefs(context, widgetId).edit().putString("widget_size", size).apply()
    }
    
    // Get the system locale language code
    private fun getSystemLanguageCode(context: Context): String {
        val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        return locale.language
    }
    
    // Get the translated word for "Week" based on system locale
    fun getWeekLabel(context: Context, widgetId: Int): String {
        val language = getSystemLanguageCode(context)
        return getWeekLabelForLanguage(language)
    }
    
    fun getWeekLabelForLanguage(language: String): String {
        return when (language) {
            "en" -> "Week"
            "es" -> "Semana"
            "fr" -> "Semaine"
            "de" -> "Woche"
            "it" -> "Settimana"
            "pt" -> "Semana"
            "nl" -> "Week"
            "ru" -> "Неделя"
            "zh" -> "周"
            "ja" -> "週"
            "ko" -> "주"
            "ar" -> "أسبوع"
            else -> "Week"
        }
    }
    
    fun deletePreferences(context: Context, widgetId: Int) {
        getPrefs(context, widgetId).edit().clear().apply()
    }
}

