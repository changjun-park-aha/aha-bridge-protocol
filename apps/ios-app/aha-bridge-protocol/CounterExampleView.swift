//
//  CounterExampleView.swift
//  aha-bridge-protocol
//
//  Created for aha-bridge-protocol
//

import SwiftUI

/**
 * Example SwiftUI view showing how to use the CounterBridge
 */
struct CounterExampleView: View {
    @StateObject private var viewModel = CounterViewModel()
    
    var body: some View {
        VStack(spacing: 0) {
            // Title
            Text("Counter Example")
                .font(.largeTitle)
                .padding()
            
            // Top part: WebView with blue border
            VStack {
                Text("WebView Counter")
                    .font(.headline)
                    .padding(.top)
                
                WebViewContainer(viewModel: viewModel)
                    .padding(10)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(
                RoundedRectangle(cornerRadius: 0)
                    .stroke(Color.blue, lineWidth: 4)
            )
            
            // Bottom part: Native component with red border
            VStack(spacing: 20) {
                Text("Native Counter")
                    .font(.headline)
                    .padding(.top)
                
                Text("Counter: \(viewModel.count)")
                    .font(.title)
                    .padding()
                
                HStack(spacing: 20) {
                    Button(action: {
                        viewModel.decrement()
                    }) {
                        Text("-")
                            .font(.title)
                            .frame(width: 50, height: 50)
                            .background(Color.red)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                    }
                    
                    Button(action: {
                        viewModel.increment()
                    }) {
                        Text("+")
                            .font(.title)
                            .frame(width: 50, height: 50)
                            .background(Color.green)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                    }
                }
                
                Button(action: {
                    viewModel.reset()
                }) {
                    Text("Reset")
                        .font(.headline)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .padding(.bottom)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(
                RoundedRectangle(cornerRadius: 0)
                    .stroke(Color.red, lineWidth: 4)
            )
        }
        .edgesIgnoringSafeArea(.bottom)
    }
}

#Preview {
    CounterExampleView()
}