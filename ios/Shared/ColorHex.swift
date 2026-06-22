import SwiftUI
#if canImport(UIKit)
import UIKit
#endif

/// Foundation-testable RGBA model + hex parsing/formatting.
struct RGBA: Equatable {
    var r: Double, g: Double, b: Double, a: Double

    /// Accepts "#RGB", "#RRGGBB", "#AARRGGBB" (with or without leading '#').
    init?(hex: String) {
        var s = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        if s.hasPrefix("#") { s.removeFirst() }
        guard let value = UInt64(s, radix: 16) else { return nil }
        switch s.count {
        case 3:
            r = Double((value >> 8) & 0xF) / 15.0
            g = Double((value >> 4) & 0xF) / 15.0
            b = Double(value & 0xF) / 15.0
            a = 1
        case 6:
            r = Double((value >> 16) & 0xFF) / 255.0
            g = Double((value >> 8) & 0xFF) / 255.0
            b = Double(value & 0xFF) / 255.0
            a = 1
        case 8:
            a = Double((value >> 24) & 0xFF) / 255.0
            r = Double((value >> 16) & 0xFF) / 255.0
            g = Double((value >> 8) & 0xFF) / 255.0
            b = Double(value & 0xFF) / 255.0
        default:
            return nil
        }
    }

    init(r: Double, g: Double, b: Double, a: Double = 1) {
        self.r = r; self.g = g; self.b = b; self.a = a
    }

    /// "#AARRGGBB".
    var hexString: String {
        func c(_ v: Double) -> Int { Int((v * 255).rounded()).clamped(0, 255) }
        return String(format: "#%02X%02X%02X%02X", c(a), c(r), c(g), c(b))
    }
}

private extension Int {
    func clamped(_ lo: Int, _ hi: Int) -> Int { Swift.min(Swift.max(self, lo), hi) }
}

extension Color {
    init(rgba: RGBA) {
        self.init(.sRGB, red: rgba.r, green: rgba.g, blue: rgba.b, opacity: rgba.a)
    }

    init?(hex: String) {
        guard let rgba = RGBA(hex: hex) else { return nil }
        self.init(rgba: rgba)
    }

    /// Resolve to "#AARRGGBB". Returns nil for dynamic/unsupported colors.
    func toHex() -> String? {
        #if canImport(UIKit)
        var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
        guard UIColor(self).getRed(&r, green: &g, blue: &b, alpha: &a) else { return nil }
        return RGBA(r: Double(r), g: Double(g), b: Double(b), a: Double(a)).hexString
        #else
        return nil
        #endif
    }
}
