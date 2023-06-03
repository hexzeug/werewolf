import { produce } from 'immer';
import { useSyncExternalStore } from 'react';

const model = {
  interaction: {
    chat: true,
    question: { text: 'witch.healing.question' },
    options: [
      {
        text: 'witch.healing.answer.yes',
        action: () => {
          alert('yes');
        },
      },
      {
        text: 'witch.healing.answer.no',
        action: () => {
          alert('no');
        },
      },
    ],
  },
  hooks: new Set(),
};

export const updateInteraction = (fn) => {
  setInteraction(
    produce(model.interaction, (interaction) => {
      fn(interaction);
    })
  );
};

export const setInteraction = (interaction) => {
  model.interaction = interaction;
};

const subscribe = (onStoreChange) => {
  model.hooks.add(onStoreChange);
  return () => {
    model.hooks.delete(onStoreChange);
  };
};

const getSnapshot = () => model.interaction;

export const useInteraction = () => {
  return useSyncExternalStore(subscribe, getSnapshot);
};
