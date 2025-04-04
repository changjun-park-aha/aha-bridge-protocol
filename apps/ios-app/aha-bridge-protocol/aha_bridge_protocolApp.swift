import SwiftUI
import WebKit

@main
struct aha_bridge_protocolApp: App {
    var body: some Scene {
        WindowGroup {
            WebView(url: URL(string: "http://localhost:3000")!)
        }
    }
}

struct WebView: UIViewRepresentable {
    let url: URL

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        let request = URLRequest(url: url)
        webView.load(request)
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {
        // No updates needed for now
    }
}
