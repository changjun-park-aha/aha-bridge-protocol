# Counter Library

A cross-platform counter library that works with Next.js, Android, and iOS.

## Features

- Zustand store for Next.js
- Native bridge for Android (Kotlin)
- Native bridge for iOS (Swift)
- WebView integration for mobile platforms
- Synchronized counter state across all platforms

## Installation

```bash
# Install dependencies
npm install
# or
yarn install
# or
pnpm install
```

## Usage

### Next.js

```tsx
import { useCounterStore } from "@aha/counter";

function CounterComponent() {
  const { count, increment, decrement, reset } = useCounterStore();

  return (
    <div>
      <h2>Counter: {count}</h2>
      <button onClick={decrement}>-</button>
      <button onClick={increment}>+</button>
      <button onClick={reset}>Reset</button>
    </div>
  );
}
```

You can also subscribe to counter changes:

```tsx
import { useEffect } from "react";
import { subscribeToCounter } from "@aha/counter";

function CounterObserver() {
  useEffect(() => {
    const unsubscribe = subscribeToCounter((count) => {
      console.log("Counter changed:", count);
    });

    return () => {
      unsubscribe();
    };
  }, []);

  return null;
}
```

### Android (Kotlin)

Add the CounterBridge to your WebView:

```kotlin
import com.aha.counter.CounterBridge

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var counterBridge: CounterBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize WebView
        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true

        // Initialize counter bridge
        counterBridge = CounterBridge(webView)

        // Subscribe to counter changes
        counterBridge.subscribe { count ->
            // Update UI with new count
            runOnUiThread {
                // Update your UI here
                println("Counter value: $count")
            }
        }

        // Load the web page
        webView.loadUrl("http://localhost:3000")

        // Initialize the counter bridge after page load
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                counterBridge.initialize()
            }
        }
    }

    // Example of how to interact with the counter
    fun incrementCounter() {
        counterBridge.increment()
    }

    fun decrementCounter() {
        counterBridge.decrement()
    }

    fun resetCounter() {
        counterBridge.reset()
    }

    fun setCounterValue(value: Int) {
        counterBridge.setValue(value)
    }
}
```

### iOS (Swift)

Using the CounterBridge with SwiftUI:

```swift
import SwiftUI
import WebKit

struct ContentView: View {
    @StateObject private var viewModel = CounterViewModel()

    var body: some View {
        VStack {
            Text("Counter: \(viewModel.count)")
                .font(.title)

            HStack {
                Button("-") {
                    viewModel.decrement()
                }

                Button("+") {
                    viewModel.increment()
                }

                Button("Reset") {
                    viewModel.reset()
                }
            }

            WebViewContainer(viewModel: viewModel)
                .frame(height: 300)
        }
        .padding()
    }
}

struct WebViewContainer: UIViewRepresentable {
    @ObservedObject var viewModel: CounterViewModel

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.navigationDelegate = context.coordinator

        // Initialize the counter bridge
        viewModel.initializeBridge(webView: webView)

        // Load the web page
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

class CounterViewModel: ObservableObject {
    @Published var count: Int = 0
    var counterBridge: CounterBridge?

    func initializeBridge(webView: WKWebView) {
        // Create counter bridge
        counterBridge = CounterBridge(webView: webView)

        // Subscribe to counter changes
        counterBridge?.subscribe { [weak self] newCount in
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
}
```

## Architecture

The counter library uses a Zustand store for state management in Next.js and provides native bridges for Android and iOS. The bridges communicate with the JavaScript code through the WebView, allowing for seamless integration between the web and native platforms.

### Components

- **Zustand Store**: Manages the counter state in Next.js
- **Bridge**: Handles communication between JavaScript and native platforms
- **Android Bridge**: Kotlin implementation for Android
- **iOS Bridge**: Swift implementation for iOS

## Building

```bash
# Build the library
cd libs/counter
npm run build
```

## License

MIT
