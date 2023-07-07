import { produce } from 'immer';
import { useSyncExternalStore } from 'react';
import api from '../control/api';

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

const sendMessage = (text) => {
  api.post('/chat', text);
};

// Mutation

export const receiveMessage = (message) =>
  setMessages(
    produce(store.messages, (messages) => {
      messages.push(message);
    })
  );

export const setMessages = (messages) => {
  store.messages = messages;
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
