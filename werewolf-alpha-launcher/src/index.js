import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import './i18n';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));

const path = window.location.pathname;
const inRoom = path.match(/\/room\/?$/);
if (inRoom) {
  const params = new URLSearchParams(window.location.search);
  const path_prefix = path.slice(0, path.indexOf('/room') + 1);
  if (params.has('wat')) {
    document.cookie = `wat=${params.get('wat')}; path=${path_prefix}`;
    window.history.replaceState('hide-wat', null, path);
  } else if (window.history.state !== 'hide-wat') {
    window.location.pathname = path_prefix;
  }
}

root.render(
  <React.StrictMode>
    <App inRoom={inRoom} />
  </React.StrictMode>
);
