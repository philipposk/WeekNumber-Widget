# Contributing

Thanks for helping out. This repo holds two small, independent apps (Android and
iOS) that display the current ISO/US week number as a home/lock-screen widget.

## Before you start

- Read `AGENTS.md` for the build commands and the few architectural rules.
- Pick a platform — `android/` and `ios/` don't depend on each other.

## Workflow

1. Branch from `main` (`feature/...` or `fix/...`).
2. Make the change. Keep diffs focused and reviewable.
3. Run the tests for the platform you touched:
   - Android: `cd android && ./gradlew testDebugUnitTest`
   - iOS: `cd ios && xcodegen generate && xcodebuild test -scheme WeekNumberWidget -destination 'platform=iOS Simulator,name=iPhone 16' CODE_SIGNING_ALLOWED=NO`
4. Open a PR. CI runs both platforms automatically.

## Conventions

- Don't commit build output (`android/app/build`, `ios/build`, `ios/DerivedData`,
  the generated `*.xcodeproj`). They're git-ignored.
- Adding a language? Update the label table on **both** platforms (see AGENTS.md).
- Keep the privacy posture: no network calls, no analytics, no personal data.
- Week-number math changes must update the unit tests on both platforms.

## Reporting bugs

Open an issue with: device + OS version, launcher (Android) or widget family
(iOS), the configured week-start, and what you expected vs. saw. A screenshot of
the widget helps a lot.
