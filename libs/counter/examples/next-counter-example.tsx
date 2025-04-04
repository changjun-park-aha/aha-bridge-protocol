import React, { useEffect, useState } from "react";
import { useCounterStore, subscribeToCounter } from "../src/store";

/**
 * Example Next.js page component that demonstrates the counter functionality
 */
export default function CounterExamplePage() {
  const { count, increment, decrement, reset, setValue } = useCounterStore();
  const [externalCount, setExternalCount] = useState(0);
  const [inputValue, setInputValue] = useState("");

  // Example of subscribing to counter changes
  useEffect(() => {
    const unsubscribe = subscribeToCounter((newCount) => {
      console.log("Counter subscription received:", newCount);
      setExternalCount(newCount);
    });

    // Clean up subscription when component unmounts
    return () => {
      unsubscribe();
    };
  }, []);

  const handleSetValue = () => {
    const value = parseInt(inputValue);
    if (!isNaN(value)) {
      setValue(value);
      setInputValue("");
    }
  };

  return (
    <div className="container">
      <h1>Counter Example</h1>

      <div className="card">
        <h2>Direct Store Access</h2>
        <p className="count">Count: {count}</p>
        <div className="button-group">
          <button onClick={decrement} className="button decrement">
            -
          </button>
          <button onClick={increment} className="button increment">
            +
          </button>
          <button onClick={reset} className="button reset">
            Reset
          </button>
        </div>
        <div className="input-group">
          <input
            type="number"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder="Enter a value"
          />
          <button onClick={handleSetValue} className="button set">
            Set Value
          </button>
        </div>
      </div>

      <div className="card">
        <h2>Subscription Example</h2>
        <p className="count">External Count: {externalCount}</p>
        <p className="description">
          This value is updated through a subscription to the counter store. It
          will stay in sync with the counter above.
        </p>
      </div>

      <div className="info">
        <h2>How It Works</h2>
        <p>
          This example demonstrates how to use the counter library in a Next.js
          application. The counter state is managed by Zustand and can be
          accessed from anywhere in your application.
        </p>
        <p>
          The counter also synchronizes with native platforms (Android and iOS)
          through the bridge when used in a WebView context.
        </p>
      </div>

      <style jsx>{`
        .container {
          max-width: 800px;
          margin: 0 auto;
          padding: 2rem;
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
            Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
        }

        h1 {
          color: #333;
          margin-bottom: 2rem;
        }

        .card {
          background-color: #fff;
          border-radius: 8px;
          box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
          padding: 1.5rem;
          margin-bottom: 1.5rem;
        }

        .count {
          font-size: 2rem;
          font-weight: bold;
          margin: 1rem 0;
        }

        .button-group {
          display: flex;
          gap: 0.5rem;
          margin-bottom: 1rem;
        }

        .button {
          padding: 0.5rem 1rem;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          font-weight: bold;
          min-width: 40px;
        }

        .increment {
          background-color: #4caf50;
          color: white;
        }

        .decrement {
          background-color: #f44336;
          color: white;
        }

        .reset {
          background-color: #2196f3;
          color: white;
        }

        .set {
          background-color: #9c27b0;
          color: white;
        }

        .input-group {
          display: flex;
          gap: 0.5rem;
          margin-top: 1rem;
        }

        input {
          padding: 0.5rem;
          border: 1px solid #ccc;
          border-radius: 4px;
          flex-grow: 1;
        }

        .description {
          color: #666;
          font-style: italic;
        }

        .info {
          background-color: #f5f5f5;
          border-radius: 8px;
          padding: 1.5rem;
        }
      `}</style>
    </div>
  );
}
