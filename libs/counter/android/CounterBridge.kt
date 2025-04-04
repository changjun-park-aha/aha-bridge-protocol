package com.aha.counter

import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONObject

/**
 * Bridge class for counter functionality in Android
 */
class CounterBridge(private val webView: WebView) {
    
    // List of subscribers to counter changes
    private val subscribers = mutableListOf<(Int) -> Unit>()
    
    // Current counter value
    private var currentValue: Int = 0
    
    init {
        // Add JavaScript interface to WebView
        webView.addJavascriptInterface(this, "AndroidCounterBridge")
    }
    
    /**
     * Subscribe to counter changes
     * @param subscriber Function to call when counter changes
     * @return Unsubscribe function
     */
    fun subscribe(subscriber: (Int) -> Unit): () -> Unit {
        subscribers.add(subscriber)
        // Immediately notify with current value
        subscriber(currentValue)
        
        // Return unsubscribe function
        return {
            subscribers.remove(subscriber)
        }
    }
    
    /**
     * Notify all subscribers of counter changes
     * @param count New counter value
     */
    private fun notifySubscribers(count: Int) {
        currentValue = count
        subscribers.forEach { it(count) }
    }
    
    /**
     * Called from JavaScript when counter value changes
     */
    @JavascriptInterface
    fun onCounterChanged(countStr: String) {
        try {
            val count = countStr.toInt()
            notifySubscribers(count)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Increment counter
     */
    fun increment() {
        val event = JSONObject().apply {
            put("type", "counter/increment")
        }
        sendEvent(event.toString())
    }
    
    /**
     * Decrement counter
     */
    fun decrement() {
        val event = JSONObject().apply {
            put("type", "counter/decrement")
        }
        sendEvent(event.toString())
    }
    
    /**
     * Reset counter
     */
    fun reset() {
        val event = JSONObject().apply {
            put("type", "counter/reset")
        }
        sendEvent(event.toString())
    }
    
    /**
     * Set counter value
     * @param value New counter value
     */
    fun setValue(value: Int) {
        val event = JSONObject().apply {
            put("type", "counter/setValue")
            put("payload", value)
        }
        sendEvent(event.toString())
    }
    
    /**
     * Send event to JavaScript
     * @param eventJson Event JSON string
     */
    private fun sendEvent(eventJson: String) {
        val escapedJson = eventJson.replace("\"", "\\\"")
        webView.post {
            webView.evaluateJavascript(
                "window.sendCounterEvent(\"$escapedJson\")",
                null
            )
        }
    }
    
    /**
     * Initialize counter bridge in WebView
     */
    fun initialize() {
        // Add listener for counter changes
        webView.post {
            webView.evaluateJavascript(
                """
                if (window.counterBridgeInitialized) return;
                
                window.counterBridgeInitialized = true;
                
                // Subscribe to counter changes
                if (typeof window.subscribeToCounterBridge === 'function') {
                    window.subscribeToCounterBridge(function(count) {
                        // Call Android bridge
                        if (window.AndroidCounterBridge) {
                            window.AndroidCounterBridge.onCounterChanged(count.toString());
                        }
                    });
                } else {
                    console.error('subscribeToCounterBridge is not available');
                }
                """.trimIndent(),
                null
            )
        }
    }
}