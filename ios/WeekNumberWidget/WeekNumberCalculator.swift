//
//  WeekNumberCalculator.swift
//  WeekNumberWidget
//
//  Created for iOS
//

import Foundation

struct WeekNumberCalculator {
    /// Calculates the current week number
    /// - Parameters:
    ///   - weekStart: "monday" for ISO 8601 (Monday start, week 1 = first week with 4+ days)
    ///                 "sunday" for US/Canada style (Sunday start, week 1 contains Jan 1)
    /// - Returns: The current week number
    static func getCurrentWeekNumber(weekStart: String = "monday", for date: Date = Date()) -> Int {
        let calendar = Calendar.current
        var components = calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: date)
        
        if weekStart == "sunday" {
            // US/Canada style: Sunday start, week 1 contains January 1
            var customCalendar = Calendar.current
            customCalendar.firstWeekday = 1 // Sunday
            customCalendar.minimumDaysInFirstWeek = 1
            
            // Use the custom calendar to get week number
            let components = customCalendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: date)
            return components.weekOfYear ?? 1
        } else {
            // ISO 8601: Monday start, week 1 is first week with at least 4 days
            var isoCalendar = Calendar.current
            isoCalendar.firstWeekday = 2 // Monday
            isoCalendar.minimumDaysInFirstWeek = 4
            
            components = isoCalendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: date)
            return components.weekOfYear ?? 1
        }
    }
    
    /// Gets the current year
    static func getCurrentYear() -> Int {
        return Calendar.current.component(.year, from: Date())
    }
    
    /// Formats the week number as a string
    static func getFormattedWeekNumber(weekStart: String = "monday") -> String {
        return "Week \(getCurrentWeekNumber(weekStart: weekStart))"
    }
}

