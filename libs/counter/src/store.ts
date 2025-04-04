import { create } from "zustand";
import { CounterState } from "./types";

/**
 * Create a Zustand store for the counter
 */
export const useCounterStore = create<CounterState>((set) => ({
  count: 0,
  increment: () => set((state) => ({ count: state.count + 1 })),
  decrement: () => set((state) => ({ count: state.count - 1 })),
  reset: () => set({ count: 0 }),
  setValue: (value: number) => set({ count: value }),
}));

/**
 * Subscribe to counter changes
 * @param callback Function to call when counter changes
 * @returns Unsubscribe function
 */
export const subscribeToCounter = (callback: (count: number) => void) => {
  return useCounterStore.subscribe(
    (state) => state.count,
    (count) => callback(count)
  );
};

/**
 * Get the current counter value
 * @returns Current counter value
 */
export const getCounterValue = () => {
  return useCounterStore.getState().count;
};
