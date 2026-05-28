# Week Number Widget

Week Number Widget is a small, clean phone widget that shows the current week number — like "Week 22" — right on your home screen and lock screen. It's for anyone who thinks in week numbers (common in Europe and at work) and wants to know which week it is at a glance, without opening an app. Available for both Android and iPhone.

## What it does
- Shows the current week number on your home screen (and lock screen where supported)
- Counts weeks the standard European way by default (weeks start on Monday)
- iPhone version also offers a US-style mode (weeks start on Sunday)
- Switches automatically between light and dark to match your phone
- Lets you customise colours and labels (on iPhone)

## Status
Phone app (widget) for Android and iPhone. The Android and iPhone versions live in separate folders within this project.

---
### For developers
Two native builds:
- `android/` — Kotlin, built in Android Studio. Home screen and lock screen widget, automatic dark/light theme, ISO 8601 week numbers. Min SDK 26, target SDK 34. See `android/README.md`.
- `ios/` — SwiftUI + WidgetKit. ISO 8601 by default with optional Sunday-start mode, customisable colours and labels. Requires iOS 17+ and Xcode 15+. See `ios/README_iOS.md`.

Android: open `android/` in Android Studio, sync Gradle, build and run. iOS: open the Xcode project in `ios/`, set Signing & Capabilities, build and run. The marketing website for this app is a separate project (Week-Number-Widget--site-).
