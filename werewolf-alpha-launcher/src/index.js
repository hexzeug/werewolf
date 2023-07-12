import React from 'react';
import ReactDOM from 'react-dom/client';
import './i18n';
import 'bulma/css/bulma.css';
import '@creativebulma/bulma-tooltip/dist/bulma-tooltip.min.css';
import App from './App';
import api, { base_url, inRoom, redirect_url, setWat } from './api';

const root = ReactDOM.createRoot(document.getElementById('root'));

const path = window.location.pathname;
const { ok, body } = await api.get('/running');
if (ok && body === true) {
  window.location.pathname = redirect_url;
}
if (inRoom) {
  const params = new URLSearchParams(window.location.search);
  if (params.has('wat')) {
    setWat(params.get('wat'));
    window.history.replaceState(null, null, path);
  } else if (!ok) {
    window.location.pathname = base_url;
  }
}

root.render(
  <React.StrictMode>
    <App inRoom={inRoom} />
  </React.StrictMode>
);
