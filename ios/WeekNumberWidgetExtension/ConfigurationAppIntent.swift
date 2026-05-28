import WidgetKit
import AppIntents

/// Backs the "Edit Widget" screen (long-press a widget → Edit Widget).
struct ConfigurationAppIntent: WidgetConfigurationIntent {
    static var title: LocalizedStringResource = "Week Number"
    static var description = IntentDescription("Show the current week number.")

    @Parameter(title: "Week starts on Monday", default: true)
    var startsOnMonday: Bool

    @Parameter(title: "Show \"Week\" label", default: true)
    var showLabel: Bool

    @Parameter(title: "Text color", default: .system)
    var tint: TintChoice

    @Parameter(title: "Background", default: .clear)
    var background: BackgroundChoice

    var weekStart: WeekStart { startsOnMonday ? .monday : .sunday }
}

/// AppEnum wrappers so the values appear as pickers in the Edit screen.
enum TintChoice: String, AppEnum {
    case system, white, black, purple, blue, red, green, orange, teal, gray

    static var typeDisplayRepresentation = TypeDisplayRepresentation(name: "Text color")
    static var caseDisplayRepresentations: [TintChoice: DisplayRepresentation] = [
        .system: "System",
        .white: "White",
        .black: "Black",
        .purple: "Purple",
        .blue: "Blue",
        .red: "Red",
        .green: "Green",
        .orange: "Orange",
        .teal: "Teal",
        .gray: "Gray",
    ]

    var widgetTint: WidgetTint { WidgetTint(rawValue: rawValue) ?? .system }
}

enum BackgroundChoice: String, AppEnum {
    case clear, white, black, gray

    static var typeDisplayRepresentation = TypeDisplayRepresentation(name: "Background")
    static var caseDisplayRepresentations: [BackgroundChoice: DisplayRepresentation] = [
        .clear: "Clear",
        .white: "White",
        .black: "Black",
        .gray: "Gray",
    ]
}
