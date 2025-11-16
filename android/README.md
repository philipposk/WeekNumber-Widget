# Week Number Widget

A simple Android app that displays the current week number on your phone's home and lock screens.

## Features

- **Home Screen Widget**: Display the current week number on your home screen
- **Lock Screen Widget**: Display the current week number on your lock screen (Android 5.0+)
- **Dark/Light Mode**: Automatic theme support based on system settings
- **ISO 8601 Standard**: Calculates week numbers using the ISO 8601 standard (weeks start on Monday)

## Requirements

- Android 8.0 (API level 26) or higher
- Android Studio Hedgehog (2023.1.1) or later

## Building the App

1. Open the project in Android Studio
2. Sync Gradle files
3. **Generate launcher icons**: Right-click on `app/src/main/res` > New > Image Asset, or use Android Studio's built-in icon generator
4. Build the project (Build > Make Project)
5. Run on an emulator or connected device

## Adding Widgets

### Home Screen Widget
1. Long press on your home screen
2. Select "Widgets"
3. Find "Week Number Widget"
4. Drag the widget to your desired location

### Lock Screen Widget (Android 5.0+)
1. Go to Settings > Security > Lock Screen
2. Enable lock screen widgets
3. Add the widget from the widget menu

## Project Structure

```
app/
├── src/main/
│   ├── java/com/weeknumber/widget/
│   │   ├── MainActivity.kt          # Main app activity
│   │   ├── AboutActivity.kt         # About page
│   │   ├── WeekNumberCalculator.kt  # Week number calculation logic
│   │   └── widget/
│   │       ├── HomeScreenWidgetProvider.kt
│   │       ├── LockScreenWidgetProvider.kt
│   │       └── BootReceiver.kt
│   ├── res/
│   │   ├── layout/                  # UI layouts
│   │   ├── values/                   # Strings, colors, themes
│   │   └── xml/                      # Widget configurations
│   └── AndroidManifest.xml
└── build.gradle
```

## Technical Details

- **Language**: Kotlin
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Widget Update Frequency**: Daily (86400000 milliseconds)

## License

This project is open source and available for personal use.

