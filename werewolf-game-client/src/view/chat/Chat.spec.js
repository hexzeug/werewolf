import {
  act,
  fireEvent,
  queryByAttribute,
  render,
  screen,
  within,
} from '@testing-library/react';
import { useChat } from '../../model/chat';
import { default as Chat } from './Chat';
import userEvent from '@testing-library/user-event';
import { usePlayer, usePlayerIds } from '../../model/player';

jest.mock('../../model/chat');
jest.mock('../../model/player');
jest.useFakeTimers();

let mockMessages = [];
beforeEach(() => {
  jest.clearAllTimers();

  const mockSend = jest.fn();
  mockMessages = [];
  useChat.mockImplementation(() => [mockMessages, mockSend]);
  usePlayer.mockImplementation((playerId) => ({ name: `${playerId}-name` }));
  usePlayerIds.mockReturnValue(['test-own-id', 'test-id']);

  global.ResizeObserver = jest.fn().mockImplementation(() => ({
    observe: jest.fn(),
    unobserve: jest.fn(),
    disconnect: jest.fn(),
  }));
  global.HTMLElement.prototype.scroll = jest.fn();
});

const setup = (jsx) => ({
  user: userEvent.setup({
    advanceTimers: jest.advanceTimersByTime,
    delay: 1,
  }),
  ...render(jsx),
});

const setupChat = () => {
  const view = setup(<Chat />);
  act(jest.runAllTimers);
  return {
    addMessage: (msg) => {
      mockMessages = [...mockMessages, msg];
      view.rerender(<Chat />);
      act(jest.runAllTimers);
    },
    ...view,
  };
};

describe('message display', () => {
  it('shoud display new messages', () => {
    const { addMessage } = setupChat();
    expect(screen.queryByText('test-id-name')).not.toBeInTheDocument();
    expect(screen.queryByText('test-message')).not.toBeInTheDocument();
    expect(screen.queryByText('test-id-2-name')).not.toBeInTheDocument();
    expect(screen.queryByText('test-message-2')).not.toBeInTheDocument();

    addMessage({ author: 'test-id', text: 'test-message' });
    expect(screen.getByText('test-id-name')).toBeInTheDocument();
    expect(screen.getByText('test-message')).toBeInTheDocument();
    expect(screen.queryByText('test-id-2-name')).not.toBeInTheDocument();
    expect(screen.queryByText('test-message-2')).not.toBeInTheDocument();

    addMessage({ author: 'test-id-2', text: 'test-message-2' });
    expect(screen.getByText('test-id-name')).toBeInTheDocument();
    expect(screen.getByText('test-message')).toBeInTheDocument();
    expect(screen.getByText('test-id-2-name')).toBeInTheDocument();
    expect(screen.getByText('test-message-2')).toBeInTheDocument();
  });

  it('should correctly mark own messages', () => {
    const { addMessage, container } = setupChat();
    const queryByDataOwn = queryByAttribute.bind(
      null,
      'data-own',
      container,
      /.*/
    );
    const getByDataOwn = queryByDataOwn;
    expect(queryByDataOwn()).not.toBeInTheDocument();
    expect(screen.queryByText('test-strange-message')).not.toBeInTheDocument();

    addMessage({ author: 'test-id', text: 'test-strange-message' });
    expect(queryByDataOwn()).not.toBeInTheDocument();
    expect(screen.getByText('test-strange-message')).toBeInTheDocument();

    addMessage({ author: 'test-own-id', text: 'test-own-message' });
    const ownMessage = getByDataOwn();
    expect(ownMessage).toBeInTheDocument();
    expect(
      within(ownMessage).getByText('test-own-message')
    ).toBeInTheDocument();
    expect(
      within(ownMessage).queryByText('test-strange-message')
    ).not.toBeInTheDocument();
    expect(screen.getByText('test-strange-message')).toBeInTheDocument();
  });
});

