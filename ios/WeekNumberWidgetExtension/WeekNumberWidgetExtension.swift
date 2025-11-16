//
//  WeekNumberWidgetExtension.swift
//  WeekNumberWidgetExtension
//
//  Created for iOS
//

import WidgetKit
import SwiftUI

struct WeekNumberWidget: Widget {
    let kind: String = "WeekNumberWidget"
    
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: WeekNumberTimelineProvider()) { entry in
            WeekNumberWidgetEntryView(entry: entry)
                .containerBackground(entry.backgroundColor, for: .widget)
        }
        .configurationDisplayName("Week Number Widget")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

struct WeekNumberEntry: TimelineEntry {
    let date: Date
    let weekNumber: Int
    let weekLabel: String
    let backgroundColor: Color
    let textColor: Color
    let showLabel: Bool
}

