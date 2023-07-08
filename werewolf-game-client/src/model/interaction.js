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
      action: Function,
    }
  ],
  chat?: boolean,
  chatReadOnly?: boolean,
}
*/
const store = {
  interaction: {},
};

// Mutation

export const updateInteraction = (fn) => {
  setInteraction(
    produce(store.interaction, (interaction) => {
      fn(interaction);
    })
  );
};

export const setInteraction = (interaction) => {
  store.interaction = interaction;
  hooks.forEach((hook) => hook());
};

// Subscription

const hooks = new Set();

const subscribe = (onStoreChange) => {
  hooks.add(onStoreChange);
  return () => {
    hooks.delete(onStoreChange);
  };
};

const getSnapshot = () => store.interaction;

export const useInteraction = () => {
  return useSyncExternalStore(subscribe, getSnapshot);
};
