//
//  WeekNumberWidgetEntryView.swift
//  WeekNumberWidgetExtension
//
//  Created for iOS
//

import WidgetKit
import SwiftUI

struct WeekNumberWidgetEntryView: View {
    var entry: WeekNumberEntry
    @Environment(\.widgetFamily) var family
    
    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 0) {
                if entry.showLabel {
                    Text(entry.weekLabel)
                        .font(fontSizeForLabel(family: family, geometry: geometry))
                        .foregroundColor(entry.textColor)
                        .padding(.bottom, spacingForFamily(family))
                }
                
                Text("\(entry.weekNumber)")
                    .font(fontSizeForNumber(family: family, geometry: geometry))
                    .fontWeight(.bold)
                    .foregroundColor(entry.textColor)
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding()
        }
    }
    
    private func fontSizeForNumber(family: WidgetFamily, geometry: GeometryProxy) -> Font {
        let baseSize: CGFloat
        switch family {
        case .systemSmall:
            baseSize = min(geometry.size.width, geometry.size.height) * 0.4
        case .systemMedium:
            baseSize = min(geometry.size.width, geometry.size.height) * 0.35
        case .systemLarge:
            baseSize = min(geometry.size.width, geometry.size.height) * 0.3
        default:
            baseSize = 48
        }
        return .system(size: baseSize, weight: .bold)
    }
    
    private func fontSizeForLabel(family: WidgetFamily, geometry: GeometryProxy) -> Font {
        let baseSize: CGFloat
        switch family {
        case .systemSmall:
            baseSize = min(geometry.size.width, geometry.size.height) * 0.12
        case .systemMedium:
            baseSize = min(geometry.size.width, geometry.size.height) * 0.1
        case .systemLarge:
            baseSize = min(geometry.size.width, geometry.size.height) * 0.08
        default:
            baseSize = 14
        }
        return .system(size: baseSize, weight: .regular)
    }
    
    private func spacingForFamily(_ family: WidgetFamily) -> CGFloat {
        switch family {
        case .systemSmall:
            return 4
        case .systemMedium:
            return 6
        case .systemLarge:
            return 8
        default:
            return 4
        }
    }
}

// Note: SwiftUI Previews don't work in app extensions
// To preview widget views, use the Widget Gallery in Xcode or test on a device/simulator
/*
#Preview(as: .systemSmall) {
    WeekNumberWidget()
} timeline: {
    WeekNumberEntry(
        date: Date(),
        weekNumber: 42,
        weekLabel: "Week",
        backgroundColor: .clear,
        textColor: .primary,
        showLabel: true
    )
    WeekNumberEntry(
        date: Date(),
        weekNumber: 43,
        weekLabel: "Week",
        backgroundColor: .blue.opacity(0.2),
        textColor: .blue,
        showLabel: true
    )
}
*/