describe('chat scrolling behavior', () => {
  const setupScrollAPIMock = () => {
    const scrollTopCache = {};
    jest
      .spyOn(global.HTMLElement.prototype, 'scrollTop', 'get')
      .mockImplementation(function () {
        return scrollTopCache[this] || 0;
      });
    jest
      .spyOn(global.HTMLElement.prototype, 'scrollTop', 'set')
      .mockImplementation(function (val) {
        scrollTopCache[this] = val;
        fireEvent.scroll(this);
        act(jest.runAllTimers);
      });
    jest
      .spyOn(global.HTMLElement.prototype, 'scrollHeight', 'get')
      .mockImplementation(() => mockMessages.length);
    const scroll = global.HTMLElement.prototype.scroll.mockImplementation(
      function ({ top }) {
        this.scrollTop = top;
      }
    );
    return {
      scroll,
      clientHeight: jest
        .spyOn(global.HTMLElement.prototype, 'clientHeight', 'get')
        .mockReturnValue(0),
    };
  };

  it('should scroll on render', () => {
    mockMessages = [{ text: 'hello' }];
    const { scroll } = setupScrollAPIMock();
    setupChat();
    expect(scroll).toBeCalledTimes(1);
  });

  it('should scroll to bottom on render', () => {
    mockMessages = [{}, {}, {}];
    setupScrollAPIMock();
    setupChat();
    expect(screen.getByTestId('scroll-container').scrollTop).toBe(3);
  });

  it('should scroll to bottom on new message', () => {
    const { scroll } = setupScrollAPIMock();
    const { addMessage } = setupChat();
    const scrollContainer = screen.getByTestId('scroll-container');
    expect(scroll).toBeCalledTimes(1);
    expect(scrollContainer.scrollTop).toBe(0);
    addMessage({});
    expect(scroll).toBeCalledTimes(2);
    expect(scrollContainer.scrollTop).toBe(1);
    addMessage({});
    expect(scroll).toBeCalledTimes(3);
    expect(scrollContainer.scrollTop).toBe(2);
  });

  it('should display scroll down button when scrolled up', () => {
    mockMessages = [{}, {}];
    setupScrollAPIMock();
    setupChat();
    expect(screen.queryByTestId('scroll-down-button')).not.toBeInTheDocument();
    screen.getByTestId('scroll-container').scrollTop = 0; // user action
    expect(screen.getByTestId('scroll-down-button')).toBeInTheDocument();
  });

  it('should scroll down on click on scroll down button', async () => {
    mockMessages = [{}, {}];
    const { scroll } = setupScrollAPIMock();
    const { user } = setupChat();
    const scrollContainer = screen.getByTestId('scroll-container');
    expect(scroll).toBeCalledTimes(1);
    expect(scrollContainer.scrollTop).toBe(2);
    scrollContainer.scrollTop = 0;
    expect(scroll).toBeCalledTimes(1);
    expect(scrollContainer.scrollTop).toBe(0);
    await user.click(screen.getByTestId('scroll-down-button'));
    expect(scroll).toBeCalledTimes(2);
    expect(scrollContainer.scrollTop).toBe(2);
  });

  it('should display new messages dot on scroll down botton', async () => {
    mockMessages = [{}];
    setupScrollAPIMock();
    const { addMessage, user } = setupChat();
    const scrollContainer = screen.getByTestId('scroll-container');
    scrollContainer.scrollTop = 0;
    expect(screen.getByTestId('scroll-down-button')).not.toHaveAttribute(
      'data-new-messages'
    );
    addMessage({});
    expect(screen.getByTestId('scroll-down-button')).toHaveAttribute(
      'data-new-messages'
    );
    await user.click(screen.getByTestId('scroll-down-button'));
    scrollContainer.scrollTop = 0;
    expect(screen.getByTestId('scroll-down-button')).not.toHaveAttribute(
      'data-new-messages'
    );
  });
});

describe('message sending form', () => {
  it('should not call send with empty message', async () => {
    const { user } = setupChat();
    const input = screen.getByPlaceholderText('chat.input.placeholder');
    expect(input).toHaveValue('');
    await user.click(screen.getByText('chat.input.send'));
    expect(useChat()[1]).not.toBeCalled();
  });

  it('should update input field value when typing', async () => {
    const { user } = setupChat();
    const input = screen.getByPlaceholderText('chat.input.placeholder');
    expect(input).toHaveValue('');
    await user.type(input, 'test-msg');
    expect(input).toHaveValue('test-msg');
  });

  it('should send and clear input field content', async () => {
    const { user } = setupChat();
    const send = useChat()[1];
    const input = screen.getByPlaceholderText('chat.input.placeholder');
    expect(send).not.toBeCalled();
    await user.type(input, 'test-msg');
    await user.click(screen.getByText('chat.input.send'));
    expect(send).toBeCalledWith('test-msg');
    expect(send).toBeCalledTimes(1);
    expect(input).toHaveValue('');
  });
});
