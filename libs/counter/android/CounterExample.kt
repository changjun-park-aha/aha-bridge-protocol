package com.aha.counter.example

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aha.counter.CounterBridge

/**
 * Example activity showing how to use the CounterBridge
 */
class CounterExampleActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private lateinit var counterBridge: CounterBridge
    private lateinit var counterValueText: TextView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var resetButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set up layout
        // Note: This is just an example, you would need to create the actual layout XML
        setContentView(R.layout.activity_counter_example)
        
        // Initialize WebView
        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        
        // Initialize counter bridge
        counterBridge = CounterBridge(webView)
        
        // Initialize UI elements
        counterValueText = findViewById(R.id.counter_value)
        incrementButton = findViewById(R.id.increment_button)
        decrementButton = findViewById(R.id.decrement_button)
        resetButton = findViewById(R.id.reset_button)
        
        // Set up button click listeners
        incrementButton.setOnClickListener {
            counterBridge.increment()
        }
        
        decrementButton.setOnClickListener {
            counterBridge.decrement()
        }
        
        resetButton.setOnClickListener {
            counterBridge.reset()
        }
        
        // Subscribe to counter changes
        counterBridge.subscribe { count ->
            // Update UI with new count
            runOnUiThread {
                counterValueText.text = count.toString()
            }
        }
        
        // Load the web page that contains the counter
        webView.loadUrl("http://localhost:3000")
        
        // Initialize the counter bridge after page load
        webView.webViewClient = object : android.webkit.WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                counterBridge.initialize()
            }
        }
    }
}

/**
 * Example of how to integrate CounterBridge in an existing activity
 */
fun integrateCounterBridgeExample(activity: AppCompatActivity, webView: WebView) {
    // Create counter bridge
    val counterBridge = CounterBridge(webView)
    
    // Initialize after page load
    webView.webViewClient = object : android.webkit.WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            counterBridge.initialize()
        }
    }
    
    // Subscribe to counter changes
    val unsubscribe = counterBridge.subscribe { count ->
        // Do something with the count
        println("Counter value changed: $count")
    }
    
    // Example of incrementing the counter
    counterBridge.increment()
    
    // Example of setting a specific value
    counterBridge.setValue(10)
    
    // Example of unsubscribing when no longer needed
    // unsubscribe()
}