package com.weeknumber.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.weeknumber.widget.databinding.ActivityMainBinding
import com.weeknumber.widget.widget.HomeScreenWidgetProvider
import com.weeknumber.widget.widget.WidgetConfigureActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        // Update week number display
        updateWeekNumber()
        
        // Set up configure widgets button
        binding.configureWidgetsButton.setOnClickListener {
            openWidgetConfiguration()
        }
        
        // Set up expandable instructions
        var isExpanded = false
        binding.instructionsHeader.setOnClickListener {
            isExpanded = !isExpanded
            if (isExpanded) {
                binding.instructionsContent.visibility = android.view.View.VISIBLE
                binding.instructionsArrow.text = "▲"
            } else {
                binding.instructionsContent.visibility = android.view.View.GONE
                binding.instructionsArrow.text = "▼"
            }
        }
    }
    
    private fun openWidgetConfiguration() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, HomeScreenWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        if (widgetIds.isNotEmpty()) {
            // Open configuration for the first widget
            val configIntent = Intent(this, WidgetConfigureActivity::class.java)
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetIds[0])
            startActivity(configIntent)
        } else {
            // No widgets yet - show instructions in a dialog or toast
            android.widget.Toast.makeText(
                this,
                "Add a widget to your home screen to configure it",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateWeekNumber() {
        // Get week number using first widget's preference, or default to Monday
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, HomeScreenWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        val weekNumber = if (widgetIds.isNotEmpty()) {
            WeekNumberCalculator.getCurrentWeekNumber(this, widgetIds[0])
        } else {
            WeekNumberCalculator.getCurrentWeekNumber() // Default to Monday
        }
        
        binding.weekNumberText.text = weekNumber.toString()
    }

    override fun onResume() {
        super.onResume()
        // Update week number when returning to the app
        updateWeekNumber()
    }
}

