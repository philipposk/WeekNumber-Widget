//
//  WidgetSettingsView.swift
//  WeekNumberWidget
//
//  Created for iOS
//

import SwiftUI
import WidgetKit

struct WidgetSettingsView: View {
    @State private var backgroundColor: Color = .clear
    @State private var textColor: Color = .primary
    @State private var weekStart: String = "monday"
    @State private var showLabel: Bool = true
    
    let widgetId: String = "default"
    
    var body: some View {
        Form {
            Section(header: Text("Appearance")) {
                ColorPicker("Background Color", selection: $backgroundColor)
                    .onChange(of: backgroundColor) { newValue in
                        WidgetPreferences.setBackgroundColor(widgetId: widgetId, color: newValue)
                        // Force reload all widgets
                        WidgetCenter.shared.reloadAllTimelines()
                        WidgetCenter.shared.reloadTimelines(ofKind: "WeekNumberWidget")
                    }
                
                ColorPicker("Text Color", selection: $textColor)
                    .onChange(of: textColor) { newValue in
                        WidgetPreferences.setTextColor(widgetId: widgetId, color: newValue)
                        // Force reload all widgets
                        WidgetCenter.shared.reloadAllTimelines()
                        WidgetCenter.shared.reloadTimelines(ofKind: "WeekNumberWidget")
                    }
                
                Toggle("Show Label", isOn: $showLabel)
                    .onChange(of: showLabel) { newValue in
                        WidgetPreferences.setShowLabel(widgetId: widgetId, show: newValue)
                        // Force reload all widgets
                        WidgetCenter.shared.reloadAllTimelines()
                        WidgetCenter.shared.reloadTimelines(ofKind: "WeekNumberWidget")
                    }
            }
            
            Section(header: Text("Week Calculation")) {
                Picker("Week Start", selection: $weekStart) {
                    Text("Monday (ISO 8601)").tag("monday")
                    Text("Sunday (US/Canada)").tag("sunday")
                }
                .onChange(of: weekStart) { newValue in
                    WidgetPreferences.setWeekStart(widgetId: widgetId, weekStart: newValue)
                    WidgetCenter.shared.reloadTimelines(ofKind: "WeekNumberWidget")
                }
            }
            
            Section(header: Text("Preview")) {
                HStack {
                    Spacer()
                    VStack(spacing: 8) {
                        if showLabel {
                            Text(WidgetPreferences.getWeekLabel())
                                .font(.caption)
                                .foregroundColor(textColor)
                        }
                        Text("\(WeekNumberCalculator.getCurrentWeekNumber(weekStart: weekStart))")
                            .font(.system(size: 48, weight: .bold))
                            .foregroundColor(textColor)
                    }
                    .frame(width: 150, height: 150)
                    .background(backgroundColor)
                    .cornerRadius(12)
                    Spacer()
                }
                .padding(.vertical)
            }
        }
        .navigationTitle("Widget Settings")
        .onAppear {
            loadSettings()
        }
    }
    
    private func loadSettings() {
        backgroundColor = WidgetPreferences.getBackgroundColor(widgetId: widgetId)
        textColor = WidgetPreferences.getTextColor(widgetId: widgetId)
        weekStart = WidgetPreferences.getWeekStart(widgetId: widgetId)
        showLabel = WidgetPreferences.getShowLabel(widgetId: widgetId)
    }
}

#Preview {
    NavigationView {
        WidgetSettingsView()
    }
}

