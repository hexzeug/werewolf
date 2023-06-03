import { produce } from 'immer';
import { useSyncExternalStore } from 'react';

// Storage

/*
interaction: {
  question?: {
    text: String, (translate key)
    data?: Object, (translate interpolation data)
  },
  options?: [
    {
      text: String, (translate key)
      data?: Object, (translate interpolation data)
    }
  ],
  chat?: boolean,
}
*/
const storage = {
  interaction: {},
};

// Mutation

export const updateInteraction = (fn) => {
  setInteraction(
    produce(storage.interaction, (interaction) => {
      fn(interaction);
    })
  );
};

export const setInteraction = (interaction) => {
  storage.interaction = interaction;
};

// Subscription

const hooks = new Set();

const subscribe = (onStoreChange) => {
  hooks.add(onStoreChange);
  return () => {
    hooks.delete(onStoreChange);
  };
};

const getSnapshot = () => storage.interaction;

export const useInteraction = () => {
  return useSyncExternalStore(subscribe, getSnapshot);
};
