import SwiftUI

struct AboutView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text("A simple widget that shows the current week number on your Home Screen and Lock Screen.")
                    .font(.body)
                    .foregroundStyle(.secondary)

                Divider()

                section("Features", [
                    "Home Screen widget (small & medium)",
                    "Lock Screen widget (circular, rectangular, inline)",
                    "ISO 8601 standard (Monday start)",
                    "US/Canada style (Sunday start)",
                    "Dark / Light mode support",
                    "Per-widget colors via Edit Widget",
                ])

                Divider()

                section("Technical", [
                    "iOS 17.0 or later",
                    "Updates automatically at midnight",
                    "No accounts, no data collected, runs on device",
                ])
            }
            .padding()
        }
        .navigationTitle("About")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func section(_ title: String, _ items: [String]) -> some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(title).font(.headline)
            ForEach(items, id: \.self) { Text("• \($0)") }
        }
        .font(.subheadline)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

#Preview {
    NavigationStack { AboutView() }
}
