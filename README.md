## Week Number Widget

**Week Number Widget** is a clean, minimal widget that shows the **current week number** right on your home screen (and lock screen where supported), for both **Android** and **iOS**.

It follows the **ISO 8601 standard** by default (weeks start on Monday) and supports dark/light mode, so you always know exactly which week of the year you’re in at a glance.

### Platforms

- **Android (`android/`)**
  - Home screen and lock screen widget
  - Automatic dark/light theme based on system settings
  - Week numbers calculated using the ISO 8601 standard
  - Built with Kotlin and Android Studio (min SDK 26, target SDK 34)

- **iOS (`ios/`)**
  - Home screen widget built with SwiftUI + WidgetKit
  - ISO 8601 week calculation, plus optional Sunday‑start mode (US/Canada style)
  - Customizable colors, label visibility, and week start day
  - Requires iOS 17+ and Xcode 15+

### Getting Started

#### Android

1. Open the project in **Android Studio**:
   - `WeekNumber-Widget/android`
2. Sync Gradle files.
3. Build and run on an emulator or device.
4. Add the widget from the Android **Widgets** picker.

For more details, see `android/README.md`.

#### iOS

1. Open `ios/WeekNumberWidget.xcodeproj` in **Xcode**.
2. Configure your **Signing & Capabilities** and App Group if needed.
3. Build and run on a simulator or device.
4. Long‑press the home screen → **+** → search for **“Week Number Widget”** and add it.

For more details, see `ios/README_iOS.md`.

### License

This project is open source under the **MIT License**. See `LICENSE` for details.


