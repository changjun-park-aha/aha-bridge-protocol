/**
 * Counter state interface
 */
export interface CounterState {
  count: number;
  increment: () => void;
  decrement: () => void;
  reset: () => void;
  setValue: (value: number) => void;
}

/**
 * Counter event types for bridge communication
 */
export enum CounterEventType {
  INCREMENT = "counter/increment",
  DECREMENT = "counter/decrement",
  RESET = "counter/reset",
  SET_VALUE = "counter/setValue",
  VALUE_CHANGED = "counter/valueChanged",
}

/**
 * Counter event interface for bridge communication
 */
export interface CounterEvent {
  type: CounterEventType;
  payload?: any;
}

/**
 * Counter subscription callback
 */
export type CounterSubscriber = (count: number) => void;
