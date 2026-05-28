import SwiftUI

/// Color choices offered in the widget's Edit configuration.
enum WidgetTint: String, CaseIterable, Identifiable {
    case system
    case white
    case black
    case purple
    case blue
    case red
    case green
    case orange
    case teal
    case gray

    var id: String { rawValue }

    var displayName: String {
        rawValue.prefix(1).uppercased() + rawValue.dropFirst()
    }

    /// nil means "use the system primary color" (adapts to light/dark).
    var color: Color? {
        switch self {
        case .system: return nil
        case .white:  return .white
        case .black:  return .black
        case .purple: return Color(red: 0.38, green: 0.0, blue: 0.93)
        case .blue:   return .blue
        case .red:    return .red
        case .green:  return .green
        case .orange: return .orange
        case .teal:   return .teal
        case .gray:   return .gray
        }
    }
}
