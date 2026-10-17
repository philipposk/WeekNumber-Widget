# Audit — `main` snapshot (commit `183c4c8`)

Read-only review of the canonical repo. Findings grouped by severity. Items are
either fixed on this branch (`feature/audit-and-extras`) or carried as TODOs.

## Critical / correctness

- **C1 — Greek (and ~15 other) locales missing from the “Week” label table.**
  `WidgetPreferences.getWeekLabelForLanguage` (Android) and `WeekNumberCalculator.weekLabel`
  (iOS) ship 12 languages — but no Greek (`el`), Polish (`pl`), Turkish (`tr`),
  Scandinavian (`sv`/`da`/`no`/`fi`), Czech (`cs`), Romanian (`ro`), Hungarian (`hu`),
  Ukrainian (`uk`), Hebrew (`he`), Hindi (`hi`), Indonesian (`id`), Thai (`th`),
  Vietnamese (`vi`), Bulgarian (`bg`). For a widget owned by a Greek author this is
  the most user-visible gap. **Fixed on this branch.**
- **C2 — Android widget options come back in *cells* on some launchers, not dp.**
  `WidgetSizing.readSize` assumes the bundle ints from
  `AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH/HEIGHT` are dp. AOSP/most launchers
  return dp, but some Samsung One UI builds and MIUI return raw cell counts /
  pixels → number ends up clipped or microscopic. **Fix added on this branch:**
  reject obviously bogus values and fall back to a derived dp from `Configuration`.
- **C3 — Multi-widget picker labels are opaque.**
  `MainActivity.showWidgetPicker` prints `Widget 1`, `Widget 2`, … — user has no
  idea which is which. **Fix on this branch:** show each widget’s current week-start
  + label state in the picker row.
- **C4 — No iOS unit tests.** Android has 13 passing. iOS calc untested. **Fixed
  on this branch:** added `WeekNumberCalculatorTests` covering ISO/US, year
  boundaries, and the localized label table.

## Logic / minor

- **L1 — `WeekNumberCalculator.getCurrentWeekNumber` (Android) default branch
  silently uses Monday.** Fine for now, but the contract isn’t enforced: any
  unrecognized string falls into ISO. Acceptable.
- **L2 — `Calendar.getInstance()` (Android) uses the default locale’s calendar
  type.** In Thai/Saudi locales the default is Buddhist/Umm-al-Qura → week
  numbers diverge. Should be `Calendar.getInstance(Locale.US)` or an explicit
  Gregorian Calendar. **Fix on this branch:** force Gregorian.
- **L3 — `WidgetPreferences` is over-built.** `getLabelColor`, `getFontSize`,
  `getFontFamily`, `getTextStyle`, `getWidgetSize` are read-only by no one in the
  app. Kept (user asked: don’t delete code) — but flagged.
- **L4 — iOS `Provider.timeline` returns only the *current* entry.** WidgetKit
  will refresh near midnight per `.after(nextMidnight)`. Fine, but adding a 2nd
  entry “tomorrow at 00:00:05” makes the number visibly tick over without
  relying on the policy. **Done on this branch.**
- **L5 — iOS `accessoryInline` family uses `containerBackground`.** Inline
  widgets ignore backgrounds; harmless but noisy. Left as-is.

## UX

- **U1 — Android “Add widget” instructions don’t mention long-press → Edit
  Widget (Android 12+) for per-widget reconfigure.** Documentation, not code.
  **Updated string on this branch.**
- **U2 — iOS Monday/Sunday picker on the main screen affects only the in-app
  preview.** Subtitle already says so. Left as-is.
- **U3 — No share action.** Quick win: share the current week number to any
  app. **Added on this branch** (Android menu item + iOS `ShareLink`).
- **U4 — App shows the number but nothing about *time-of-year context*.**
  Days remaining, week start/end dates, year progress %. **Added on this
  branch** as a small “This week” block on both platforms.

## Security / privacy

- **S1 — No network, no analytics, no PII.** Genuinely a “runs on device” app —
  matches the privacy posture in `PRIVACY_POLICY_TEMPLATE.md`. Good.
- **S2 — Android `RECEIVE_BOOT_COMPLETED` is declared.** Used by `BootReceiver`
  to refresh widgets after reboot. Justified.
- **S3 — Color parsing trusts SharedPreferences strings.** `parseColor` throws
  `IllegalArgumentException` on bad input, caught and falls back to theme color.
  Safe.

## Performance

- **P1 — `HomeScreenWidgetProvider.updateAppWidget` builds and parses prefs on
  every update.** Cost is microseconds; widget updates are rare. Fine.
- **P2 — Android stores 1 `SharedPreferences` file per widget id.** Convenient
  for cleanup but expensive disk churn if you ever have 50 widgets. Realistic
  ceiling: ~5. Fine.

## Repo hygiene

- **H1 — Pre-merge `build/` and `DerivedData/` were tracked.** Now ignored;
  `.gitignore` cleaned up in the previous commit.
- **H2 — Top README references the now-removed Android lock-screen widget.**
  Updated copy on this branch.
- **H3 — No CI.** Builds are only verified locally. **Added on this branch:**
  GitHub Actions workflows for Android (`./gradlew testDebugUnitTest assembleDebug`)
  and iOS (`xcodegen + xcodebuild`). Catches regressions on every PR.
- **H4 — No `AGENTS.md` / `CONTRIBUTING.md`.** Added on this branch.

## Not changed in this pass (deferred)

- Free color picker (needs App Group on iOS; not requested now).
- Real launcher icon art (still the 7-dot vector).
- Store listing assets (screenshots, feature graphic).
- Android UI redesign.
- watchOS / Wear OS companions.
- Multi-week “next 3 weeks” widget — a thin variant worth doing, but out of
  scope for this branch to keep the diff reviewable.
