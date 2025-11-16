# Android to iOS App Conversion Guide

This guide provides step-by-step instructions for converting an Android app to iOS, based on lessons learned from converting the Week Number Widget app. Follow these instructions to avoid common errors and build issues.

## Prerequisites

- Xcode installed (latest version)
- Apple Developer account (for device testing)
- Understanding of the Android app structure

## 1. Initial Project Setup

### Create Xcode Project
1. Open Xcode → Create New Project
2. Choose **iOS** → **App**
3. Set:
   - **Product Name**: Your app name (e.g., "Week Number Widget")
   - **Team**: Your development team
   - **Organization Identifier**: Your identifier (e.g., `philippos`)
   - **Bundle Identifier**: Will be `{OrganizationIdentifier}.{ProductName}` (e.g., `philippos.Week-Number-Widget`)
   - **Interface**: SwiftUI
   - **Language**: Swift
   - **Storage**: None (or Core Data if needed)

### Important: Use File System Synchronized Build System
- The project should use **objectVersion = 77** (File System Synchronized Build System)
- This is the default in newer Xcode versions
- Files are automatically included based on folder structure

## 2. Project Structure

Create the following folder structure:
```
YourApp/
├── YourApp/                    # Main app folder
│   ├── YourAppApp.swift        # @main entry point
│   ├── ContentView.swift       # Main view
│   ├── AboutView.swift         # About screen (if needed)
│   ├── SettingsView.swift      # Settings screen (if needed)
│   ├── Calculator.swift        # Business logic (translated from Kotlin)
│   ├── Preferences.swift       # UserDefaults wrapper
│   ├── Info.plist              # App Info.plist
│   ├── Assets.xcassets         # Images/assets
│   └── YourApp.entitlements    # App entitlements
└── YourAppExtension/           # Widget extension folder
    ├── YourAppBundle.swift     # @main widget bundle
    ├── YourAppWidget.swift     # Widget definition
    ├── TimelineProvider.swift  # Widget update logic
    ├── WidgetEntryView.swift   # Widget UI
    ├── Info.plist              # Extension Info.plist
    └── YourAppExtension.entitlements
```

## 3. Widget Extension Setup

### Add Widget Extension Target
1. In Xcode: **File** → **New** → **Target**
2. Choose **Widget Extension**
3. Set:
   - **Product Name**: `YourAppExtension` (NOT the same as main app)
   - **Include Configuration Intent**: NO (unless you need user configuration)
4. **DO NOT** check "Activate" - we'll configure it manually

### Critical: Widget Extension Info.plist Configuration

The extension's `Info.plist` **MUST** include the `NSExtension` dictionary:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleDevelopmentRegion</key>
    <string>$(DEVELOPMENT_LANGUAGE)</string>
    <key>CFBundleDisplayName</key>
    <string>Your Widget Name</string>
    <key>CFBundleExecutable</key>
    <string>$(EXECUTABLE_NAME)</string>
    <key>CFBundleIdentifier</key>
    <string>$(PRODUCT_BUNDLE_IDENTIFIER)</string>
    <key>CFBundleInfoDictionaryVersion</key>
    <string>6.0</string>
    <key>CFBundleName</key>
    <string>$(PRODUCT_NAME)</string>
    <key>CFBundlePackageType</key>
    <string>$(PRODUCT_BUNDLE_PACKAGE_TYPE)</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>CFBundleVersion</key>
    <string>1</string>
    <key>NSExtension</key>
    <dict>
        <key>NSExtensionPointIdentifier</key>
        <string>com.apple.widgetkit-extension</string>
    </dict>
    <key>NSHumanReadableCopyright</key>
    <string>Copyright © 2025. All rights reserved.</string>
