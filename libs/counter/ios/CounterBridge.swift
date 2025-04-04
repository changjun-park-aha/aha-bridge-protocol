import Foundation
import WebKit

/**
 * Bridge class for counter functionality in iOS
 */
class CounterBridge: NSObject, WKScriptMessageHandler {
    
    // WebView instance
    private weak var webView: WKWebView?
    
    // List of subscribers to counter changes
    private var subscribers = [(Int) -> Void]()
    
    // Current counter value
    private var currentValue: Int = 0
    
    /**
     * Initialize with a WebView
     */
    init(webView: WKWebView) {
        self.webView = webView
        super.init()
        
        // Add script message handler
        webView.configuration.userContentController.add(self, name: "iOSCounterBridge")
        
        // Initialize bridge in WebView
        initialize()
    }
    
    /**
     * Subscribe to counter changes
     * @param subscriber Function to call when counter changes
     * @return Unsubscribe function
     */
    func subscribe(subscriber: @escaping (Int) -> Void) -> () -> Void {
        subscribers.append(subscriber)
        // Immediately notify with current value
        subscriber(currentValue)
        
        // Return unsubscribe function
        return { [weak self] in
            self?.subscribers.removeAll(where: { $0 === subscriber })
        }
    }
    
    /**
     * Notify all subscribers of counter changes
     * @param count New counter value
     */
    private func notifySubscribers(count: Int) {
        currentValue = count
        subscribers.forEach { $0(count) }
    }
    
    /**
     * Handle messages from JavaScript
     */
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        if message.name == "iOSCounterBridge", let body = message.body as? String {
            if let count = Int(body) {
                notifySubscribers(count)
            }
        }
    }
    
    /**
     * Increment counter
     */
    func increment() {
        sendEvent(type: "counter/increment")
    }
    
    /**
     * Decrement counter
     */
    func decrement() {
        sendEvent(type: "counter/decrement")
    }
    
    /**
     * Reset counter
     */
    func reset() {
        sendEvent(type: "counter/reset")
    }
    
    /**
     * Set counter value
     * @param value New counter value
     */
    func setValue(value: Int) {
        sendEvent(type: "counter/setValue", payload: value)
    }
    
    /**
     * Send event to JavaScript
     */
    private func sendEvent(type: String, payload: Any? = nil) {
        guard let webView = webView else { return }
        
        var eventDict: [String: Any] = ["type": type]
        if let payload = payload {
            eventDict["payload"] = payload
        }
        
        do {
            let eventData = try JSONSerialization.data(withJSONObject: eventDict)
            if let eventJson = String(data: eventData, encoding: .utf8) {
                let escapedJson = eventJson.replacingOccurrences(of: "\"", with: "\\\"")
                let script = "window.sendCounterEvent(\"\(escapedJson)\")"
                webView.evaluateJavaScript(script, completionHandler: nil)
            }
        } catch {
            print("Error creating event JSON: \(error)")
        }
    }
    
    /**
     * Initialize counter bridge in WebView
     */
    private func initialize() {
        guard let webView = webView else { return }
        
        let script = """
        if (window.counterBridgeInitialized) return;
        
        window.counterBridgeInitialized = true;
        
        // Subscribe to counter changes
        if (typeof window.subscribeToCounterBridge === 'function') {
            window.subscribeToCounterBridge(function(count) {
                // Call iOS bridge
                window.webkit.messageHandlers.iOSCounterBridge.postMessage(count.toString());
            });
        } else {
            console.error('subscribeToCounterBridge is not available');
        }
        """
        
        webView.evaluateJavaScript(script, completionHandler: nil)
    }
    
    /**
     * Cleanup when bridge is no longer needed
     */
    func cleanup() {
        webView?.configuration.userContentController.removeScriptMessageHandler(forName: "iOSCounterBridge")
    }
    
    deinit {
        cleanup()
    }
}