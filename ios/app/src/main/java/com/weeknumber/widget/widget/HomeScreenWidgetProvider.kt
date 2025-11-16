package com.weeknumber.widget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.weeknumber.widget.R
import com.weeknumber.widget.WeekNumberCalculator

class HomeScreenWidgetProvider : AppWidgetProvider() {

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
            ComponentName(context, HomeScreenWidgetProvider::class.java)
        )
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle
    ) {
        // Update widget when size changes
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            try {
                val weekNumber = WeekNumberCalculator.getCurrentWeekNumber(context, appWidgetId)
                
                // Determine which layout to use based on widget size
                // Get actual widget dimensions to choose appropriate layout and calculate scale
                var minWidth = 180 // Default to medium size
                var minHeight = 180
                val layoutId = try {
                    val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
                    minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                    minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                    
                    // Choose layout based on actual size (in dp, approximately)
                    when {
                        // Very small widget (1x1 cell = ~70dp)
                        minWidth <= 85 && minHeight <= 85 -> R.layout.widget_home_screen_compact
                        // Small widget (2x1 or 1x2 = ~110dp)
                        minWidth <= 130 || minHeight <= 130 -> R.layout.widget_home_screen_small
                        // Large widget
                        minWidth > 200 || minHeight > 200 -> R.layout.widget_home_screen_large
                        // Extra large widget
                        minWidth > 300 || minHeight > 300 -> R.layout.widget_home_screen_extra_large
                        // Medium (default)
                        else -> R.layout.widget_home_screen
                    }
                } catch (e: Exception) {
                    // Fallback to medium layout if size detection fails
                    R.layout.widget_home_screen
                }
                
                val views = RemoteViews(context.packageName, layoutId)

                // Determine if widget is tall/narrow (height > width * 1.3)
                val isTall = minHeight > minWidth * 1.3f
                
                // Format week number - if tall, display digits vertically
                val weekNumberText = if (isTall && weekNumber.toString().length > 1) {
                    // Display digits vertically (one under another)
                    weekNumber.toString().toCharArray().joinToString("\n")
                } else {
                    // Display normally
                    weekNumber.toString()
                }

                // Set text
                views.setTextViewText(R.id.widget_week_number, weekNumberText)
                
                val showLabel = true
                
                // Set label text (always set it, visibility is handled later)
                views.setTextViewText(R.id.widget_label, WidgetPreferences.getWeekLabel(context, appWidgetId))

                // Apply colors with error handling
                try {
                    val bgColorStr = WidgetPreferences.getBackgroundColor(context, appWidgetId)
                    val backgroundColor = if (bgColorStr == "#00000000" || 
                        (bgColorStr.length >= 8 && bgColorStr.substring(1, 3).toIntOrNull(16) == 0)) {
                        android.graphics.Color.TRANSPARENT
                    } else {
                        android.graphics.Color.parseColor(bgColorStr)
                    }
                    val textColor = android.graphics.Color.parseColor(
                        WidgetPreferences.getTextColor(context, appWidgetId)
                    )
                    
                    // Apply background - check if it's Danish flag
                    val danishFlagRed = android.graphics.Color.parseColor("#C8102E")
                    if (backgroundColor == danishFlagRed) {
                        // Set Danish flag drawable as background
                        views.setInt(R.id.widget_background, "setBackgroundResource", R.drawable.danish_flag_background)
                    } else if (backgroundColor == android.graphics.Color.TRANSPARENT) {
                        views.setInt(R.id.widget_background, "setBackgroundColor", android.graphics.Color.TRANSPARENT)
                    } else {
                        views.setInt(R.id.widget_background, "setBackgroundColor", backgroundColor)
                    }
                    views.setTextColor(R.id.widget_week_number, textColor)
                    // Label uses same color as text
                    if (showLabel) {
                        views.setTextColor(R.id.widget_label, textColor)
                    }
                } catch (e: IllegalArgumentException) {
                    // Use default theme-aware colors if parsing fails
                    val defaultBg = android.graphics.Color.TRANSPARENT
                    val defaultText = android.graphics.Color.parseColor(WidgetPreferences.getThemeAwareTextColor(context))
                    views.setInt(R.id.widget_background, "setBackgroundColor", defaultBg)
                    views.setTextColor(R.id.widget_week_number, defaultText)
                    // Label uses same color as text
                    if (showLabel) {
                        views.setTextColor(R.id.widget_label, defaultText)
                    }
                }
                
                // Calculate font sizes to ensure text fits within widget bounds
                // Widget dimensions from getAppWidgetOptions are already in dp
                // Account for padding: layout padding (4dp on each side = 8dp) + minimal safety margin
                val layoutPadding = 8f // 4dp on each side from layout
                val safetyMargin = 4f // Reduced safety margin to maximize number size while preventing clipping
                val totalPadding = layoutPadding + safetyMargin
                
                val availableWidth = (minWidth - totalPadding).coerceAtLeast(1f)
                
                // Allocate height based on whether label is shown
                // Reserve space for label - use 18% for label to ensure it's always visible
                val labelHeightRatio = if (showLabel) 0.18f else 0f
                
                // Calculate available height for label
                val availableHeightForLabel = if (showLabel) {
                    (minHeight * labelHeightRatio - layoutPadding).coerceAtLeast(8f) // Minimum 8dp for label
                } else {
                    0f
                }
                
                // Account for TextView's internal padding - use less padding for larger numbers
                // Reduce spacing between label and number (from 2f to 0f since layout handles it)
                val availableHeightForNumber = (minHeight * (1f - labelHeightRatio) - totalPadding).coerceAtLeast(1f)
                
                // Calculate font size based on both width and height constraints
                // For the number, we need to fit it within both width and height
                // Use optimized multipliers to maximize size while preventing clipping
                val weekNumberStr = weekNumberText.replace("\n", "") // Remove newlines for width calculation
                val digitCount = weekNumberStr.length
                
                // Calculate max font size based on width
                // Use 0.65 multiplier for digit width (balanced between size and safety)
                // For bold numbers, digits are approximately 0.65-0.7x font size wide
                val digitWidthMultiplier = 0.65f
                val maxFontSizeByWidth = (availableWidth / (digitCount * digitWidthMultiplier)) * 0.95f // 95% to maximize size
                
                // Calculate max font size based on height
                // For vertical layout (tall widgets), each line needs its own height
                val lines = if (isTall && weekNumber.toString().length > 1) weekNumber.toString().length else 1
                // Font height is approximately 1.0-1.2x font size including ascent/descent
                // Use 0.85 multiplier to maximize size while ensuring text fits
                val maxFontSizeByHeight = (availableHeightForNumber / lines) * 0.85f
                
                // Calculate maximum safe font size - use the smaller of the two constraints
                // Always use calculated size (no user override) to prevent clipping
                val finalFontSize = minOf(maxFontSizeByWidth, maxFontSizeByHeight).coerceIn(16f, 500f)
                
                // Calculate label size - make it larger and more visible
                val finalLabelSize = if (showLabel) {
                    // Calculate based on both proportional size and available height
                    // Use 0.35f (35% of number size) for better visibility
                    val proportionalSize = finalFontSize * 0.35f
                    
                    // Also consider available height for label (ensure it fits)
                    val maxSizeByHeight = availableHeightForLabel * 0.85f // 85% of available height
                    
                    // Use the smaller of proportional size or height-based size, but ensure minimum
                    val calculatedSize = minOf(proportionalSize, maxSizeByHeight)
                    
                    // Ensure minimum size of 12sp for readability in all widget sizes
                    // Maximum is 50% of number size or 200sp, whichever is smaller
                    calculatedSize.coerceIn(12f, minOf(finalFontSize * 0.5f, 200f))
                } else {
                    0f // Not used when label is hidden
                }
                
                // Apply the font sizes (always calculated, no user override for numbers)
                views.setTextViewTextSize(R.id.widget_week_number, android.util.TypedValue.COMPLEX_UNIT_SP, finalFontSize)
                
                // Handle label visibility and size
                if (showLabel) {
                    views.setViewVisibility(R.id.widget_label, android.view.View.VISIBLE)
                    views.setTextViewTextSize(R.id.widget_label, android.util.TypedValue.COMPLEX_UNIT_SP, finalLabelSize)
                } else {
                    views.setViewVisibility(R.id.widget_label, android.view.View.GONE)
                }
                
                // Add click listener to open configuration when widget is tapped
                val configIntent = Intent(context, WidgetConfigureActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    // Use these flags to ensure activity opens properly
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                // Use unique request code for each widget to ensure PendingIntent works
                // Use a combination to avoid conflicts
                val requestCode = appWidgetId and 0x7FFFFFFF // Ensure positive
                val configPendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode,
                    configIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                // Set click on the root layout - this makes the entire widget clickable
                views.setOnClickPendingIntent(R.id.widget_background, configPendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                // Fallback: use default layout and colors
                val views = RemoteViews(context.packageName, R.layout.widget_home_screen)
                val weekNumber = WeekNumberCalculator.getCurrentWeekNumber(context, appWidgetId)
                
                // Check if widget is tall for vertical display
                var isTall = false
                try {
                    val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
                    val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                    val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                    isTall = minHeight > minWidth * 1.3f
                } catch (ex: Exception) {
                    // Use default
                }
                
                val weekNumberText = if (isTall && weekNumber.toString().length > 1) {
                    weekNumber.toString().toCharArray().joinToString("\n")
                } else {
                    weekNumber.toString()
                }
                
                views.setTextViewText(R.id.widget_week_number, weekNumberText)
                views.setTextViewText(R.id.widget_label, WidgetPreferences.getWeekLabel(context, appWidgetId))
                
                // Apply colors in fallback
                try {
                    val bgColorStr = WidgetPreferences.getBackgroundColor(context, appWidgetId)
                    val backgroundColor = if (bgColorStr == "#00000000" || 
                        (bgColorStr.length >= 8 && bgColorStr.substring(1, 3).toIntOrNull(16) == 0)) {
                        android.graphics.Color.TRANSPARENT
                    } else {
                        android.graphics.Color.parseColor(bgColorStr)
                    }
                    val textColor = android.graphics.Color.parseColor(
                        WidgetPreferences.getTextColor(context, appWidgetId)
                    )
                    
                    // Apply background - check if it's Danish flag
                    val danishFlagRed = android.graphics.Color.parseColor("#C8102E")
                    if (backgroundColor == danishFlagRed) {
                        // Set Danish flag drawable as background
                        views.setInt(R.id.widget_background, "setBackgroundResource", R.drawable.danish_flag_background)
                    } else if (backgroundColor == android.graphics.Color.TRANSPARENT) {
                        views.setInt(R.id.widget_background, "setBackgroundColor", android.graphics.Color.TRANSPARENT)
                    } else {
                        views.setInt(R.id.widget_background, "setBackgroundColor", backgroundColor)
                    }
                    views.setTextColor(R.id.widget_week_number, textColor)
                } catch (e: IllegalArgumentException) {
                    val defaultBg = android.graphics.Color.TRANSPARENT
                    val defaultText = android.graphics.Color.parseColor(WidgetPreferences.getThemeAwareTextColor(context))
                    views.setInt(R.id.widget_background, "setBackgroundColor", defaultBg)
                    views.setTextColor(R.id.widget_week_number, defaultText)
                }
                
                val showLabel = true
                
                // Apply basic font sizing to ensure text fits (simplified fallback)
                try {
                    val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
                    val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                    val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                    
                    val layoutPadding = 8f
                    val safetyMargin = 4f
                    val totalPadding = layoutPadding + safetyMargin
                    
                    val availableWidth = (minWidth - totalPadding).coerceAtLeast(1f)
                    
                    // Allocate height - label gets 10% when visible (layout weight 1:9)
                    val labelHeightRatio = if (showLabel) 0.10f else 0f
                    
                    val availableHeightForNumber = (minHeight * (1f - labelHeightRatio) - totalPadding - 2f).coerceAtLeast(1f)
                    
                    val weekNumberStr = weekNumberText.replace("\n", "")
                    val digitCount = weekNumberStr.length
                    val lines = if (minHeight > minWidth * 1.3f && weekNumber.toString().length > 1) weekNumber.toString().length else 1
                    
                    // Use optimized calculations
                    val digitWidthMultiplier = 0.65f
                    val maxFontSizeByWidth = (availableWidth / (digitCount * digitWidthMultiplier)) * 0.95f
                    val maxFontSizeByHeight = (availableHeightForNumber / lines) * 0.85f
                    val finalFontSize = minOf(maxFontSizeByWidth, maxFontSizeByHeight).coerceIn(16f, 500f)
                    
                    // Calculate label size
                    val finalLabelSize = if (showLabel) {
                        // Make label size proportional to the week number size for consistency
                        (finalFontSize * 0.22f).coerceIn(10f, 200f)
                    } else {
                        0f // Not used when label is hidden
                    }
                    
                    views.setTextViewTextSize(R.id.widget_week_number, android.util.TypedValue.COMPLEX_UNIT_SP, finalFontSize)
                    
                    // Handle label visibility and color
                    if (showLabel) {
                        views.setViewVisibility(R.id.widget_label, android.view.View.VISIBLE)
                        views.setTextViewTextSize(R.id.widget_label, android.util.TypedValue.COMPLEX_UNIT_SP, finalLabelSize)
                        try {
                            val textColor = android.graphics.Color.parseColor(
                                WidgetPreferences.getTextColor(context, appWidgetId)
                            )
                            views.setTextColor(R.id.widget_label, textColor)
                        } catch (e: Exception) {
                            val defaultText = android.graphics.Color.parseColor(WidgetPreferences.getThemeAwareTextColor(context))
                            views.setTextColor(R.id.widget_label, defaultText)
                        }
                    } else {
                        views.setViewVisibility(R.id.widget_label, android.view.View.GONE)
                    }
                } catch (e: Exception) {
                    // Use default if calculation fails
                }
                
                // Add click listener to open configuration
                val configIntent = Intent(context, WidgetConfigureActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                val requestCode = appWidgetId and 0x7FFFFFFF // Ensure positive
                val configPendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode,
                    configIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_background, configPendingIntent)
                
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}

