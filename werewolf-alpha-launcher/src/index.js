import React from 'react';
import ReactDOM from 'react-dom/client';
import './i18n';
import 'bulma/css/bulma.css';
import App from './App';
import { base_url, inRoom } from './api';

const root = ReactDOM.createRoot(document.getElementById('root'));

const path = window.location.pathname;
if (inRoom) {
  const params = new URLSearchParams(window.location.search);
  if (params.has('wat')) {
    document.cookie = `wat=${params.get('wat')}; path=${base_url}`;
    window.history.replaceState('hide-wat', null, path);
  } else if (window.history.state !== 'hide-wat') {
    window.location.pathname = base_url;
  }
}

root.render(
  <React.StrictMode>
    <App inRoom={inRoom} />
  </React.StrictMode>
);
