import SwiftUI

struct ContentView: View {
    @State private var weekStart: WeekStart = .monday
    @State private var showInstructions = false

    private var weekNumber: Int {
        WeekNumberCalculator.currentWeekNumber(weekStart: weekStart)
    }

    private var shareText: String {
        // Language-neutral separator so the connector isn't English-only.
        "\(WeekNumberCalculator.weekLabel()) \(weekNumber) · \(WeekNumberCalculator.currentYear())"
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    heroCard
                    thisWeekCard
                    description
                    weekStartPicker
                    colorsLink
                    instructionsCard
                    aboutLink
                }
                .padding(20)
            }
            .background(Color(.systemBackground))
            .navigationTitle("Week Number")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    ShareLink(item: shareText) {
                        Image(systemName: "square.and.arrow.up")
                    }
                }
            }
        }
    }

    private var thisWeekCard: some View {
        let range = WeekNumberCalculator.weekRange(weekStart: weekStart)
        let fmt: DateFormatter = {
            let f = DateFormatter()
            // Locale-adaptive month/day order rather than a fixed "MMM d".
            f.setLocalizedDateFormatFromTemplate("MMMd")
            return f
        }()
        let rangeText: String = {
            guard let range else { return "—" }
            return "\(fmt.string(from: range.start)) – \(fmt.string(from: range.end))"
        }()
        let daysLeft = WeekNumberCalculator.daysRemainingInYear()
        let progress = WeekNumberCalculator.yearProgress()

        return VStack(alignment: .leading, spacing: 12) {
            Text("THIS WEEK")
                .font(.caption.weight(.semibold))
                .tracking(1)
                .foregroundStyle(.secondary)
            infoRow(icon: "calendar", label: "Dates", value: rangeText)
            infoRow(icon: "clock", label: "Days left in year", value: "\(daysLeft)")
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Image(systemName: "chart.bar.fill").frame(width: 22)
                    Text("Year progress").foregroundStyle(.secondary)
                    Spacer()
                    Text("\(Int(progress * 100))%").fontWeight(.semibold)
                }
                .font(.subheadline)
                ProgressView(value: progress)
                    .tint(.accentColor)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(20)
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
    }

    private func infoRow(icon: String, label: String, value: String) -> some View {
        HStack {
            Image(systemName: icon).frame(width: 22)
            Text(label).foregroundStyle(.secondary)
            Spacer()
            Text(value).fontWeight(.semibold)
        }
        .font(.subheadline)
    }

    private func rowLink<Destination: View>(icon: String, title: String, destination: Destination) -> some View {
        NavigationLink(destination: destination) {
            HStack {
                Image(systemName: icon)
                Text(title).font(.headline)
                Spacer()
                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            .padding(20)
            .frame(maxWidth: .infinity)
            .background(Color(.secondarySystemBackground))
            .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
        }
        .buttonStyle(.plain)
    }

    private var colorsLink: some View {
        rowLink(icon: "paintpalette", title: "Widget colours", destination: WidgetColorSettingsView())
    }

    private var aboutLink: some View {
        rowLink(icon: "info.circle", title: "About", destination: AboutView())
    }

    private var heroCard: some View {
        VStack(spacing: 4) {
            Text(WeekNumberCalculator.weekLabel().uppercased())
                .font(.subheadline.weight(.semibold))
                .tracking(2)
                .foregroundStyle(.secondary)
            Text("\(weekNumber)")
                .font(.system(size: 120, weight: .bold, design: .rounded))
                .foregroundStyle(.primary)
                .contentTransition(.numericText())
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 40)
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("\(WeekNumberCalculator.weekLabel()) \(weekNumber)")
    }

    private var description: some View {
        Text("Place the current week number on your Home Screen or Lock Screen. Long-press a widget to change its colors and settings.")
            .font(.callout)
            .multilineTextAlignment(.center)
            .foregroundStyle(.secondary)
    }

    private var weekStartPicker: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("WEEK STARTS ON")
                .font(.caption.weight(.semibold))
                .tracking(1)
                .foregroundStyle(.secondary)
            Picker("Week starts on", selection: $weekStart) {
                Text("Monday").tag(WeekStart.monday)
                Text("Sunday").tag(WeekStart.sunday)
            }
            .pickerStyle(.segmented)
            Text("This preview uses your choice. Each widget keeps its own setting, changed by long-pressing the widget.")
                .font(.caption2)
                .foregroundStyle(.tertiary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(20)
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
    }

    private var instructionsCard: some View {
        VStack(alignment: .leading, spacing: 0) {
            Button {
                withAnimation { showInstructions.toggle() }
            } label: {
                HStack {
                    Text("How to add the widget")
                        .font(.headline)
                        .foregroundStyle(.primary)
                    Spacer()
                    Image(systemName: showInstructions ? "chevron.up" : "chevron.down")
                        .foregroundStyle(.secondary)
                }
                .padding(20)
            }

            if showInstructions {
                Text("""
                Home Screen
                1. Long-press an empty area of the Home Screen.
                2. Tap the + button (top-left).
                3. Search for “Week Number”.
                4. Pick a size and tap Add Widget.

                Lock Screen
                1. Long-press the Lock Screen, tap Customize.
                2. Tap the area under the clock.
                3. Tap a slot, then choose “Week Number”.

                Long-press any placed widget → Edit Widget to change colors, the week start day, or the label.
                """)
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .padding(.horizontal, 20)
                .padding(.bottom, 20)
            }
        }
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
    }
}

#Preview {
    ContentView()
}
