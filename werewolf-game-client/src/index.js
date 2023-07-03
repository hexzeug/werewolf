import React from 'react';
import ReactDOM from 'react-dom/client';
import './i18n';
import './index.css';
import App from './view/App';
import { enablePatches } from 'immer';
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
