import React from 'react';
import ReactDOM from 'react-dom/client';
import { enablePatches } from 'immer';
import App from './view/App';
import './index.css';
import './i18n';
import { loadGame } from './control/logic';

// for debugging
if (window.location.hash.length > 10) {
  document.cookie = `wat=${window.location.hash.slice(1)}`;
  window.location.hash = '';
}

enablePatches();

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

loadGame();
