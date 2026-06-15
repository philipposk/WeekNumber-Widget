# AGENTS.md

Guidance for AI agents and new contributors working in this repo.

## Layout

```
android/   Kotlin + Gradle app (home-screen App Widget)
ios/       SwiftUI + WidgetKit app (home + lock screen widgets), XcodeGen-managed
AUDIT.md   Standing code-review findings + what's fixed / deferred
```

The two apps are independent codebases that share only a concept and a localized
"Week" label table. There is no shared build.

## Android

- Build: `cd android && ./gradlew assembleDebug`
- Test:  `cd android && ./gradlew testDebugUnitTest`
- JDK 17 required. The `.gradle`, `build/`, `local.properties` dirs are git-ignored.
- Min SDK 26, target SDK 34. `applicationId` / `namespace`: `com.weeknumber.widget`.
- Widget logic: `app/src/main/java/com/weeknumber/widget/widget/`
  - `HomeScreenWidgetProvider` renders; `WidgetSizing` owns all font/layout math
    (single source of truth — do not duplicate sizing logic back into the provider).
  - `WidgetConfigureActivity` has two modes: first-time setup (`APPWIDGET_CONFIGURE`,
    must `setResult`) and runtime reconfigure (`ACTION_RECONFIGURE`). Preserve both.
  - `WidgetPreferences` stores one `SharedPreferences` file per widget id.

## iOS

- The Xcode project is **generated** from `ios/project.yml` by XcodeGen.
  Never hand-edit `WeekNumberWidget.xcodeproj`; edit `project.yml` then run
  `xcodegen generate`.
- Build (sim): `cd ios && xcodegen generate && xcodebuild -scheme WeekNumberWidget -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build`
- Test: `xcodebuild test -scheme WeekNumberWidget -destination 'platform=iOS Simulator,name=iPhone 16' CODE_SIGNING_ALLOWED=NO`
- Min iOS 17 (required by `AppIntentConfiguration`).
- `Shared/` is compiled into the app **and** the widget extension. Keep it
  Foundation-only (no UIKit) so it stays portable and unit-testable.
- Widget settings use the native **Edit Widget** sheet (per-widget), backed by
  `ConfigurationAppIntent`. There is intentionally no App Group yet.

## House rules

- Don't delete existing code without a reason in the PR description.
- Keep `Shared` week math identical in behavior across both platforms (ISO Monday /
  US Sunday). There are unit tests on both sides — update them when behavior changes.
- No network, no analytics, no PII. Keep it that way (see `PRIVACY_POLICY_TEMPLATE.md`).
- Both platforms localize the "Week" label from one table; add new languages to
  **both** `WidgetPreferences.getWeekLabelForLanguage` (Android) and
  `WeekNumberCalculator.weekLabel` (iOS).