</dict>
</plist>
```

### Prevent Info.plist from Being Copied as Resource

In `project.pbxproj`, add `Info.plist` to the exception set for the widget extension target:

```pbxproj
6DDB47022EC6E30E000CA098 /* Exceptions for "YourAppExtension" folder in "YourAppExtension" target */ = {
    isa = PBXFileSystemSynchronizedBuildFileExceptionSet;
    membershipExceptions = (
        YourAppApp.swift,           // Exclude main app entry point
        SettingsView.swift,         // Exclude main app views
        Info.plist,                  // CRITICAL: Prevent Info.plist from being copied as resource
    );
    target = 6DDB46D22EC6E1CD000CA098 /* YourAppExtension */;
};
```

### Build Settings for Extension

In the extension target's build settings:
- `GENERATE_INFOPLIST_FILE = NO`
- `INFOPLIST_FILE = YourAppExtension/Info.plist`
- `INFOPLIST_PREPROCESS = YES`
- `CODE_SIGN_IDENTITY = "Apple Development"` (for both Debug and Release)

## 4. App Groups Setup (for Shared Data)

If your widget needs to share data with the main app:

### Create App Group
1. In Apple Developer Portal: **Certificates, Identifiers & Profiles** → **Identifiers**
2. Create new **App Group**: `group.{OrganizationIdentifier}.{AppName}`
   - Example: `group.philippos.Week-Number-Widget`

### Add to Entitlements
**Main App** (`YourApp.entitlements`):
```xml
<key>com.apple.security.application-groups</key>
<array>
    <string>group.philippos.Week-Number-Widget</string>
</array>
```

**Extension** (`YourAppExtension.entitlements`):
```xml
<key>com.apple.security.application-groups</key>
<array>
    <string>group.philippos.Week-Number-Widget</string>
</array>
```

### Use in Code
```swift
// In Preferences.swift
let appGroupIdentifier = "group.philippos.Week-Number-Widget"
let sharedDefaults = UserDefaults(suiteName: appGroupIdentifier)!
```

## 5. Code Translation Patterns

### Kotlin → Swift

**UserDefaults (SharedPreferences)**
```kotlin
// Android (Kotlin)
val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
prefs.edit().putString("key", "value").apply()
val value = prefs.getString("key", "default")
```

```swift
// iOS (Swift)
let defaults = UserDefaults(suiteName: "group.philippos.Week-Number-Widget")!
defaults.set("value", forKey: "key")
let value = defaults.string(forKey: "key") ?? "default"
```

**Date/Calendar Logic**
```kotlin
// Android
val calendar = Calendar.getInstance()
calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
```

```swift
// iOS
var calendar = Calendar(identifier: .iso8601)
calendar.firstWeekday = 2 // Monday
```

**Colors**
```kotlin
// Android
val color = Color.parseColor("#FF5733")
```

```swift
// iOS
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
```

## 6. Widget-Specific Code

### Widget Background (iOS 17+ Compatibility)

Create a View extension for widget backgrounds:

```swift
import WidgetKit
import SwiftUI

extension View {
    @ViewBuilder
    func widgetBackground(_ color: Color) -> some View {
        if #available(iOS 17.0, *) {
            self.containerBackground(color, for: .widget)
        } else {
            self.background(color)
        }
    }
}
```

Use in widget view:
```swift
VStack {
    Text("Week 46")
}
.widgetBackground(Color.blue)
```

### Widget Entry Structure
```swift
struct WidgetEntry: TimelineEntry {
    let date: Date
    let weekNumber: Int
    let backgroundColor: Color
    let textColor: Color
    // ... other properties
}
```

### Timeline Provider
```swift
struct WidgetTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> WidgetEntry {
        WidgetEntry(date: Date(), weekNumber: 1, ...)
    }
    
    func getSnapshot(in context: Context, completion: @escaping (WidgetEntry) -> Void) {
        let entry = WidgetEntry(date: Date(), weekNumber: calculateWeek(), ...)
        completion(entry)
    }
    
    func getTimeline(in context: Context, completion: @escaping (Timeline<WidgetEntry>) -> Void) {
        let entry = WidgetEntry(date: Date(), weekNumber: calculateWeek(), ...)
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdateDate))
        completion(timeline)
    }
}
```

## 7. Build Scripts (Optional but Recommended)

Add build scripts to ensure `NSExtension` dictionary is present in the built extension:

### Script 1: Fix Extension Info.plist
Add to **Widget Extension** target → **Build Phases** → **+** → **New Run Script Phase**:

```bash
echo "========================================"
echo "[Fix NSExtension] Script starting"
APPEX_PATH="${BUILT_PRODUCTS_DIR}/${WRAPPER_NAME}"
INFO_PLIST="${APPEX_PATH}/Info.plist"
echo "[Fix NSExtension] Checking: $INFO_PLIST"

