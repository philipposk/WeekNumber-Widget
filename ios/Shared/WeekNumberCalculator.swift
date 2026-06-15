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
        case "el": return "Εβδομάδα"
        case "pl": return "Tydzień"
        case "tr": return "Hafta"
        case "sv": return "Vecka"
        case "da": return "Uge"
        case "no", "nb", "nn": return "Uke"
        case "fi": return "Viikko"
        case "cs": return "Týden"
        case "ro": return "Săptămâna"
        case "hu": return "Hét"
        case "uk": return "Тиждень"
        case "bg": return "Седмица"
        case "he", "iw": return "שבוע"
        case "hi": return "सप्ताह"
        case "id", "ms": return "Minggu"
        case "th": return "สัปดาห์"
        case "vi": return "Tuần"
        default:   return "Week"
        }
    }

    /// Days remaining in the current calendar year (incl. today).
    static func daysRemainingInYear(date: Date = Date()) -> Int {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone.current
        let year = calendar.component(.year, from: date)
        guard let lastDay = calendar.date(from: DateComponents(year: year, month: 12, day: 31)) else { return 0 }
        let days = calendar.dateComponents([.day], from: calendar.startOfDay(for: date), to: lastDay).day ?? 0
        return max(0, days)
    }

    /// Year progress as a fraction [0, 1].
    static func yearProgress(date: Date = Date()) -> Double {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone.current
        let year = calendar.component(.year, from: date)
        guard let yearStart = calendar.date(from: DateComponents(year: year, month: 1, day: 1)),
              let yearEnd = calendar.date(from: DateComponents(year: year + 1, month: 1, day: 1)) else { return 0 }
        let total = yearEnd.timeIntervalSince(yearStart)
        let elapsed = date.timeIntervalSince(yearStart)
        return min(max(elapsed / total, 0), 1)
    }

    /// First/last day of the current week, respecting the chosen start.
    static func weekRange(weekStart: WeekStart, date: Date = Date()) -> (start: Date, end: Date)? {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone.current
        calendar.firstWeekday = (weekStart == .monday) ? 2 : 1
        calendar.minimumDaysInFirstWeek = (weekStart == .monday) ? 4 : 1
        guard let interval = calendar.dateInterval(of: .weekOfYear, for: date) else { return nil }
        let end = calendar.date(byAdding: .day, value: -1, to: interval.end) ?? interval.end
        return (interval.start, end)
    }
}
