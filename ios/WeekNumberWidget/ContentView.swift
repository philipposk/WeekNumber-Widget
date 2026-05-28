import SwiftUI

struct ContentView: View {
    @State private var weekStart: WeekStart = .monday
    @State private var showInstructions = false

    private var weekNumber: Int {
        WeekNumberCalculator.currentWeekNumber(weekStart: weekStart)
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    heroCard
                    description
                    weekStartPicker
                    instructionsCard
                    aboutLink
                }
                .padding(20)
            }
            .background(Color(.systemBackground))
            .navigationTitle("Week Number")
        }
    }

    private var aboutLink: some View {
        NavigationLink(destination: AboutView()) {
            HStack {
                Image(systemName: "info.circle")
                Text("About").font(.headline)
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