if [ ! -f "$INFO_PLIST" ]; then
    echo "[Fix NSExtension] ERROR: Info.plist not found"
    exit 1
fi

echo "[Fix NSExtension] Info.plist found, checking for NSExtension..."

if ! /usr/libexec/PlistBuddy -c "Print NSExtension" "$INFO_PLIST" >/dev/null 2>&1; then
    echo "[Fix NSExtension] NSExtension missing, adding it..."
    /usr/libexec/PlistBuddy -c "Add NSExtension dict" "$INFO_PLIST" 2>&1
    /usr/libexec/PlistBuddy -c "Add NSExtension:NSExtensionPointIdentifier string com.apple.widgetkit-extension" "$INFO_PLIST" 2>&1
    echo "[Fix NSExtension] ✓ Added NSExtension to Info.plist"
else
    echo "[Fix NSExtension] ✓ NSExtension already present"
fi

echo "[Fix NSExtension] Script completed"
echo "========================================"
```

**Settings:**
- **Shell**: `/bin/sh`
- **Run script only when installing**: ✅ (checked)
- **Show environment variables in build log**: ✅ (optional)

### Script 2: Fix Embedded Extension Info.plist
Add to **Main App** target → **Build Phases** → **+** → **New Run Script Phase**:

```bash
echo "========================================"
echo "[Fix Embedded Extension] Script starting"
APP_BUNDLE="${BUILT_PRODUCTS_DIR}/${WRAPPER_NAME}"
echo "APP_BUNDLE: $APP_BUNDLE"

# Find all appex files in PlugIns
find "${APP_BUNDLE}/PlugIns" -name "*.appex" -type d 2>/dev/null | while read appex; do
    echo "[Fix Embedded Extension] Found appex: $appex"
    EXT_INFO="${appex}/Info.plist"
    if [ -f "$EXT_INFO" ]; then
        echo "[Fix Embedded Extension] Checking Info.plist: $EXT_INFO"
        if ! /usr/libexec/PlistBuddy -c "Print NSExtension" "$EXT_INFO" >/dev/null 2>&1; then
            echo "[Fix Embedded Extension] NSExtension missing, adding..."
            /usr/libexec/PlistBuddy -c "Add NSExtension dict" "$EXT_INFO" 2>&1 || echo "Failed to add dict"
            /usr/libexec/PlistBuddy -c "Add NSExtension:NSExtensionPointIdentifier string com.apple.widgetkit-extension" "$EXT_INFO" 2>&1 || echo "Failed to add identifier"
            echo "[Fix Embedded Extension] ✓ Fixed $EXT_INFO"
        else
            echo "[Fix Embedded Extension] ✓ NSExtension already present in $EXT_INFO"
        fi
    else
        echo "[Fix Embedded Extension] Info.plist not found in $appex"
    fi
done

echo "[Fix Embedded Extension] Script completed"
echo "========================================"
```

**Settings:**
- **Shell**: `/bin/sh`
- **Run script only when installing**: ✅ (checked)

## 8. Target Membership

### Ensure Correct File Membership

**Main App Target** should include:
- `YourAppApp.swift` (with `@main`)
- `ContentView.swift`
- `AboutView.swift`
- `SettingsView.swift`
- `Calculator.swift`
- `Preferences.swift`
- Main app's `Info.plist`
- Main app's `Assets.xcassets`

**Widget Extension Target** should include:
- `YourAppBundle.swift` (with `@main`)
- `YourAppWidget.swift`
- `TimelineProvider.swift`
- `WidgetEntryView.swift`
- Extension's `Info.plist`
- `Calculator.swift` (shared logic)
- `Preferences.swift` (shared logic)

**Widget Extension Target** should NOT include:
- `YourAppApp.swift`
- `ContentView.swift`
- `AboutView.swift`
- `SettingsView.swift`

### File System Synchronized Build System Exceptions

In `project.pbxproj`, configure exceptions:

```pbxproj
/* Exceptions for "YourAppExtension" folder in "Main App" target */
membershipExceptions = (
    Calculator.swift,        // Exclude from main app (if in extension folder)
    Preferences.swift,       // Exclude from main app (if in extension folder)
);

