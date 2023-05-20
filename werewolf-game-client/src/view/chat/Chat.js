import { useCallback, useEffect, useRef, useState } from 'react';
import { usePlayer, usePlayerIds } from '../../model/player';
import './Chat.css';
import { useChat } from '../../model/chat';
import { useTranslation } from 'react-i18next';

const Chat = () => {
  const { t } = useTranslation();
  const [messages, sendMessage] = useChat();
  // scrolling on new message
  const refScrollContainer = useRef(null);
  const refScrollBottom = useRef(null);
  const oldScrollTopMax = useRef(null);
  const scrollTimeout = useRef(null);
  const resetScrollTimeout = () => {
    if (scrollTimeout.current) {
      clearTimeout(scrollTimeout.current);
    }
    scrollTimeout.current = setTimeout(() => {
      scrollTimeout.current = null;
    }, 100);
  };
  const handleScroll = useCallback(() => {
    if (scrollTimeout.current) {
      resetScrollTimeout();
    }
  }, []);
  useEffect(() => {
    const container = refScrollContainer.current;
    if (
      !oldScrollTopMax.current ||
      scrollTimeout.current ||
      container.scrollTop >= oldScrollTopMax.current
    ) {
      refScrollBottom.current.scrollIntoView({
        block: 'start',
        behavior: 'smooth',
      });
      resetScrollTimeout();
    }
    oldScrollTopMax.current = container.scrollHeight - container.clientHeight;
  }, [messages]);
  // send messages
  const [inputMsg, setInputMsg] = useState('');
  const handleInputChange = useCallback((e) => setInputMsg(e.target.value), []);
  const handleSubmit = useCallback(
    (e) => {
      e.preventDefault();
      if (inputMsg.length > 0 && !inputMsg.match(/^\s+$/)) {
        sendMessage(inputMsg);
      }
      setInputMsg('');
    },
    [inputMsg, sendMessage]
  );
  return (
    <div className="Chat">
      <div
        className="Chat__scroll-container"
        ref={refScrollContainer}
        onScroll={handleScroll}
      >
        <div className="Chat__body">
          {messages.map((message, i) => (
            <ChatMessage message={message} key={i} />
          ))}
        </div>
        <div className="Chat__scroll-bottom" ref={refScrollBottom} />
      </div>
      <hr />
      <form className="Chat__form" onSubmit={handleSubmit}>
        <input
          className="Chat__input"
          type="text"
          value={inputMsg}
          onChange={handleInputChange}
          placeholder={t('chat.input.placeholder')}
          autoComplete="off"
        />
        <button type="submit" className="Chat__submit button">
          {t('chat.input.send')}
        </button>
      </form>
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
