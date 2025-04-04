import { CounterEvent, CounterEventType, CounterSubscriber } from "./types";
import { getCounterValue, useCounterStore } from "./store";

/**
 * List of subscribers to counter changes
 */
const subscribers: CounterSubscriber[] = [];

/**
 * Subscribe to counter changes
 * @param subscriber Function to call when counter changes
 * @returns Unsubscribe function
 */
export const subscribeToCounterBridge = (subscriber: CounterSubscriber): (() => void) => {
  subscribers.push(subscriber);
  // Immediately notify with current value
  subscriber(getCounterValue());
  
  // Return unsubscribe function
  return () => {
    const index = subscribers.indexOf(subscriber);
    if (index !== -1) {
      subscribers.splice(index, 1);
    }
  };
};

/**
 * Notify all subscribers of counter changes
 * @param count New counter value
 */
const notifySubscribers = (count: number) => {
  subscribers.forEach(subscriber => subscriber(count));
};

// Subscribe to store changes to notify bridge subscribers
useCounterStore.subscribe(
  state => state.count,
  count => notifySubscribers(count)
);

/**
 * Handle counter events from native platforms
 * @param event Counter event
 */
export const handleCounterEvent = (event: CounterEvent) => {
  const { type, payload } = event;
  const store = useCounterStore.getState();
  
  switch (type) {
    case CounterEventType.INCREMENT:
      store.increment();
      break;
    case CounterEventType.DECREMENT:
      store.decrement();
      break;
    case CounterEventType.RESET:
      store.reset();
      break;
    case CounterEventType.SET_VALUE:
      if (typeof payload === "number") {
        store.setValue(payload);
      }
      break;
    default:
      console.warn(`Unknown counter event type: ${type}`);
  }
};

/**
 * Send counter event to JavaScript
 * This function is meant to be called from native code
 */
export const sendCounterEvent = (eventJson: string) => {
  try {
    const event = JSON.parse(eventJson) as CounterEvent;
    handleCounterEvent(event);
  } catch (error) {
    console.error("Failed to parse counter event:", error);
  }
};

// Make sendCounterEvent available globally for native code to call
if (typeof window !== "undefined") {
  (window as any).sendCounterEvent = sendCounterEvent;
}
