import Foundation

/// Mirrors the Android WeekNumberCalculator.
/// ISO 8601: Monday start, week 1 = first week with >= 4 days.
/// US style: Sunday start, week 1 contains January 1.
enum WeekStart: String, CaseIterable {
    case monday
    case sunday
}

enum WeekNumberCalculator {

    static func currentWeekNumber(weekStart: WeekStart, date: Date = Date()) -> Int {
        var calendar = Calendar(identifier: .gregorian)
        switch weekStart {
        case .monday:
            calendar.firstWeekday = 2          // Monday
            calendar.minimumDaysInFirstWeek = 4 // ISO 8601
        case .sunday:
            calendar.firstWeekday = 1          // Sunday
            calendar.minimumDaysInFirstWeek = 1 // US/Canada
        }
        return calendar.component(.weekOfYear, from: date)
    }

    static func currentYear(date: Date = Date()) -> Int {
        Calendar(identifier: .gregorian).component(.year, from: date)
    }

    /// Localized word for "Week" based on the current system language.
    /// Mirrors the Android getWeekLabelForLanguage table.
    static func weekLabel(languageCode: String = Locale.current.language.languageCode?.identifier ?? "en") -> String {
        switch languageCode {
        case "en": return "Week"
        case "es": return "Semana"
        case "fr": return "Semaine"
        case "de": return "Woche"
        case "it": return "Settimana"
        case "pt": return "Semana"
        case "nl": return "Week"
        case "ru": return "Неделя"
        case "zh": return "周"
        case "ja": return "週"
        case "ko": return "주"
        case "ar": return "أسبوع"
        default:   return "Week"
        }
    }
}
