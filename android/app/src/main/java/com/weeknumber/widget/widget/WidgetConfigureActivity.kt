package com.weeknumber.widget.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.weeknumber.widget.R
import com.weeknumber.widget.WeekNumberCalculator

class WidgetConfigureActivity : AppCompatActivity() {

    companion object {
        const val ACTION_RECONFIGURE = "com.weeknumber.widget.action.RECONFIGURE"
    }

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var isSetupMode = true

    private lateinit var backgroundColorPicker: View
    private lateinit var textColorPicker: View
    private lateinit var showTitleSwitch: Switch
    private lateinit var weekStartSwitch: Switch
    private lateinit var weekStartText: TextView
    private lateinit var previewText: TextView
    private lateinit var previewLabel: TextView
    private lateinit var previewBackground: View
    private lateinit var saveButton: Button

    private var backgroundColor = Color.TRANSPARENT
    private var textColor = Color.BLACK
    private var pendingWeekStart = "monday"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isSetupMode = intent?.action != ACTION_RECONFIGURE
        setResult(if (isSetupMode) Activity.RESULT_CANCELED else Activity.RESULT_OK)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && savedInstanceState != null) {
            appWidgetId = savedInstanceState.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Toast.makeText(this, "Widget ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isSetupMode) {
                    persistDefaultsAndCommit()
                }
                finish()
            }
        })

        setContentView(R.layout.activity_widget_configure)
        initializeViews()
        loadCurrentSettings()
        setupListeners()
        saveButton.setOnClickListener { saveSettings() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }

    private fun initializeViews() {
        backgroundColorPicker = findViewById(R.id.background_color_picker)
        textColorPicker = findViewById(R.id.text_color_picker)
        showTitleSwitch = findViewById(R.id.show_title_switch)
        weekStartSwitch = findViewById(R.id.week_start_switch)
        weekStartText = findViewById(R.id.week_start_text)
        previewText = findViewById(R.id.preview_week_number)
        previewLabel = findViewById(R.id.preview_label)
        previewBackground = findViewById(R.id.preview_background)
        saveButton = findViewById(R.id.save_button)
    }

    private fun loadCurrentSettings() {
        val prefs = WidgetPreferences.getPrefs(this, appWidgetId)
        val hasPreferences = prefs.all.isNotEmpty()

        if (!hasPreferences) {
            backgroundColor = Color.TRANSPARENT
            textColor = Color.parseColor(WidgetPreferences.getThemeAwareTextColor(this))
            showTitleSwitch.isChecked = true
            weekStartSwitch.isChecked = false
            pendingWeekStart = "monday"
            weekStartText.text = "Monday"
        } else {
            val bgColorStr = WidgetPreferences.getBackgroundColor(this, appWidgetId)
            backgroundColor = if (bgColorStr == "#00000000") Color.TRANSPARENT else Color.parseColor(bgColorStr)
            textColor = Color.parseColor(WidgetPreferences.getTextColor(this, appWidgetId))

            showTitleSwitch.isChecked = WidgetPreferences.getLabelSizeOption(this, appWidgetId) != "no_title"

            pendingWeekStart = WidgetPreferences.getWeekStart(this, appWidgetId)
            weekStartSwitch.isChecked = pendingWeekStart == "sunday"
            weekStartText.text = if (pendingWeekStart == "sunday") "Sunday" else "Monday"
        }

        updateColorPickers()
        updatePreview()
    }

    private fun updateColorPickers() {
        if (backgroundColor == Color.TRANSPARENT) {
            backgroundColorPicker.setBackgroundResource(android.R.drawable.edit_text)
            backgroundColorPicker.alpha = 0.2f
        } else {
            backgroundColorPicker.setBackgroundColor(backgroundColor)
            backgroundColorPicker.alpha = 1.0f
        }
        textColorPicker.setBackgroundColor(textColor)
    }

    private fun setupListeners() {
        backgroundColorPicker.setOnClickListener { showColorPickerDialog("background") }
        textColorPicker.setOnClickListener { showColorPickerDialog("text") }
        showTitleSwitch.setOnCheckedChangeListener { _, _ -> updatePreview() }
        weekStartSwitch.setOnCheckedChangeListener { _, isChecked ->
            pendingWeekStart = if (isChecked) "sunday" else "monday"
            weekStartText.text = if (isChecked) "Sunday" else "Monday"
            updatePreview()
        }
    }

    private fun showColorPickerDialog(type: String) {
        val colors = arrayOf(
            "System" to if (type == "background") Color.TRANSPARENT else Color.parseColor(WidgetPreferences.getThemeAwareTextColor(this)),
            "Transparent" to Color.TRANSPARENT,
            "White" to Color.WHITE,
            "Black" to Color.BLACK,
            "Purple" to Color.parseColor("#FF6200EE"),
            "Blue" to Color.BLUE,
            "Red" to Color.RED,
            "Green" to Color.GREEN,
            "Orange" to Color.parseColor("#FFFF9800"),
            "Teal" to Color.parseColor("#FF03DAC5"),
            "Gray" to Color.GRAY
        )

        val names = (colors.map { it.first } + "Custom…").toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(if (type == "background") "Background" else "Text color")
            .setItems(names) { _, which ->
                if (which == colors.size) {
                    showCustomColorDialog(type)
                    return@setItems
                }
                val selected = colors[which].second
                if (type == "background") {
                    backgroundColor = selected
                    if (backgroundColor == Color.TRANSPARENT && colors[which].first == "System") {
                        textColor = Color.parseColor(WidgetPreferences.getThemeAwareTextColor(this))
                    }
                } else {
                    textColor = selected
                }
                updateColorPickers()
                updatePreview()
            }
            .show()
    }

    /** Free RGB(+alpha for background) colour picker built from SeekBars — no extra deps. */
    private fun showCustomColorDialog(type: String) {
        val allowAlpha = type == "background"
        val initial = if (type == "background") backgroundColor else textColor
        // Transparent means alpha 0; reflect that as a starting opacity of 0.
        var a = if (initial == Color.TRANSPARENT) 0 else Color.alpha(initial)
        var r = Color.red(initial); var g = Color.green(initial); var b = Color.blue(initial)

        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(8))
        }
        val swatch = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(56))
        }
        container.addView(swatch)

        fun current() = Color.argb(if (allowAlpha) a else 255, r, g, b)
        fun refresh() { swatch.setBackgroundColor(current()) }
        refresh()

        fun addSlider(label: String, initialValue: Int, onChange: (Int) -> Unit) {
            container.addView(TextView(this).apply {
                text = label
                setPadding(0, dp(12), 0, 0)
            })
            container.addView(SeekBar(this).apply {
                max = 255
                progress = initialValue
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) { onChange(p); refresh() }
                    override fun onStartTrackingTouch(sb: SeekBar?) {}
                    override fun onStopTrackingTouch(sb: SeekBar?) {}
                })
            })
        }

        if (allowAlpha) addSlider("Opacity", a) { a = it }
        addSlider("Red", r) { r = it }
        addSlider("Green", g) { g = it }
        addSlider("Blue", b) { b = it }

        AlertDialog.Builder(this)
            .setTitle("Custom colour")
            .setView(container)
            .setPositiveButton("Use") { _, _ ->
                val picked = current()
                if (type == "background") backgroundColor = picked else textColor = picked
                updateColorPickers()
                updatePreview()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updatePreview() {
        val weekNumber = computePreviewWeekNumber()
        previewText.text = weekNumber.toString()

        val showLabel = showTitleSwitch.isChecked
        if (showLabel) {
            previewLabel.visibility = View.VISIBLE
            previewLabel.text = WidgetPreferences.getWeekLabel(this, appWidgetId)
            previewLabel.setTextColor(textColor)
            previewLabel.textSize = 14f
        } else {
            previewLabel.visibility = View.GONE
        }

        previewBackground.setBackgroundColor(backgroundColor)
        previewBackground.alpha = 1.0f
        previewText.setTextColor(textColor)
        previewText.textSize = 64f
        previewText.typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    private fun computePreviewWeekNumber(): Int {
        val cal = java.util.Calendar.getInstance()
        if (pendingWeekStart == "sunday") {
            cal.firstDayOfWeek = java.util.Calendar.SUNDAY
            cal.minimalDaysInFirstWeek = 1
        } else {
            cal.firstDayOfWeek = java.util.Calendar.MONDAY
            cal.minimalDaysInFirstWeek = 4
        }
        return cal.get(java.util.Calendar.WEEK_OF_YEAR)
    }

    private fun persistDefaultsAndCommit() {
        val prefs = WidgetPreferences.getPrefs(this, appWidgetId)
        if (prefs.all.isEmpty()) {
            WidgetPreferences.setBackgroundColor(this, appWidgetId, "#00000000")
            WidgetPreferences.setTextColor(this, appWidgetId, WidgetPreferences.getThemeAwareTextColor(this))
            WidgetPreferences.setLabelSizeOption(this, appWidgetId, "small")
            WidgetPreferences.setWeekStart(this, appWidgetId, "monday")
        }
        val appWidgetManager = AppWidgetManager.getInstance(this)
        HomeScreenWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)
        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
    }

    private fun saveSettings() {
        val bgColorStr = if (backgroundColor == Color.TRANSPARENT) "#00000000"
                        else String.format("#%08X", backgroundColor)
        WidgetPreferences.setBackgroundColor(this, appWidgetId, bgColorStr)
        WidgetPreferences.setTextColor(this, appWidgetId, String.format("#%08X", textColor))
        WidgetPreferences.setLabelSizeOption(
            this, appWidgetId, if (showTitleSwitch.isChecked) "small" else "no_title"
        )
        WidgetPreferences.setWeekStart(this, appWidgetId, pendingWeekStart)

        val appWidgetManager = AppWidgetManager.getInstance(this)
        HomeScreenWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)

        if (isSetupMode) {
            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
        }
        finish()
    }
}
