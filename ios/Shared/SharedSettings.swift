import Foundation
import os

/// App-wide widget defaults, shared between the app and the widget extension
/// through an App Group. A widget configured with the "Match app" option reads
/// these values; otherwise the per-widget AppIntent settings win.
enum SharedSettings {
    static let appGroup = "group.com.weeknumber.widget"

    private static let log = Logger(subsystem: "com.weeknumber.widget", category: "SharedSettings")

    /// Resolved once. nil only if the App Group entitlement is missing/unsigned,
    /// in which case we log loudly so the failure isn't silent.
    private static let store: UserDefaults? = {
        let defaults = UserDefaults(suiteName: appGroup)
        if defaults == nil {
            log.error("App Group \(appGroup, privacy: .public) unavailable — shared colours will not persist. Check the App Group entitlement on both the app and the widget extension.")
        }
        return defaults
    }()

    private enum Key {
        static let textColor = "app.textColorHex"
        static let backgroundColor = "app.backgroundColorHex"
    }

    /// Hex like "#RRGGBB" or "#AARRGGBB"; nil means "not set, fall back".
    static var textColorHex: String? {
        get { store?.string(forKey: Key.textColor) }
        set { store?.set(newValue, forKey: Key.textColor) }
    }

    static var backgroundColorHex: String? {
        get { store?.string(forKey: Key.backgroundColor) }
        set { store?.set(newValue, forKey: Key.backgroundColor) }
    }
}
