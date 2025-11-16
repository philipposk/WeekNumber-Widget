# Week Number Widget - iOS

A simple iOS app that displays the current week number on your iPhone's home screen as a widget.

## Features

- **Home Screen Widget**: Display the current week number on your home screen
- **Dark/Light Mode**: Automatic theme support based on system settings
- **ISO 8601 Standard**: Calculates week numbers using the ISO 8601 standard (weeks start on Monday)
- **US/Canada Style**: Option to use Sunday-start week calculation
- **Customizable**: Change colors, show/hide label, and configure week start day

## Requirements

- iOS 17.0 or later
- Xcode 15.0 or later
- Swift 5.9 or later

## Project Structure

```
WeekNumberWidget/
├── WeekNumberWidget/              # Main app
│   ├── WeekNumberWidgetApp.swift  # App entry point
│   ├── ContentView.swift          # Main view
│   ├── AboutView.swift            # About page
│   ├── WidgetSettingsView.swift   # Widget configuration
│   ├── WeekNumberCalculator.swift # Week number calculation logic
│   ├── WidgetPreferences.swift   # Shared preferences
│   └── Info.plist                 # App configuration
│
└── WeekNumberWidgetExtension/     # Widget extension
    ├── WeekNumberWidgetBundle.swift      # Widget bundle
    ├── WeekNumberWidgetExtension.swift   # Widget definition
    ├── WeekNumberTimelineProvider.swift  # Timeline provider
    ├── WeekNumberWidgetEntryView.swift   # Widget view
    └── Info.plist                        # Extension configuration
```

## Building the App

1. Open `WeekNumberWidget.xcodeproj` in Xcode
2. Select your development team in Signing & Capabilities
3. Ensure the App Group is configured:
   - App Group: `group.philippos.Week-Number-Widget`
   - Both the main app and widget extension must have this App Group enabled
4. Build and run on a simulator or device

## Setting Up App Groups

1. In Xcode, select the project
2. Go to "Signing & Capabilities" for both targets:
   - WeekNumberWidget (main app)
   - WeekNumberWidgetExtension (widget extension)
3. Click "+ Capability" and add "App Groups"
4. Add the group: `group.philippos.Week-Number-Widget`
5. Make sure both targets use the same App Group

## Adding Widgets

1. Long press on your home screen
2. Tap the "+" button in the top left
3. Search for "Week Number Widget"
4. Select a widget size (Small, Medium, or Large)
5. Tap "Add Widget"
6. Position it where you want on your home screen

## Configuration

Open the app and navigate to "Widget Settings" to customize:
- Background color
- Text color
- Show/hide label
- Week start day (Monday or Sunday)

Changes are automatically applied to all widgets.

## Technical Details

- **Language**: Swift
- **UI Framework**: SwiftUI
- **Widget Framework**: WidgetKit
- **Min iOS Version**: 17.0
- **Widget Update Frequency**: Daily (updates at midnight)

## Week Number Calculation

The app supports two week numbering systems:

1. **ISO 8601 (Monday start)**: 
   - Weeks start on Monday
   - Week 1 is the first week with at least 4 days in the new year

2. **US/Canada (Sunday start)**:
   - Weeks start on Sunday
   - Week 1 contains January 1st

## License

This project is open source and available for personal use.

