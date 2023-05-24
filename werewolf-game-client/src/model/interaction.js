import { produce } from 'immer';

const model = {
  interaction: {
    chat: true,
    question: 'witch.healing.question',
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

export const subscribe = (onStoreChange) => {
  model.hooks.add(onStoreChange);
  return () => {
    model.hooks.delete(onStoreChange);
  };
};

export const getSnapshot = () => model.interaction;
