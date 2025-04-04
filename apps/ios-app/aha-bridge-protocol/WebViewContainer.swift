//
//  WebViewContainer.swift
//  aha-bridge-protocol
//
//  Created for aha-bridge-protocol
//

import SwiftUI
import WebKit

/**
 * WebView container that integrates with CounterBridge
 */
struct WebViewContainer: UIViewRepresentable {
    @ObservedObject var viewModel: CounterViewModel
    
    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.navigationDelegate = context.coordinator
        
        // Initialize the counter bridge
        viewModel.initializeBridge(webView: webView)
        
        // Load the web page that contains the counter
        if let url = URL(string: "http://localhost:3000") {
            webView.load(URLRequest(url: url))
        }
        
        return webView
    }
    
    func updateUIView(_ uiView: WKWebView, context: Context) {
        // No updates needed
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, WKNavigationDelegate {
        var parent: WebViewContainer
        
        init(_ parent: WebViewContainer) {
            self.parent = parent
        }
        
        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            // Initialize bridge after page load
            parent.viewModel.counterBridge?.initialize()
        }
    }
}