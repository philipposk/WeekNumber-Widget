# iOS Setup Guide

This guide will help you set up the Week Number Widget iOS app in Xcode.

## Step 1: Create a New Xcode Project

1. Open Xcode
2. Select "Create a new Xcode project"
3. Choose "iOS" > "App"
4. Click "Next"
5. Fill in the project details:
   - **Product Name**: `WeekNumberWidget`
   - **Team**: Select your development team
   - **Organization Identifier**: `philippos` (or your own)
   - **Interface**: SwiftUI
   - **Language**: Swift
   - **Storage**: None (or your preference)
6. Choose a location to save the project
7. Click "Create"

## Step 2: Add Widget Extension

1. In Xcode, go to **File** > **New** > **Target**
2. Select **Widget Extension**
3. Click "Next"
4. Fill in:
   - **Product Name**: `WeekNumberWidgetExtension`
   - **Include Configuration Intent**: Unchecked (we're using static configuration)
5. Click "Finish"
6. When prompted, click "Activate" to activate the scheme

## Step 3: Configure App Groups

1. Select the project in the navigator
2. Select the **WeekNumberWidget** target
3. Go to **Signing & Capabilities** tab
4. Click **+ Capability**
5. Add **App Groups**
6. Check the box and add: `group.philippos.Week-Number-Widget`
7. Repeat steps 2-6 for the **WeekNumberWidgetExtension** target
8. Make sure both targets use the same App Group identifier

## Step 4: Replace Default Files

Replace the default files with the provided Swift files:

### Main App Files (WeekNumberWidget target):
- `WeekNumberWidgetApp.swift` - Replace the default App file
- `ContentView.swift` - Replace the default ContentView
- `AboutView.swift` - Add new file
- `WidgetSettingsView.swift` - Add new file
- `WeekNumberCalculator.swift` - Add new file
- `WidgetPreferences.swift` - Add new file

### Widget Extension Files (WeekNumberWidgetExtension target):
- `WeekNumberWidgetBundle.swift` - Replace the default bundle file
- `WeekNumberWidgetExtension.swift` - Replace/add widget definition
- `WeekNumberTimelineProvider.swift` - Add new file
- `WeekNumberWidgetEntryView.swift` - Replace/add widget view

## Step 5: Add Files to Targets

Make sure the shared files are added to both targets:

1. Select `WeekNumberCalculator.swift` in the navigator
2. In the File Inspector (right panel), under **Target Membership**, check:
   - ✅ WeekNumberWidget
   - ✅ WeekNumberWidgetExtension

3. Repeat for `WidgetPreferences.swift`

## Step 6: Update Info.plist Files

Replace the Info.plist files in both targets with the provided ones, or update the bundle identifiers to match your project.

## Step 7: Configure Entitlements

1. For the **WeekNumberWidget** target:
   - Go to **Signing & Capabilities**
   - The App Groups capability should already be there
   - Make sure the entitlements file references the App Group

2. For the **WeekNumberWidgetExtension** target:
   - Same as above

## Step 8: Build and Run

1. Select a simulator or connected device
2. Select the **WeekNumberWidget** scheme (not the extension)
3. Press **Cmd+R** to build and run
4. The app should launch on your device/simulator

## Step 9: Test the Widget

1. After the app runs, go to the home screen
2. Long press on the home screen
3. Tap the **+** button
4. Search for "Week Number Widget"
5. Add the widget to your home screen

## Troubleshooting

### Widget doesn't appear
- Make sure both targets have the App Group configured
- Check that the bundle identifiers match
- Clean build folder (Cmd+Shift+K) and rebuild

### Widget doesn't update
- Check that App Groups are properly configured
- Verify UserDefaults suite name matches in both targets
- Try removing and re-adding the widget

### Build errors
- Make sure all Swift files are added to the correct targets
- Check that imports are correct (WidgetKit for widget files)
- Verify iOS deployment target is 17.0 or later

## Notes

- The widget updates daily at midnight
- Settings are shared between the app and widget via App Groups
- The widget supports Small, Medium, and Large sizes
- All customization is done through the main app's settings

