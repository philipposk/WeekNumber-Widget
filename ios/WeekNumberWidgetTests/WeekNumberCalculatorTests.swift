import XCTest
@testable import WeekNumberWidget

final class WeekNumberCalculatorTests: XCTestCase {

    private func date(_ y: Int, _ m: Int, _ d: Int) -> Date {
        var cal = Calendar(identifier: .gregorian)
        cal.timeZone = TimeZone(identifier: "UTC")!
        return cal.date(from: DateComponents(year: y, month: m, day: d, hour: 12))!
    }

    // MARK: ISO 8601 (Monday start)

    func testISOFirstWeekOf2024() {
        // 2024-01-01 is a Monday → ISO week 1.
        XCTAssertEqual(WeekNumberCalculator.currentWeekNumber(weekStart: .monday, date: date(2024, 1, 1)), 1)
    }

    func testISOWeek1SpansYearBoundary() {
        // 2021-01-01 is a Friday; ISO assigns it to week 53 of 2020.
        XCTAssertEqual(WeekNumberCalculator.currentWeekNumber(weekStart: .monday, date: date(2021, 1, 1)), 53)
    }

    func testISOMidYear() {
        // 2026-06-15 (a Monday) is ISO week 25.
        XCTAssertEqual(WeekNumberCalculator.currentWeekNumber(weekStart: .monday, date: date(2026, 6, 15)), 25)
    }

    // MARK: US (Sunday start)

    func testUSFirstWeekContainsJan1() {
        XCTAssertEqual(WeekNumberCalculator.currentWeekNumber(weekStart: .sunday, date: date(2024, 1, 1)), 1)
    }

    func testUSJan1IsAlwaysWeek1() {
        // US style: week 1 always contains Jan 1, regardless of weekday.
        XCTAssertEqual(WeekNumberCalculator.currentWeekNumber(weekStart: .sunday, date: date(2021, 1, 1)), 1)
    }

    // MARK: Localized label

    func testLabelKnownLanguages() {
        XCTAssertEqual(WeekNumberCalculator.weekLabel(languageCode: "en"), "Week")
        XCTAssertEqual(WeekNumberCalculator.weekLabel(languageCode: "el"), "Εβδομάδα")
        XCTAssertEqual(WeekNumberCalculator.weekLabel(languageCode: "de"), "Woche")
        XCTAssertEqual(WeekNumberCalculator.weekLabel(languageCode: "ja"), "週")
    }

    func testLabelFallsBackToEnglish() {
        XCTAssertEqual(WeekNumberCalculator.weekLabel(languageCode: "xx"), "Week")
    }

    // MARK: Year info

    func testDaysRemainingInYearAtNewYearsEve() {
        XCTAssertEqual(WeekNumberCalculator.daysRemainingInYear(date: date(2026, 12, 31)), 0)
    }

    func testYearProgressBounds() {
        let p = WeekNumberCalculator.yearProgress(date: date(2026, 7, 1))
        XCTAssertGreaterThan(p, 0.0)
        XCTAssertLessThan(p, 1.0)
    }

    func testWeekRangeIsSevenDays() {
        let range = WeekNumberCalculator.weekRange(weekStart: .monday, date: date(2026, 6, 17))
        XCTAssertNotNil(range)
        let days = Calendar(identifier: .gregorian).dateComponents([.day], from: range!.start, to: range!.end).day
        XCTAssertEqual(days, 6)
    }
}
