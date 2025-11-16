//
//  WeekNumberTimelineProvider.swift
//  WeekNumberWidgetExtension
//
//  Created for iOS
//

import WidgetKit
import SwiftUI

struct WeekNumberTimelineProvider: TimelineProvider {
    typealias Entry = WeekNumberEntry
    
    func placeholder(in context: Context) -> WeekNumberEntry {
        WeekNumberEntry(
            date: Date(),
            weekNumber: 42,
            weekLabel: "Week",
            backgroundColor: .clear,
            textColor: .primary,
            showLabel: true
        )
    }
    
    func getSnapshot(in context: Context, completion: @escaping (WeekNumberEntry) -> Void) {
        let entry = createEntry()
        completion(entry)
    }
    
    func getTimeline(in context: Context, completion: @escaping (Timeline<WeekNumberEntry>) -> Void) {
        var entries: [WeekNumberEntry] = []
        
        // Create entry for now
        let currentEntry = createEntry()
        entries.append(currentEntry)
        
        // Create entry for tomorrow at midnight to update the widget
        let calendar = Calendar.current
        let tomorrow = calendar.startOfDay(for: calendar.date(byAdding: .day, value: 1, to: Date()) ?? Date())
        
        let tomorrowEntry = createEntry(for: tomorrow)
        entries.append(tomorrowEntry)
        
        // Create timeline that refreshes daily
        let timeline = Timeline(entries: entries, policy: .atEnd)
        completion(timeline)
    }
    
    private func createEntry(for date: Date = Date()) -> WeekNumberEntry {
        // Get widget preferences (using default widget ID for now)
        let widgetId = "default"
        let weekStart = WidgetPreferences.getWeekStart(widgetId: widgetId)
        let weekNumber = WeekNumberCalculator.getCurrentWeekNumber(weekStart: weekStart)
        let weekLabel = WidgetPreferences.getWeekLabel()
        let backgroundColor = WidgetPreferences.getBackgroundColor(widgetId: widgetId)
        let textColor = WidgetPreferences.getTextColor(widgetId: widgetId)
        let showLabel = WidgetPreferences.getShowLabel(widgetId: widgetId)
        
        return WeekNumberEntry(
            date: date,
            weekNumber: weekNumber,
            weekLabel: weekLabel,
            backgroundColor: backgroundColor,
            textColor: textColor,
            showLabel: showLabel
        )
    }
}

