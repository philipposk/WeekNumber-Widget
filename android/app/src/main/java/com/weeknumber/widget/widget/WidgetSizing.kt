package com.weeknumber.widget.widget

import android.appwidget.AppWidgetManager
import android.os.Bundle
import com.weeknumber.widget.R

object WidgetSizing {

    data class Size(val widthDp: Int, val heightDp: Int) {
        val isTall: Boolean get() = heightDp > widthDp * 1.3f
    }

    data class Fonts(
        val layoutId: Int,
        val numberText: String,
        val numberSp: Float,
        val labelSp: Float
    )

    private const val DEFAULT_DP = 180
    private const val LAYOUT_PADDING_DP = 8f
    private const val SAFETY_MARGIN_DP = 4f
    private const val LABEL_HEIGHT_RATIO = 0.18f
    private const val DIGIT_WIDTH_RATIO = 0.65f
    private const val WIDTH_SAFETY = 0.95f
    private const val HEIGHT_SAFETY = 0.85f
    private const val LABEL_RATIO_OF_NUMBER = 0.35f
    private const val MIN_NUMBER_SP = 16f
    private const val MAX_NUMBER_SP = 500f
    private const val MIN_LABEL_SP = 12f
    private const val MAX_LABEL_SP = 200f

    fun readSize(mgr: AppWidgetManager, appWidgetId: Int): Size {
        return try {
            val options: Bundle = mgr.getAppWidgetOptions(appWidgetId)
            val w = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, DEFAULT_DP)
            val h = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, DEFAULT_DP)
            Size(
                if (w <= 0) DEFAULT_DP else w,
                if (h <= 0) DEFAULT_DP else h
            )
        } catch (_: Exception) {
            Size(DEFAULT_DP, DEFAULT_DP)
        }
    }

    fun pickLayout(size: Size): Int = when {
        size.widthDp <= 85 && size.heightDp <= 85 -> R.layout.widget_home_screen_compact
        size.widthDp <= 130 || size.heightDp <= 130 -> R.layout.widget_home_screen_small
        size.widthDp > 300 || size.heightDp > 300 -> R.layout.widget_home_screen_extra_large
        size.widthDp > 200 || size.heightDp > 200 -> R.layout.widget_home_screen_large
        else -> R.layout.widget_home_screen
    }

    fun compute(size: Size, weekNumber: Int, showLabel: Boolean): Fonts {
        val numberStr = weekNumber.toString()
        val text = if (size.isTall && numberStr.length > 1) {
            numberStr.toCharArray().joinToString("\n")
        } else {
            numberStr
        }

        val totalPadding = LAYOUT_PADDING_DP + SAFETY_MARGIN_DP
        val availableWidth = (size.widthDp - totalPadding).coerceAtLeast(1f)
        val labelRatio = if (showLabel) LABEL_HEIGHT_RATIO else 0f
        val availableHeightForLabel =
            if (showLabel) (size.heightDp * labelRatio - LAYOUT_PADDING_DP).coerceAtLeast(8f) else 0f
        val availableHeightForNumber =
            (size.heightDp * (1f - labelRatio) - totalPadding).coerceAtLeast(1f)

        val digitCount = numberStr.length
        val maxByWidth = (availableWidth / (digitCount * DIGIT_WIDTH_RATIO)) * WIDTH_SAFETY
        val lines = if (size.isTall && numberStr.length > 1) numberStr.length else 1
        val maxByHeight = (availableHeightForNumber / lines) * HEIGHT_SAFETY
        val numberSp = minOf(maxByWidth, maxByHeight).coerceIn(MIN_NUMBER_SP, MAX_NUMBER_SP)

        val labelSp = if (showLabel) {
            val proportional = numberSp * LABEL_RATIO_OF_NUMBER
            val byHeight = availableHeightForLabel * HEIGHT_SAFETY
            minOf(proportional, byHeight).coerceIn(MIN_LABEL_SP, minOf(numberSp * 0.5f, MAX_LABEL_SP))
        } else 0f

        return Fonts(pickLayout(size), text, numberSp, labelSp)
    }
}
