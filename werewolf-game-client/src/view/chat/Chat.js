import { useCallback, useEffect, useRef, useState } from 'react';
import { usePlayer, usePlayerIds } from '../../model/player';
import './Chat.css';
import { useChat } from '../../model/chat';
import { useTranslation } from 'react-i18next';

const TIMEOUT_MS = 100;

const Chat = ({ readOnly }) => {
  const { t } = useTranslation();
  const [messages, sendMessage] = useChat();
  // isScrolledToBottom is true if
  //  a) the chat is already scrolled to the bottom or
  //  b) the chat is currently in the animation of scrolling to the bottom.
  // It is stored in a state and a ref simultaneously.
  // It must only be updated by calling setIsScrolledToBottom() in order
  // to keep the state and the ref value synchronized.
  const [isScrolledToBottomState, setIsScrolledToBottomState] = useState(true);
  const isScrolledToBottomRef = useRef(isScrolledToBottomState);
  const [areUnreadMessages, setAreUnreadMessages] = useState(false);
  const setIsScrolledToBottom = useCallback((newState) => {
    if (newState) {
      setAreUnreadMessages(false);
    }
    isScrolledToBottomRef.current = newState;
    setIsScrolledToBottomState(newState);
  }, []);

  const scrollContainer = useRef(null);
  const calculateScrollTopMax = useCallback(() => {
    return (
      scrollContainer.current.scrollHeight -
      scrollContainer.current.clientHeight
    );
  }, []);
  const recalculateIsScrolledToBottom = useCallback(() => {
    setIsScrolledToBottom(
      scrollContainer.current.scrollTop >= calculateScrollTopMax()
    );
  }, [setIsScrolledToBottom, calculateScrollTopMax]);
  // scrollTimeout is used to track scrolling animations started
  // by scrollToBottom() and enable isScrolledToBottom to already be
  // true while the animation is still running. To do this a timeout
  // is started with a call to scrollToBottom() and each time a scroll
  // event is fired by the browser it is reset. When is finishes running
  // without being reset we assume the scroll animation ended.
  // Nevertheless we need to recalculate if we are at the bottom, as
  // the user might have scrolled up again before the timeout finished.
  const scrollTimeout = useRef(null);
  const resetScrollTimeout = useCallback(() => {
    if (scrollTimeout.current) {
      clearTimeout(scrollTimeout.current);
    }
    scrollTimeout.current = setTimeout(() => {
      scrollTimeout.current = null;
      recalculateIsScrolledToBottom();
    }, TIMEOUT_MS);
  }, [recalculateIsScrolledToBottom]);

  const scrollToBottom = useCallback(
    (smooth = true) => {
      scrollContainer.current.scroll({
        top: calculateScrollTopMax(),
        behavior: smooth ? 'smooth' : 'instant',
      });
      if (smooth) {
        resetScrollTimeout();
      }
      setIsScrolledToBottom(true);
    },
    [resetScrollTimeout, setIsScrolledToBottom, calculateScrollTopMax]
  );
  // event handler for scroll event
  const handleScroll = useCallback(() => {
    if (scrollTimeout.current) {
      resetScrollTimeout();
    } else {
      recalculateIsScrolledToBottom();
    }
  }, [resetScrollTimeout, recalculateIsScrolledToBottom]);
  // event handler for resize event
  useEffect(() => {
    const resizeObserver = new ResizeObserver(() => {
      if (isScrolledToBottomRef.current) {
        scrollToBottom(false);
      }
    });
    resizeObserver.observe(scrollContainer.current);
    return () => {
      resizeObserver.disconnect();
    };
  }, [scrollToBottom]);
  // event handler for new messages
  useEffect(() => {
    if (isScrolledToBottomRef.current) {
      scrollToBottom();
    } else {
      setAreUnreadMessages(true);
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
          ref={scrollContainer}
          onScroll={handleScroll}
          data-testid="scroll-container"
        >
          <div className="Chat__body">
            {messages.map((message, i) => (
              <ChatMessage key={i} message={message} />
            ))}
          </div>
        </div>
        {!isScrolledToBottomState && (
          <button
            className="Chat__scroll-down button"
            data-new-messages={areUnreadMessages || null}
            onClick={scrollToBottom}
            data-testid="scroll-down-button"
          />
        )}
      </div>
      {!readOnly && (
        <>
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
        </>
      )}
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
