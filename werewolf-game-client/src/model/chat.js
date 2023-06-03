import { produce } from 'immer';
import { useSyncExternalStore } from 'react';

// Storage

/*
message: {
  text: String,
  author: String,
}
*/
const store = {
  messages: [],
};

// debug / developement
const sendMessage = (text) => {
  receiveMessage({ text, author: 'abc1' });
};

// Mutation

export const receiveMessage = (message) => {
  store.messages = produce(store.messages, (messages) => {
    messages.push(message);
  });
  hooks.forEach((hook) => hook(store.messages));
};

// Subscription

const hooks = new Set();

const subscribe = (onStoreChange) => {
  hooks.add(onStoreChange);
  return () => {
    hooks.delete(onStoreChange);
  };
};

const getSnapshot = () => store.messages;

export const useChat = () => {
  const messages = useSyncExternalStore(subscribe, getSnapshot);
  return [messages, sendMessage];
};
