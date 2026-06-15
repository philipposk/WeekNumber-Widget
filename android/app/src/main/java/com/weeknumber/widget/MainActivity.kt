package com.weeknumber.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.weeknumber.widget.databinding.ActivityMainBinding
import com.weeknumber.widget.widget.HomeScreenWidgetProvider
import com.weeknumber.widget.widget.WidgetConfigureActivity
import com.weeknumber.widget.widget.WidgetPreferences

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

        when (widgetIds.size) {
            0 -> android.widget.Toast.makeText(
                this,
                "Add a widget to your home screen first",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            1 -> launchConfig(widgetIds[0])
            else -> showWidgetPicker(widgetIds)
        }
    }

    private fun showWidgetPicker(widgetIds: IntArray) {
        // Label each widget with its current week number + week-start so the
        // user can tell otherwise-identical widgets apart.
        val labels = widgetIds.mapIndexed { i, id ->
            val week = WeekNumberCalculator.getCurrentWeekNumber(this, id)
            val start = WidgetPreferences.getWeekStart(this, id)
                .replaceFirstChar { it.uppercase() }
            "Widget ${i + 1}  ·  Week $week  ·  $start start"
        }.toTypedArray()
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Pick widget to configure")
            .setItems(labels) { _, which -> launchConfig(widgetIds[which]) }
            .show()
    }

    private fun launchConfig(widgetId: Int) {
        val configIntent = Intent(this, WidgetConfigureActivity::class.java).apply {
            action = WidgetConfigureActivity.ACTION_RECONFIGURE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        startActivity(configIntent)
    }

    private fun updateWeekNumber() {
        // Get week number using first widget's preference, or default to Monday
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val componentName = ComponentName(this, HomeScreenWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)

        val firstId = widgetIds.firstOrNull() ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val weekNumber = WeekNumberCalculator.getCurrentWeekNumber(this, firstId)

        binding.weekNumberText.text = weekNumber.toString()
        updateThisWeekCard(firstId)
    }

    private fun updateThisWeekCard(widgetId: Int) {
        binding.weekDatesText.text =
            WeekNumberCalculator.weekRangeLabel(this, widgetId)
        val daysLeft = WeekNumberCalculator.daysRemainingInYear()
        binding.daysLeftText.text = resources.getQuantityString(
            R.plurals.days_left_in_year, daysLeft, daysLeft
        )
        val progress = WeekNumberCalculator.yearProgressPercent()
        binding.yearProgress.progress = progress
        binding.yearProgressText.text = getString(R.string.year_progress, progress)
    }

    private fun shareWeekNumber() {
        // Share the same week the user sees on screen (first widget's preference).
        val firstId = AppWidgetManager.getInstance(this)
            .getAppWidgetIds(ComponentName(this, HomeScreenWidgetProvider::class.java))
            .firstOrNull() ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val week = WeekNumberCalculator.getCurrentWeekNumber(this, firstId)
        val text = getString(R.string.share_week_text, week, WeekNumberCalculator.getCurrentYear())
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(send, getString(R.string.share_week)))
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> { shareWeekNumber(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        // Update week number when returning to the app
        updateWeekNumber()
    }
}

