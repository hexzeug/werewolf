import React from 'react';
import ReactDOM from 'react-dom/client';
import './i18n';
import 'bulma/css/bulma.css';
import '@creativebulma/bulma-tooltip/dist/bulma-tooltip.min.css';
import App from './App';
import { base_url, inRoom, setWat } from './api';

const root = ReactDOM.createRoot(document.getElementById('root'));

const path = window.location.pathname;
if (inRoom) {
  const params = new URLSearchParams(window.location.search);
  if (params.has('wat')) {
    setWat(params.get('wat'));
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
