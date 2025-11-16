//
//  ContentView.swift
//  WeekNumberWidget
//
//  Created for iOS
//

import SwiftUI

struct ContentView: View {
    @State private var weekNumber: Int = 1
    @State private var isInstructionsExpanded = false
    @State private var weekStart: String = "monday"
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Week Number Display
                VStack(spacing: 10) {
                    Text("Week Number")
                        .font(.headline)
                        .foregroundColor(.secondary)
                    
                    Text("\(weekNumber)")
                        .font(.system(size: 72, weight: .bold))
                        .foregroundColor(.primary)
                }
                .padding()
                .frame(maxWidth: .infinity)
                
                // Instructions Section
                VStack(alignment: .leading, spacing: 10) {
                    Button(action: {
                        withAnimation {
                            isInstructionsExpanded.toggle()
                        }
                    }) {
                        HStack {
                            Text("How to Add Widget")
                                .font(.headline)
                            Spacer()
                            Image(systemName: isInstructionsExpanded ? "chevron.up" : "chevron.down")
                        }
                        .foregroundColor(.primary)
                    }
                    
                    if isInstructionsExpanded {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("1. Long press on your home screen")
                            Text("2. Tap the \"+\" button in the top left")
                            Text("3. Search for \"Week Number Widget\"")
                            Text("4. Select a widget size")
                            Text("5. Tap \"Add Widget\"")
                            Text("6. Position it where you want")
                        }
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .padding(.top, 5)
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(10)
                
                // Settings Section
                NavigationLink(destination: WidgetSettingsView()) {
                    HStack {
                        Image(systemName: "slider.horizontal.3")
                        Text("Widget Settings")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    .font(.headline)
                    .foregroundColor(.primary)
                }
                .padding()
                
                Spacer()
                
                // About Button
                NavigationLink(destination: AboutView()) {
                    HStack {
                        Image(systemName: "info.circle")
                        Text("About")
                    }
                    .font(.headline)
                    .foregroundColor(.blue)
                }
                .padding()
            }
            .padding()
            .navigationTitle("Week Number Widget")
            .onAppear {
                weekStart = WidgetPreferences.getWeekStart()
                updateWeekNumber()
            }
        }
    }
    
    private func updateWeekNumber() {
        weekNumber = WeekNumberCalculator.getCurrentWeekNumber(weekStart: weekStart)
    }
}

#Preview {
    ContentView()
}

