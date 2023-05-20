import { useEffect, useRef, useState } from 'react';
import { usePlayer, usePlayerIds } from '../../model/player';
import './Chat.css';
import { useChat } from '../../model/chat';

const Chat = () => {
  const [messages, sendMessage] = useChat();
  const scrollContainer = useRef({ scrollHeight: 0, clientHeight: 0 });
  const scrollBottom = useRef(null);
  const oldScrollTopMax = useRef(0);
  useEffect(() => {
    const container = scrollContainer.current;
    if (container.scrollTop >= oldScrollTopMax.current) {
      scrollBottom.current.scrollIntoView({
        block: 'start',
        behavior: 'smooth',
      });
    }
    oldScrollTopMax.current = container.scrollHeight - container.clientHeight;
  }, [messages]);
  const [scrollTop, setScrollTop] = useState(0);
  return (
    <div className="Chat">
      <div
        className="Chat__scroll-container"
        ref={scrollContainer}
        onScroll={() => {
          setScrollTop(scrollContainer.current.scrollTop);
        }}
      >
        <div className="Chat__body">
          {messages.map((message, i) => (
            <ChatMessage message={message} key={i} />
          ))}
        </div>
        <div className="Chat__scroll-bottom" ref={scrollBottom} />
      </div>
      <hr />
      <div className="Chat__form">
        <button className="button" onClick={() => sendMessage('test')}>
          Send
        </button>
        <span>
          {' '}
          scrollTop: {scrollTop}, scrollTopMax (calculated):{' '}
          {scrollContainer.current.scrollHeight -
            scrollContainer.current.clientHeight}
        </span>
      </div>
    </div>
  );
};

const ChatMessage = ({ message }) => {
  const playerIds = usePlayerIds();
  const { name } = usePlayer(message.author) ?? {};
  return (
    <div
      className="Chat__message"
      data-own={playerIds[0] === message.author || null}
    >
      <p className="Chat__message__header">
        <span className="Chat__message__author">{name}</span>
      </p>
      <p className="Chat__message__text">{message.text}</p>
    </div>
  );
};

export default Chat;
