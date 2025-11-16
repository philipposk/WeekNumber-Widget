package com.weeknumber.widget.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.weeknumber.widget.R

class WidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    
    // UI elements
    private lateinit var backgroundColorPicker: View
    private lateinit var textColorPicker: View
    private lateinit var showTitleSwitch: Switch
    private lateinit var weekStartSwitch: Switch
    private lateinit var weekStartText: TextView
    private lateinit var previewText: TextView
    private lateinit var previewLabel: TextView
    private lateinit var previewBackground: View
    private lateinit var saveButton: Button
    
    // Current colors
    private var backgroundColor = Color.TRANSPARENT
    private var textColor = Color.BLACK // Will be updated in loadCurrentSettings based on theme
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle back button press - save widget when user presses back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // If user presses back, still save the widget with current/default settings
                val appWidgetManager = AppWidgetManager.getInstance(this@WidgetConfigureActivity)
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    HomeScreenWidgetProvider.updateAppWidget(this@WidgetConfigureActivity, appWidgetManager, appWidgetId)
                    val resultValue = Intent()
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    setResult(Activity.RESULT_OK, resultValue)
                }
                finish()
            }
        })
        
        // Set result to CANCELED in case user backs out
        setResult(Activity.RESULT_CANCELED)
        
        try {
            // Find widget ID from intent first
            val intent = intent
            val extras = intent.extras
            
            // Try to get widget ID from extras
            if (extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
            } else {
                // Also try to get from intent directly (for widget clicks)
                appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }
            
            // If no widget ID, try to get from saved instance state
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && savedInstanceState != null) {
                appWidgetId = savedInstanceState.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }
            
            // If still no widget ID, log and show error but don't finish immediately
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                android.util.Log.e("WidgetConfigure", "No widget ID found in intent")
                // Show error message to user
                android.widget.Toast.makeText(
                    this,
                    "Error: Widget ID not found",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }
            
            // Update widget immediately with default settings so it appears even if user closes config
            val appWidgetManager = AppWidgetManager.getInstance(this)
            HomeScreenWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)
            
            setContentView(R.layout.activity_widget_configure)
            
            initializeViews()
            loadCurrentSettings()
            setupListeners()
            
            // Save button - check if initialized
            if (::saveButton.isInitialized) {
                saveButton.setOnClickListener {
                    saveSettings()
                }
            }
        } catch (e: Exception) {
            // Log the error for debugging
            android.util.Log.e("WidgetConfigure", "Error in onCreate", e)
            // If there's an error, at least try to save the widget with defaults
            val appWidgetManager = AppWidgetManager.getInstance(this)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                try {
                    HomeScreenWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)
                    val resultValue = Intent()
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    setResult(Activity.RESULT_OK, resultValue)
                } catch (updateError: Exception) {
                    android.util.Log.e("WidgetConfigure", "Error updating widget", updateError)
                }
            }
            // Show error to user
            android.widget.Toast.makeText(
                this,
                "Error opening configuration: ${e.message}",
                android.widget.Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // Handle new intent when activity is already running (singleTop mode)
        val newWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        
        if (newWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && newWidgetId != appWidgetId) {
            appWidgetId = newWidgetId
            // Reload settings for the new widget
            loadCurrentSettings()
        }
    }
    
    private fun initializeViews() {
        try {
            backgroundColorPicker = findViewById(R.id.background_color_picker)
            textColorPicker = findViewById(R.id.text_color_picker)
            showTitleSwitch = findViewById(R.id.show_title_switch)
            weekStartSwitch = findViewById(R.id.week_start_switch)
            weekStartText = findViewById(R.id.week_start_text)
            previewText = findViewById(R.id.preview_week_number)
            previewLabel = findViewById(R.id.preview_label)
            previewBackground = findViewById(R.id.preview_background)
            saveButton = findViewById(R.id.save_button)
        } catch (e: Exception) {
            // If views can't be found, widget will still work with defaults
            android.util.Log.e("WidgetConfigure", "Error initializing views", e)
        }
    }
    
    private fun loadCurrentSettings() {
        // Check if preferences exist for this widget
        val prefs = WidgetPreferences.getPrefs(this, appWidgetId)
        val hasPreferences = prefs.all.isNotEmpty()
        
        if (!hasPreferences) {
            // First time - use defaults: transparent background and system text
            backgroundColor = Color.TRANSPARENT
            textColor = Color.parseColor(WidgetPreferences.getThemeAwareTextColor(this))
            showTitleSwitch.isChecked = true // Show title by default
            weekStartSwitch.isChecked = false // Monday (false = Monday, true = Sunday)
            weekStartText.text = "Monday"
        } else {
            // Load saved preferences
            val bgColorStr = WidgetPreferences.getBackgroundColor(this, appWidgetId)
            backgroundColor = if (bgColorStr == "#00000000") Color.TRANSPARENT else Color.parseColor(bgColorStr)
            val textColorStr = WidgetPreferences.getTextColor(this, appWidgetId)
            textColor = Color.parseColor(textColorStr)
            
            // Load show title preference
            val labelSizeOption = WidgetPreferences.getLabelSizeOption(this, appWidgetId)
            showTitleSwitch.isChecked = labelSizeOption != "no_title"
            
            // Load week start preference
            val weekStart = WidgetPreferences.getWeekStart(this, appWidgetId)
            weekStartSwitch.isChecked = weekStart == "sunday"
            weekStartText.text = if (weekStart == "sunday") "Sunday" else "Monday"
        }
        
        updateColorPickers()
        updatePreview()
    }
    
    private fun updateColorPickers() {
        // Check if background is Danish flag by checking if it's the Danish flag red color
        val danishFlagRed = Color.parseColor("#C8102E")
        if (backgroundColor == danishFlagRed) {
            backgroundColorPicker.setBackgroundResource(R.drawable.danish_flag_background)
            backgroundColorPicker.alpha = 1.0f
        } else if (backgroundColor == Color.TRANSPARENT) {
            // Show a subtle pattern for transparent
            backgroundColorPicker.setBackgroundResource(android.R.drawable.edit_text)
            backgroundColorPicker.alpha = 0.2f
        } else {
            backgroundColorPicker.setBackgroundColor(backgroundColor)
            backgroundColorPicker.alpha = 1.0f
        }
        textColorPicker.setBackgroundColor(textColor)
    }
    
    private fun setupListeners() {
        // Color pickers
        backgroundColorPicker.setOnClickListener {
            showColorPickerDialog("background")
        }
        textColorPicker.setOnClickListener {
            showColorPickerDialog("text")
        }
        
        // Show title toggle
        showTitleSwitch.setOnCheckedChangeListener { _, _ ->
            updatePreview()
        }
        
        // Week start toggle - update text and preview
        weekStartSwitch.setOnCheckedChangeListener { _, isChecked ->
            weekStartText.text = if (isChecked) "Sunday" else "Monday"
            // Temporarily save preference to calculate preview correctly
            val tempWeekStart = if (isChecked) "sunday" else "monday"
            WidgetPreferences.setWeekStart(this, appWidgetId, tempWeekStart)
            updatePreview()
        }
    }
    
    private fun showColorPickerDialog(type: String) {
        if (type == "background") {
            // Background color options
            val backgrounds = arrayOf(
                "System" to Color.TRANSPARENT,
                "Transparent" to Color.TRANSPARENT,
                "White" to Color.WHITE,
                "Black" to Color.BLACK,
                "Danish Flag" to Color.parseColor("#C8102E"), // Red color for Danish flag
                "Purple" to Color.parseColor("#FF6200EE"),
                "Blue" to Color.BLUE,
                "Red" to Color.RED,
                "Green" to Color.GREEN,
                "Orange" to Color.parseColor("#FFFF9800"),
                "Teal" to Color.parseColor("#FF03DAC5"),
                "Gray" to Color.GRAY
            )
            
            val backgroundNames = backgrounds.map { it.first }.toTypedArray()
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Background")
            builder.setItems(backgroundNames) { _, which ->
                val selectedBackground = backgrounds[which].first
                val selectedColor = backgrounds[which].second
                
                backgroundColor = selectedColor
                
                // Set default text color based on background
                when (selectedBackground) {
                    "Black" -> {
                        textColor = Color.WHITE
                        textColorPicker.setBackgroundColor(textColor)
                    }
                    "White" -> {
                        textColor = Color.BLACK
                        textColorPicker.setBackgroundColor(textColor)
                    }
                    "Danish Flag" -> {
                        textColor = Color.BLACK
                        textColorPicker.setBackgroundColor(textColor)
                    }
                    "System", "Transparent" -> {
                        textColor = Color.parseColor(WidgetPreferences.getThemeAwareTextColor(this))
                        textColorPicker.setBackgroundColor(textColor)
                    }
                }
                
                // Update background picker display
                if (selectedBackground == "Danish Flag") {
                    backgroundColorPicker.setBackgroundResource(R.drawable.danish_flag_background)
                    backgroundColorPicker.alpha = 1.0f
                } else if (backgroundColor == Color.TRANSPARENT) {
                    backgroundColorPicker.setBackgroundResource(android.R.drawable.edit_text)
                    backgroundColorPicker.alpha = 0.3f
                } else {
                    backgroundColorPicker.setBackgroundColor(backgroundColor)
                    backgroundColorPicker.alpha = 1.0f
                }
                
                updatePreview()
            }
            builder.show()
        } else {
            // Text color options (no transparent)
            val textColors = arrayOf(
                "System" to Color.parseColor(WidgetPreferences.getThemeAwareTextColor(this)),
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
            
            val textColorNames = textColors.map { it.first }.toTypedArray()
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Text color")
            builder.setItems(textColorNames) { _, which ->
                val selectedColor = textColors[which].second
                textColor = selectedColor
                textColorPicker.setBackgroundColor(textColor)
                updatePreview()
            }
            builder.show()
        }
    }
    
    private fun updatePreview() {
        val weekNumber = com.weeknumber.widget.WeekNumberCalculator.getCurrentWeekNumber(this, appWidgetId)
        previewText.text = weekNumber.toString()
        
        // Get show label from toggle
        val showLabel = showTitleSwitch.isChecked
        
        // Handle label visibility
        if (showLabel) {
            previewLabel.visibility = android.view.View.VISIBLE
            previewLabel.text = WidgetPreferences.getWeekLabel(this, appWidgetId)
            previewLabel.setTextColor(textColor)
            previewLabel.textSize = 14f
        } else {
            previewLabel.visibility = android.view.View.GONE
        }
        
        // Handle background - check if it's Danish flag
        val danishFlagRed = Color.parseColor("#C8102E")
        if (backgroundColor == danishFlagRed) {
            previewBackground.setBackgroundResource(R.drawable.danish_flag_background)
            previewBackground.alpha = 1.0f
        } else if (backgroundColor == Color.TRANSPARENT) {
            previewBackground.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            previewBackground.alpha = 0.3f
        } else {
            previewBackground.setBackgroundColor(backgroundColor)
            previewBackground.alpha = 1.0f
        }
        
        // Set text colors
        previewText.setTextColor(textColor)
        if (showLabel) {
            previewLabel.setTextColor(textColor)
        }
        
        // Preview text size
        previewText.textSize = 64f
        previewText.typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    
    private fun saveSettings() {
        // Save all preferences
        // Handle transparent background properly
        val bgColorStr = if (backgroundColor == Color.TRANSPARENT) {
            "#00000000"
        } else {
            String.format("#%08X", backgroundColor)
        }
        WidgetPreferences.setBackgroundColor(this, appWidgetId, bgColorStr)
        WidgetPreferences.setTextColor(this, appWidgetId, String.format("#%08X", textColor))
        
        // Save show title option
        val labelSizeOption = if (showTitleSwitch.isChecked) "small" else "no_title"
        WidgetPreferences.setLabelSizeOption(this, appWidgetId, labelSizeOption)
        
        // Save week start option
        val weekStart = if (weekStartSwitch.isChecked) "sunday" else "monday"
        WidgetPreferences.setWeekStart(this, appWidgetId, weekStart)
        
        // Update the widget
        val appWidgetManager = AppWidgetManager.getInstance(this)
        HomeScreenWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)
        
        // Return result
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}

