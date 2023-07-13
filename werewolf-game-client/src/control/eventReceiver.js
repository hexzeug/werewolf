import { base_url, api_url } from './api';

const eventSource = new EventSource(api_url + '/event-stream');

eventSource.onopen = () => {
  console.log(`SSE: Connection to ${eventSource.url} opened.`);
};

eventSource.onerror = (event) => {
  switch (eventSource.readyState) {
    case EventSource.CONNECTING:
      console.warn(
        `SSE: Connection to ${eventSource.url} lost. Trying to reconnect...`
      );
      break;
    case EventSource.OPEN:
      try {
        handleServerError(JSON.parse(event.data));
      } catch {
        handleServerError(event.data);
      }
      break;
    case EventSource.CLOSED:
      console.error(`SSE: Connection to ${eventSource.url} failed.`);
      window.location.pathname = base_url;
      break;
    default:
      break;
  }
};

const handleServerError = (data) => {
  switch (data?.name) {
    case 'connectedfromotherlocation':
      console.error(
        'SSE: Connected from other location. Closing the connection.'
      );
      eventSource.close();
      if (process.env.NODE_ENV !== 'development') {
        alert('Connected from other location.');
      }
      break;
    default:
      console.error(
        `SSE: Server sent unparseable error data: "${data}". Closing the connection.`
      );
      eventSource.close();
      break;
  }
};

const listeners = {};

export const addEventListener = (type, listener) => {
  if (!listeners[type]) listeners[type] = new Map();
  const wrapper = ({ data }) => {
    try {
      listener(JSON.parse(data));
    } catch (ex) {
      console.error(`SSE: Error parsing data of event ${type}.`, ex);
    }
  };
  listeners[type].set(listener, wrapper);
  eventSource.addEventListener(type, wrapper);
};

export const removeEventListener = (type, listener) => {
  if (!listeners[type]) return;
  eventSource.removeEventListener(type, listeners[type].get(listener));
  listeners[type].delete(listener);
  if (listeners[type].size === 0) delete listeners[type];
};

export const close = eventSource.close;

export const getReadyState = () => eventSource.readyState;
