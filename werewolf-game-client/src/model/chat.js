import { produce } from 'immer';
import { useEffect, useState } from 'react';

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
  model.hooks.forEach((hook) => hook.setMessages(model.messages));
};

// debug / developement
const sendMessage = (text) => {
  receiveMessage({ text, author: 'abc1' });
};

export const useChat = () => {
  const [messages, setMessages] = useState(model.messages);
  useEffect(() => {
    model.hooks.add({ setMessages });
    return () => model.hooks.delete({ setMessages });
  }, []);
  return [messages, sendMessage];
};
