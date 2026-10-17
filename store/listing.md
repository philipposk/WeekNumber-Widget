# Store listing copy

Generated marketing assets live in `store/play/` (Google Play) and
`store/appstore/` (App Store). Source SVGs are in `store/src/` — re-render with:

```bash
DYLD_FALLBACK_LIBRARY_PATH=/opt/homebrew/lib \
  cairosvg store/src/shot1.svg -o store/play/shot1.png -W 1080 -H 1920
```

> Note: these are clean illustrative mockups. Before submission, you may also
> capture real device/simulator screenshots — both stores accept either.

---

## App name
**Week Number — Widget**

## Subtitle (App Store, ≤30 chars)
Current week, at a glance

## Short description (Google Play, ≤80 chars)
The current week number on your Home & Lock Screen. ISO 8601 or US. No ads.

## Full description

**Always know which week of the year it is.**

Week Number puts the current week number right on your Home Screen and Lock
Screen, so you never have to count or look it up again. It follows the ISO 8601
standard (weeks start on Monday) by default, with an optional US/Canada mode
(weeks start on Sunday).

**Features**
- Home Screen widget in multiple sizes
- Lock Screen widgets on iPhone (circular, rectangular, inline)
- ISO 8601 (Monday) or US/Canada (Sunday) week numbering
- Per-widget colours, or “Match app” to use your own colour
- Light and dark mode, automatically
- The “Week” label in 30+ languages
- A “this week” panel: date range, days left in the year, year progress
- Share the current week number to any app
- Updates automatically at midnight

**Private by design**
No accounts. No network. No analytics. No data collected — everything runs on
your device.

Perfect for anyone who plans by week numbers — common across Europe, logistics,
manufacturing, schools, and project work.

## Keywords (App Store, comma-separated, ≤100 chars)
week number,week,ISO 8601,calendar,widget,lock screen,year,planner,date,kalenderwoche

## Category
Productivity

## Content rating
Everyone / 4+

## Asset inventory
| Asset | Size | File |
|---|---|---|
| Play feature graphic | 1024×500 | `play/feature_graphic.png` |
| Play screenshots | 1080×1920 | `play/shot1.png`, `play/shot2.png`, `play/shot3.png` |
| App Store 6.7" screenshots | 1290×2796 | `appstore/shot1.png`, `appstore/shot2.png`, `appstore/shot3.png` |
| App icon (master) | 1024×1024 | `../design/icon_master.svg` → rendered into both apps |
