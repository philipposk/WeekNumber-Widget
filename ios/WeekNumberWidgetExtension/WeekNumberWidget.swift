import WidgetKit
import SwiftUI

// MARK: - Timeline

struct WeekEntry: TimelineEntry {
    let date: Date
    let configuration: ConfigurationAppIntent
}

struct Provider: AppIntentTimelineProvider {
    func placeholder(in context: Context) -> WeekEntry {
        WeekEntry(date: Date(), configuration: ConfigurationAppIntent())
    }

    func snapshot(for configuration: ConfigurationAppIntent, in context: Context) async -> WeekEntry {
        WeekEntry(date: Date(), configuration: configuration)
    }

    /// Two entries — now and just after the next midnight — so the number ticks
    /// over on its own at the day boundary, with a refresh scheduled past it.
    func timeline(for configuration: ConfigurationAppIntent, in context: Context) async -> Timeline<WeekEntry> {
        let now = Date()
        let nextMidnight = Calendar.current.nextDate(
            after: now,
            matching: DateComponents(hour: 0, minute: 0, second: 5),
            matchingPolicy: .nextTime
        ) ?? now.addingTimeInterval(3600)
        let entries = [
            WeekEntry(date: now, configuration: configuration),
            WeekEntry(date: nextMidnight, configuration: configuration),
        ]
        return Timeline(entries: entries, policy: .after(nextMidnight))
    }
}

// MARK: - Views

struct WeekNumberWidgetView: View {
    @Environment(\.widgetFamily) private var family
    var entry: WeekEntry

    private var weekNumber: Int {
        WeekNumberCalculator.currentWeekNumber(
            weekStart: entry.configuration.weekStart,
            date: entry.date
        )
    }

    private var textColor: Color {
        switch entry.configuration.tint {
        case .matchApp:
            if let hex = SharedSettings.textColorHex, let c = Color(hex: hex) { return c }
            return .primary
        default:
            return entry.configuration.tint.widgetTint.color ?? .primary
        }
    }

    private var isAccessory: Bool {
        family == .accessoryCircular || family == .accessoryRectangular || family == .accessoryInline
    }

    private var a11yLabel: String {
        "\(WeekNumberCalculator.weekLabel()) \(weekNumber)"
    }

    var body: some View {
        Group {
            switch family {
            case .accessoryInline:
                Text("\(WeekNumberCalculator.weekLabel()) \(weekNumber)")
            case .accessoryCircular:
                circular
            case .accessoryRectangular:
                rectangular
            case .systemLarge:
                largeScreen
            default:
                homeScreen
            }
        }
        .containerBackground(for: .widget) {
            backgroundColor
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(a11yLabel)
    }

    @ViewBuilder private var backgroundColor: some View {
        switch entry.configuration.background {
        case .matchApp:
            if let hex = SharedSettings.backgroundColorHex, let c = Color(hex: hex) { c } else { Color.clear }
        case .clear: Color.clear
        case .white: Color.white
        case .black: Color.black
        case .gray:  Color.gray.opacity(0.3)
        }
    }

    private var homeScreen: some View {
        VStack(spacing: 2) {
            if entry.configuration.showLabel {
                Text(WeekNumberCalculator.weekLabel().uppercased())
                    .font(.caption.weight(.semibold))
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
                    .foregroundStyle(textColor.opacity(0.7))
            }
            Text("\(weekNumber)")
                .font(.system(size: 200, weight: .bold, design: .rounded))
                .minimumScaleFactor(0.1)
                .lineLimit(1)
                .foregroundStyle(textColor)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(8)
    }

    private var largeScreen: some View {
        let range = WeekNumberCalculator.weekRange(weekStart: entry.configuration.weekStart, date: entry.date)
        let fmt: DateFormatter = {
            let f = DateFormatter()
            f.setLocalizedDateFormatFromTemplate("MMMd")
            return f
        }()
        let rangeText = range.map { "\(fmt.string(from: $0.start)) – \(fmt.string(from: $0.end))" } ?? ""
        let daysLeft = WeekNumberCalculator.daysRemainingInYear(date: entry.date)

        return VStack(spacing: 4) {
            if entry.configuration.showLabel {
                Text(WeekNumberCalculator.weekLabel().uppercased())
                    .font(.title3.weight(.semibold))
                    .foregroundStyle(textColor.opacity(0.7))
            }
            Text("\(weekNumber)")
                .font(.system(size: 150, weight: .bold, design: .rounded))
                .minimumScaleFactor(0.3)
                .lineLimit(1)
                .foregroundStyle(textColor)
            VStack(spacing: 2) {
                Text(rangeText)
                Text("\(daysLeft) days left in the year")
            }
            .font(.headline)
            .foregroundStyle(textColor.opacity(0.6))
            .padding(.top, 8)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(16)
    }

    private var circular: some View {
        VStack(spacing: 0) {
            if entry.configuration.showLabel {
                Text(WeekNumberCalculator.weekLabel())
                    .font(.system(size: 11, weight: .medium))
            }
            Text("\(weekNumber)")
                .font(.system(size: 28, weight: .bold, design: .rounded))
                .minimumScaleFactor(0.5)
        }
    }

    private var rectangular: some View {
        HStack(alignment: .firstTextBaseline, spacing: 6) {
            if entry.configuration.showLabel {
                Text(WeekNumberCalculator.weekLabel())
                    .font(.headline)
            }
            Text("\(weekNumber)")
                .font(.system(.largeTitle, design: .rounded).weight(.bold))
        }
    }
}

// MARK: - Widget

struct WeekNumberWidget: Widget {
    let kind = "WeekNumberWidget"

    var body: some WidgetConfiguration {
        AppIntentConfiguration(
            kind: kind,
            intent: ConfigurationAppIntent.self,
            provider: Provider()
        ) { entry in
            WeekNumberWidgetView(entry: entry)
        }
        .configurationDisplayName("Week Number")
        .description("Shows the current week number.")
        .supportedFamilies([
            .systemSmall,
            .systemMedium,
            .systemLarge,
            .accessoryCircular,
            .accessoryRectangular,
            .accessoryInline,
        ])
    }
}
