import SwiftUI
import WidgetKit
import os

/// Full colour pickers in the app. The chosen colours are shared with the
/// widget through an App Group and used by any widget set to "Match app".
struct WidgetColorSettingsView: View {
    @State private var textColor: Color = .primary
    @State private var backgroundColor: Color = .clear
    @State private var showDynamicWarning = false

    private static let log = Logger(subsystem: "com.weeknumber.widget", category: "ColorSettings")

    var body: some View {
        Form {
            Section {
                ColorPicker("Text colour", selection: $textColor, supportsOpacity: false)
                    .onChange(of: textColor) { _, newValue in
                        persist(newValue) { SharedSettings.textColorHex = $0 }
                    }
                ColorPicker("Background", selection: $backgroundColor, supportsOpacity: true)
                    .onChange(of: backgroundColor) { _, newValue in
                        persist(newValue) { SharedSettings.backgroundColorHex = $0 }
                    }
            } header: {
                Text("Widget colours")
            } footer: {
                Text("Applies to any widget whose colour is set to “Match app”. Long-press a widget → Edit Widget to switch it to Match app.")
            }

            Section {
                Button("Reset to defaults", role: .destructive) {
                    SharedSettings.textColorHex = nil
                    SharedSettings.backgroundColorHex = nil
                    textColor = .primary
                    backgroundColor = .clear
                    reload()
                }
            }
        }
        .navigationTitle("Widget colours")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear(perform: load)
        .alert("Pick a solid colour", isPresented: $showDynamicWarning) {
            Button("OK", role: .cancel) { }
        } message: {
            Text("Dynamic colours (like the system default) can’t be shared with widgets. Choose a specific colour from the picker.")
        }
    }

    private func load() {
        textColor = loadColor(SharedSettings.textColorHex, default: .primary) { SharedSettings.textColorHex = $0 }
        backgroundColor = loadColor(SharedSettings.backgroundColorHex, default: .clear) { SharedSettings.backgroundColorHex = $0 }
    }

    /// Parse a stored hex; if it's present but corrupt, drop it so it can't linger.
    private func loadColor(_ hex: String?, default fallback: Color, clear: (String?) -> Void) -> Color {
        guard let hex else { return fallback }
        if let c = Color(hex: hex) { return c }
        Self.log.error("Discarding invalid stored colour hex: \(hex, privacy: .public)")
        clear(nil)
        return fallback
    }

    /// Only persist real RGBA colours. Dynamic/system colours (e.g. .primary)
    /// have no hex representation — ignore them rather than silently wiping the
    /// stored value, and tell the user why.
    private func persist(_ color: Color, into setter: (String?) -> Void) {
        if let hex = color.toHex() {
            setter(hex)
            reload()
        } else {
            showDynamicWarning = true
        }
    }

    private func reload() {
        WidgetCenter.shared.reloadAllTimelines()
    }
}

#Preview {
    NavigationStack { WidgetColorSettingsView() }
}
