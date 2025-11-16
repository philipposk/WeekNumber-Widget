//
//  AboutView.swift
//  WeekNumberWidget
//
//  Created for iOS
//

import SwiftUI

struct AboutView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text("About")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .padding(.bottom, 10)
                
                Text("A simple widget to display the current week number on your iPhone's home screen.")
                    .font(.body)
                    .foregroundColor(.secondary)
                
                Divider()
                
                VStack(alignment: .leading, spacing: 10) {
                    Text("Features")
                        .font(.headline)
                    
                    Text("• Home Screen Widget")
                    Text("• ISO 8601 Standard (Monday start)")
                    Text("• US/Canada Style (Sunday start)")
                    Text("• Dark/Light Mode Support")
                    Text("• Customizable Colors")
                }
                .font(.subheadline)
                
                Divider()
                
                VStack(alignment: .leading, spacing: 10) {
                    Text("Technical Details")
                        .font(.headline)
                    
                    Text("• iOS 17.0 or later")
                    Text("• Widget updates daily")
                    Text("• Supports multiple widget sizes")
                }
                .font(.subheadline)
                
                Spacer()
            }
            .padding()
        }
        .navigationTitle("About")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    NavigationView {
        AboutView()
    }
}

