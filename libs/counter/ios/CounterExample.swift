import SwiftUI
import WebKit

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

/**
 * Example of how to integrate CounterBridge in an existing UIViewController
 */
class CounterExampleViewController: UIViewController, WKNavigationDelegate {
    private var webView: WKWebView!
    private var counterBridge: CounterBridge?
    private var counterLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Set up WebView
        webView = WKWebView(frame: CGRect(x: 0, y: 0, width: view.bounds.width, height: 300))
        webView.navigationDelegate = self
        view.addSubview(webView)
        
        // Set up counter label
        counterLabel = UILabel(frame: CGRect(x: 0, y: 320, width: view.bounds.width, height: 50))
        counterLabel.textAlignment = .center
        counterLabel.font = UIFont.systemFont(ofSize: 24)
        counterLabel.text = "Counter: 0"
        view.addSubview(counterLabel)
        
        // Load the web page
        if let url = URL(string: "http://localhost:3000") {
            webView.load(URLRequest(url: url))
        }
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        // Initialize counter bridge
        counterBridge = CounterBridge(webView: webView)
        
        // Subscribe to counter changes
        counterBridge?.subscribe { [weak self] count in
            DispatchQueue.main.async {
                self?.counterLabel.text = "Counter: \(count)"
            }
        }
    }
    
    @objc func incrementCounter() {
        counterBridge?.increment()
    }
    
    @objc func decrementCounter() {
        counterBridge?.decrement()
    }
    
    @objc func resetCounter() {
        counterBridge?.reset()
    }
}