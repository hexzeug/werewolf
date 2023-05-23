import { produce } from 'immer';
import { useSyncExternalStore } from 'react';

const model = {
  messages: [
    { author: 'abc3', text: 'Sishidayako ist sus.' },
    { author: 'abc1', text: 'Nein Ikree ist sus.' },
  ],
  hooks: new Set(),
};

export const receiveMessage = (message) => {
  model.messages = produce(model.messages, (messages) => {
    messages.push(message);
  });
  model.hooks.forEach((hook) => hook(model.messages));
};

export const subscribe = (onStoreChange) => {
  model.hooks.add(onStoreChange);
  return () => {
    model.hooks.delete(onStoreChange);
  };
};

export const getSnapshot = () => model.messages;

// debug / developement
const sendMessage = (text) => {
  receiveMessage({ text, author: 'abc1' });
};

export const useChat = () => {
  const messages = useSyncExternalStore(subscribe, getSnapshot);
  return [messages, sendMessage];
};
