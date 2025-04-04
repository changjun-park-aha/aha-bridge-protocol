//
//  CounterViewModel.swift
//  aha-bridge-protocol
//
//  Created for aha-bridge-protocol
//

import SwiftUI
import WebKit

/**
 * ViewModel for the counter example
 */
class CounterViewModel: ObservableObject {
    @Published var count: Int = 0
    var counterBridge: CounterBridge?
    private var unsubscribe: (() -> Void)?
    
    func initializeBridge(webView: WKWebView) {
        // Create counter bridge
        counterBridge = CounterBridge(webView: webView)
        
        // Subscribe to counter changes
        unsubscribe = counterBridge?.subscribe { [weak self] newCount in
            DispatchQueue.main.async {
                self?.count = newCount
            }
        }
    }
    
    func increment() {
        counterBridge?.increment()
    }
    
    func decrement() {
        counterBridge?.decrement()
    }
    
    func reset() {
        counterBridge?.reset()
    }
    
    func setValue(_ value: Int) {
        counterBridge?.setValue(value: value)
    }
    
    deinit {
        // Clean up subscription when view model is deallocated
        unsubscribe?()
        counterBridge?.cleanup()
    }
}