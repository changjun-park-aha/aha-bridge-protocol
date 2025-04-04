import React, { useEffect } from "react";
import { useCounterStore } from "./store";

interface CounterComponentProps {
  /**
   * Optional custom styling for the counter container
   */
  className?: string;
}

/**
 * Counter component that uses the Zustand store
 */
export const CounterComponent: React.FC<CounterComponentProps> = ({
  className,
}) => {
  const { count, increment, decrement, reset } = useCounterStore();

  // Log counter changes for demonstration purposes
  useEffect(() => {
    console.log("Counter value changed:", count);
  }, [count]);

  return (
    <div className={`counter-container ${className || ""}`}>
      <h2>Counter: {count}</h2>
      <div className="counter-buttons">
        <button onClick={decrement} className="counter-button decrement">
          -
        </button>
        <button onClick={increment} className="counter-button increment">
          +
        </button>
        <button onClick={reset} className="counter-button reset">
          Reset
        </button>
      </div>

      <style jsx>{`
        .counter-container {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 1rem;
          border: 1px solid #ccc;
          border-radius: 8px;
          margin: 1rem 0;
          max-width: 300px;
        }

        .counter-buttons {
          display: flex;
          gap: 0.5rem;
          margin-top: 1rem;
        }

        .counter-button {
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
      `}</style>
    </div>
  );
};

/**
 * Example of how to use the counter component in a Next.js page
 */
export const CounterExample: React.FC = () => {
  return (
    <div className="counter-example">
      <h1>Counter Example</h1>
      <p>
        This is an example of using the counter component with Zustand store.
      </p>

      <CounterComponent />

      <div className="info">
        <p>
          The counter state is managed by Zustand and can be accessed from
          anywhere in your application. It also synchronizes with native
          platforms through the bridge.
        </p>
      </div>

      <style jsx>{`
        .counter-example {
          padding: 2rem;
          max-width: 800px;
          margin: 0 auto;
        }

        .info {
          margin-top: 2rem;
          padding: 1rem;
          background-color: #f5f5f5;
          border-radius: 8px;
        }
      `}</style>
    </div>
  );
};

export default CounterComponent;
