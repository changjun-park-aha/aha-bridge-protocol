package com.aha.ahaBridgeProtocol

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import com.aha.ahaBridgeProtocol.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView
    private lateinit var counterBridge: CounterBridge
    private lateinit var counterValueText: TextView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Get the main content layout
        val contentLayout = findViewById<LinearLayout>(R.id.content_layout)
        contentLayout.orientation = LinearLayout.VERTICAL
        contentLayout.removeAllViews()

        // Create title
        val titleText = TextView(this)
        titleText.text = "Counter Example"
        titleText.textSize = 24f
        titleText.gravity = Gravity.CENTER
        titleText.setPadding(0, 40, 0, 40)
        contentLayout.addView(titleText)

        // Create WebView container with blue border
        val webViewContainer = LinearLayout(this)
        webViewContainer.orientation = LinearLayout.VERTICAL
        
        // Set blue border programmatically in case the drawable resource is not available
        try {
            webViewContainer.setBackgroundResource(R.drawable.blue_border)
        } catch (e: Exception) {
            // Fallback to programmatic border
            webViewContainer.setBackgroundColor(Color.WHITE)
            webViewContainer.setPadding(4, 4, 4, 4)
            val gd = GradientDrawable()
            gd.setStroke(4, Color.BLUE)
            webViewContainer.background = gd
        }
        
        val webViewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        )
        webViewParams.setMargins(16)
        webViewContainer.layoutParams = webViewParams

        // Add WebView title
        val webViewTitle = TextView(this)
        webViewTitle.text = "WebView Counter"
        webViewTitle.textSize = 18f
        webViewTitle.gravity = Gravity.CENTER
        webViewTitle.setPadding(0, 20, 0, 20)
        webViewContainer.addView(webViewTitle)

        // Initialize WebView
        webView = WebView(this)
        val webViewLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        )
        webViewLayoutParams.setMargins(16)
        webView.layoutParams = webViewLayoutParams
        webViewContainer.addView(webView)
        contentLayout.addView(webViewContainer)

        // Create native counter container with red border
        val nativeContainer = LinearLayout(this)
        nativeContainer.orientation = LinearLayout.VERTICAL
        
        // Set red border programmatically in case the drawable resource is not available
        try {
            nativeContainer.setBackgroundResource(R.drawable.red_border)
        } catch (e: Exception) {
            // Fallback to programmatic border
            nativeContainer.setBackgroundColor(Color.WHITE)
            nativeContainer.setPadding(4, 4, 4, 4)
            val gd = GradientDrawable()
            gd.setStroke(4, Color.RED)
            nativeContainer.background = gd
        }
        
        val nativeParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        )
        nativeParams.setMargins(16)
        nativeContainer.layoutParams = nativeParams

        // Add native counter title
        val nativeTitle = TextView(this)
        nativeTitle.text = "Native Counter"
        nativeTitle.textSize = 18f
        nativeTitle.gravity = Gravity.CENTER
        nativeTitle.setPadding(0, 20, 0, 20)
        nativeContainer.addView(nativeTitle)

        // Add counter value text
        counterValueText = TextView(this)
        counterValueText.text = "Counter: 0"
        counterValueText.textSize = 22f
        counterValueText.gravity = Gravity.CENTER
        counterValueText.setPadding(0, 20, 0, 20)
        nativeContainer.addView(counterValueText)

        // Add buttons container
        val buttonsContainer = LinearLayout(this)
        buttonsContainer.orientation = LinearLayout.HORIZONTAL
        buttonsContainer.gravity = Gravity.CENTER
        val buttonsParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        buttonsParams.setMargins(16)
        buttonsContainer.layoutParams = buttonsParams

        // Add decrement button
        decrementButton = Button(this)
        decrementButton.text = "-"
        decrementButton.setBackgroundColor(Color.RED)
        decrementButton.setTextColor(Color.WHITE)
        val buttonParams = LinearLayout.LayoutParams(
            150,
            150
        )
        buttonParams.setMargins(16)
        decrementButton.layoutParams = buttonParams
        buttonsContainer.addView(decrementButton)

        // Add increment button
        incrementButton = Button(this)
        incrementButton.text = "+"
        incrementButton.setBackgroundColor(Color.GREEN)
        incrementButton.setTextColor(Color.WHITE)
        incrementButton.layoutParams = buttonParams
        buttonsContainer.addView(incrementButton)

        nativeContainer.addView(buttonsContainer)

        // Add reset button
        resetButton = Button(this)
        resetButton.text = "Reset"
        resetButton.setBackgroundColor(Color.BLUE)
        resetButton.setTextColor(Color.WHITE)
        val resetParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        resetParams.gravity = Gravity.CENTER
        resetParams.setMargins(0, 16, 0, 32)
        resetButton.layoutParams = resetParams
        nativeContainer.addView(resetButton)

        contentLayout.addView(nativeContainer)

        // Setup WebView
        setupWebView()

        // Initialize counter bridge
        counterBridge = CounterBridge(webView)

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
                counterValueText.text = "Counter: $count"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Since we're using WebView and not navigation components,
        // we can simply return to the previous page in the WebView if possible
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onSupportNavigateUp()
    }
    
    private fun setupWebView() {
        // Enable JavaScript
        webView.settings.javaScriptEnabled = true
        
        // Set WebViewClient to handle page navigation within the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                counterBridge.initialize()
            }
        }
        
        // Load a URL
        webView.loadUrl("https://google.com")
    }
}

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
    @android.webkit.JavascriptInterface
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