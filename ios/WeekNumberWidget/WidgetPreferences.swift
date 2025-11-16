//
//  WidgetPreferences.swift
//  WeekNumberWidget
//
//  Created for iOS
//

import Foundation
import SwiftUI
import UIKit

struct WidgetPreferences {
    private static let appGroupIdentifier = "group.philippos.Week-Number-Widget"
    private static let suiteName = appGroupIdentifier
    
    // Default values
    private static let defaultBackgroundColor = Color.clear
    private static let defaultTextColor = Color.primary
    private static let defaultWeekStart = "monday"
    private static let defaultShowLabel = true
    
    /// Get UserDefaults suite for sharing between app and widget
    private static var sharedDefaults: UserDefaults? {
        return UserDefaults(suiteName: suiteName)
    }
    
    // MARK: - Background Color
    
    static func getBackgroundColor(widgetId: String = "default") -> Color {
        guard let defaults = sharedDefaults else {
            return defaultBackgroundColor
        }
        
        // Check if color is stored as hex string
        if let hexString = defaults.string(forKey: "background_color_\(widgetId)") {
            return Color(hex: hexString) ?? defaultBackgroundColor
        }
        
        return defaultBackgroundColor
    }
    
    static func setBackgroundColor(widgetId: String = "default", color: Color) {
        // Convert color to hex string
        let hexString = color.toHex()
        
        // Try App Group first
        if let defaults = sharedDefaults {
            defaults.set(hexString, forKey: "background_color_\(widgetId)")
            defaults.synchronize()
        }
        
        // Also save to standard UserDefaults as fallback
        UserDefaults.standard.set(hexString, forKey: "background_color_\(widgetId)")
        UserDefaults.standard.synchronize()
    }
    
    // MARK: - Text Color
    
    static func getTextColor(widgetId: String = "default") -> Color {
        guard let defaults = sharedDefaults else {
            return defaultTextColor
        }
        
        // Check if color is stored as hex string
        if let hexString = defaults.string(forKey: "text_color_\(widgetId)") {
            return Color(hex: hexString) ?? defaultTextColor
        }
        
        // Return theme-aware default
        return defaultTextColor
    }
    
    static func setTextColor(widgetId: String = "default", color: Color) {
        // Convert color to hex string
        let hexString = color.toHex()
        
        // Try App Group first
        if let defaults = sharedDefaults {
            defaults.set(hexString, forKey: "text_color_\(widgetId)")
            defaults.synchronize()
        }
        
        // Also save to standard UserDefaults as fallback
        UserDefaults.standard.set(hexString, forKey: "text_color_\(widgetId)")
        UserDefaults.standard.synchronize()
    }
    
    // MARK: - Week Start
    
    static func getWeekStart(widgetId: String = "default") -> String {
        guard let defaults = sharedDefaults,
              let weekStart = defaults.string(forKey: "week_start_\(widgetId)") else {
            return defaultWeekStart
        }
        return weekStart
    }
    
    static func setWeekStart(widgetId: String = "default", weekStart: String) {
        guard let defaults = sharedDefaults else { return }
        defaults.set(weekStart, forKey: "week_start_\(widgetId)")
        defaults.synchronize()
    }
    
    // MARK: - Show Label
    
    static func getShowLabel(widgetId: String = "default") -> Bool {
        guard let defaults = sharedDefaults else {
            return defaultShowLabel
        }
        // Check if the key exists - if not, return default
        if defaults.object(forKey: "show_label_\(widgetId)") == nil {
            return defaultShowLabel
        }
        return defaults.bool(forKey: "show_label_\(widgetId)")
    }
    
    static func setShowLabel(widgetId: String = "default", show: Bool) {
        // Try App Group first
        if let defaults = sharedDefaults {
            defaults.set(show, forKey: "show_label_\(widgetId)")
            defaults.synchronize()
        }
        
        // Also save to standard UserDefaults as fallback
        UserDefaults.standard.set(show, forKey: "show_label_\(widgetId)")
        UserDefaults.standard.synchronize()
    }
    
    // MARK: - Week Label
    
    static func getWeekLabel() -> String {
        let locale = Locale.current
        let language = locale.languageCode ?? "en"
        
        return getWeekLabelForLanguage(language)
    }
    
    static func getWeekLabelForLanguage(_ language: String) -> String {
        switch language {
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
        default: return "Week"
        }
    }
}

// Extension to convert Color to hex string and back
extension Color {
    init?(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        
        // Handle transparent color
        if hex == "00000000" || hex == "000000" {
            self = .clear
            return
        }
        
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            return nil
        }
        
        // Check if transparent
        if a == 0 {
            self = .clear
            return
        }
        
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
    
    func toHex() -> String {
        // Handle semantic colors by resolving them in a light context
        let resolvedColor: UIColor
        if #available(iOS 13.0, *) {
            resolvedColor = UIColor(self)
        } else {
            resolvedColor = UIColor(self)
        }
        
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        // Try to get RGB values
        var success = false
        if resolvedColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha) {
            success = true
        } else {
            // For colors that don't support getRed (like pattern colors), try a different approach
            let cgColor = resolvedColor.cgColor
            if let components = cgColor.components, cgColor.numberOfComponents >= 3 {
                if cgColor.numberOfComponents == 4 {
                    red = components[0]
                    green = components[1]
                    blue = components[2]
                    alpha = components[3]
                } else {
                    red = components[0]
                    green = components[1]
                    blue = components[2]
                    alpha = 1.0
                }
                success = true
            }
        }
        
        if success {
            // Check if color is transparent
            if alpha < 0.01 {
                return "#00000000"
            }
            
            let rgb: Int = (Int)(red * 255) << 16 | (Int)(green * 255) << 8 | (Int)(blue * 255) << 0
            return String(format: "#%06x", rgb)
        }
        
        // Fallback for colors that can't be converted
        return "#000000"
    }
}