/* Exceptions for "YourAppExtension" folder in "Extension" target */
membershipExceptions = (
    YourAppApp.swift,        // Exclude main app entry point
    SettingsView.swift,      // Exclude main app views
    Info.plist,              // CRITICAL: Prevent Info.plist from being copied as resource
);
```

## 9. Deployment Target

Set minimum iOS version in both targets:
- **Main App**: iOS 14.0 (or your minimum requirement)
- **Extension**: iOS 14.0 (must match or be higher than main app)

In build settings:
- `IPHONEOS_DEPLOYMENT_TARGET = 14.0`

## 10. Code Signing

For development:
- **Code Signing Style**: Automatic
- **Development Team**: Your team
- **Code Sign Identity**: `Apple Development` (for both Debug and Release)

## 11. Schemes

Ensure schemes are properly configured:
- **Main App Scheme**: Should build and run the main app
- **Extension Scheme**: Should build the extension (usually not run directly)

Schemes should be in: `YourApp.xcodeproj/xcshareddata/xcschemes/`

## 12. Common Errors and Solutions

### Error: "Multiple commands produce"
**Cause**: `Info.plist` is being both processed and copied as a resource.

**Solution**: Add `Info.plist` to the exception set for the extension target.

### Error: "NSExtension dictionary missing"
**Cause**: Extension's `Info.plist` doesn't have `NSExtension` dictionary.

**Solution**: 
1. Add `NSExtension` dictionary to `Info.plist` manually
2. Add build scripts (see section 7) to ensure it's present in built product

### Error: "'main' attribute can only apply to one type"
**Cause**: Both main app and extension have `@main` in the same target.

**Solution**: Ensure `YourAppApp.swift` is only in main app target, and `YourAppBundle.swift` is only in extension target.

### Error: "No Scheme" in dropdown
**Cause**: Project file is corrupted or schemes are missing.

**Solution**: 
1. Check `project.pbxproj` for syntax errors (balanced braces)
2. Verify schemes exist in `xcshareddata/xcschemes/`
3. Close and reopen Xcode

### Error: "please adopt container background api"
**Cause**: Widget not using `.containerBackground` on iOS 17+.

**Solution**: Use the `widgetBackground` extension (see section 6).

### Error: "Cannot find 'SomeView' in scope"
**Cause**: View file not included in target membership.

**Solution**: Check File Inspector → Target Membership, ensure file is checked for correct target.

## 13. Testing Checklist

- [ ] App builds without errors
- [ ] App runs on simulator
- [ ] App runs on physical device (requires Developer Mode)
- [ ] Widget appears in widget gallery
- [ ] Widget displays correctly on home screen
- [ ] Widget updates correctly
- [ ] Settings persist between app launches
- [ ] Settings sync between app and widget (if using App Groups)
- [ ] App works on minimum iOS version (iOS 14.0)
- [ ] App works on latest iOS version (iOS 17+)

## 14. Final Notes

1. **Always test on physical device** - Widget extensions can behave differently than simulators
2. **Check build logs** - Look for script output to verify fixes are applied
3. **Clean build folder** - Use `Shift + Cmd + K` before rebuilding after major changes
4. **Developer Mode** - Required on physical devices for development builds
5. **Code signing** - Ensure both targets use the same signing identity
6. **App Groups** - Must be configured in both Apple Developer Portal and entitlements

## Quick Reference: Key Files to Check

1. `project.pbxproj` - Project configuration, target membership, build phases
2. `Info.plist` (extension) - Must have `NSExtension` dictionary
3. `*.entitlements` - App Groups configuration
4. `xcshareddata/xcschemes/*.xcscheme` - Scheme definitions
5. Build scripts - Verify they run and fix `Info.plist`

---

**Remember**: The most common issues are:
1. `Info.plist` being copied as resource (add to exception set)
2. Missing `NSExtension` dictionary (add manually + build scripts)
3. Incorrect target membership (check File Inspector)
4. Missing App Groups (configure in portal + entitlements)

Follow this guide step-by-step to avoid these issues!

