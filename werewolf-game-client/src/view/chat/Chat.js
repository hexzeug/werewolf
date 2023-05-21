import { useCallback, useEffect, useRef, useState } from 'react';
import { usePlayer, usePlayerIds } from '../../model/player';
import './Chat.css';
import { useChat } from '../../model/chat';
import { useTranslation } from 'react-i18next';

const TIMEOUT_MS = 100;

const Chat = () => {
  const { t } = useTranslation();
  const [messages, sendMessage] = useChat();
  const [atBottomState, setAtBottomState] = useState(true);
  const atBottomRef = useRef(atBottomState);
  const [newMessages, setNewMessages] = useState(false);
  const setAtBottom = useCallback((newState) => {
    if (newState) {
      setNewMessages(false);
    }
    atBottomRef.current = newState;
    setAtBottomState(newState);
  }, []);
  const refScrollContainer = useRef(null);
  const scrollTimeout = useRef(null);
  const recalculateAtBottom = useCallback(() => {
    const scrollTopMax =
      refScrollContainer.current.scrollHeight -
      refScrollContainer.current.clientHeight;
    const scrollTop = refScrollContainer.current.scrollTop;
    setAtBottom(scrollTop >= scrollTopMax);
  }, [setAtBottom]);
  const resetScrollTimeout = useCallback(() => {
    if (scrollTimeout.current) {
      clearTimeout(scrollTimeout.current);
    }
    scrollTimeout.current = setTimeout(() => {
      scrollTimeout.current = null;
      recalculateAtBottom();
    }, TIMEOUT_MS);
  }, [recalculateAtBottom]);
  const scrollToBottom = useCallback(() => {
    const scrollTopMax =
      refScrollContainer.current.scrollHeight -
      refScrollContainer.current.clientHeight;
    refScrollContainer.current.scroll({
      top: scrollTopMax,
      behavior: 'smooth',
    });
    resetScrollTimeout();
    setAtBottom(true);
  }, [resetScrollTimeout, setAtBottom]);
  const handleScroll = useCallback(() => {
    if (scrollTimeout.current) {
      resetScrollTimeout();
    } else {
      recalculateAtBottom();
    }
  }, [resetScrollTimeout, recalculateAtBottom]);
  useEffect(() => {
    if (atBottomRef.current) {
      scrollToBottom();
    } else {
      setNewMessages(true);
    }
  }, [messages, scrollToBottom]);
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
      <div className="Chat__main-container">
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
        </div>
        {!atBottomState && (
          <button
            className="Chat__scroll-down button"
            data-new-messages={newMessages || null}
            onClick={scrollToBottom}
          />
        )}
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
