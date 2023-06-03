import { produceWithPatches } from 'immer';
import { useSyncExternalStore } from 'react';

// Storage

/* 
player: {
  name: String,
  status: String,
  role?: String,
  marked?: boolean,
  inLove?: boolean,
  accused?: boolean,
  playerTags?: Array, (player ids)
  disabled?: boolean,
  onClick?: Function,
}
 */
const store = {
  playerOrder: [],
  players: {},
};

// Mutation

export const setPlayerOrder = (playerOrder) => {
  store.playerOrder = playerOrder;
  const playersChanged = new Set(playerOrder);
  hooks.forEach((hook) => {
    if (hook.type === 'list') {
      hook.onStoreChange();
    } else if (hook.type === 'player' && playersChanged.has(hook.playerId)) {
      hook.onStoreChange();
    }
  });
};

export const updatePlayers = (fn) =>
  setPlayers(
    ...produceWithPatches(store.players, (playerMap) => fn(playerMap))
  );

export const setPlayers = (players, patches = undefined) => {
  const playersChanged = new Set();
  if (patches) {
    patches.forEach((patch) => {
      playersChanged.add(patch.path[0]);
    });
  } else {
    Object.keys(store.players).forEach((id) => playersChanged.add(id));
    Object.keys(players).forEach((id) => playersChanged.add(id));
  }
  store.players = players;
  hooks.forEach((hook) => {
    if (hook.type === 'player' && playersChanged.has(hook.playerId)) {
      hook.onStoreChange();
    }
  });
};

// Subscription

const hooks = new Set();

const subscribePlayerCache = {};
const subscribePlayer = (playerId) => {
  if (!subscribePlayerCache[playerId]) {
    subscribePlayerCache[playerId] = (onStoreChange) => {
      const hook = { type: 'player', playerId, onStoreChange };
      hooks.add(hook);
      return () => {
        hooks.delete(hook);
      };
    };
  }
  return subscribePlayerCache[playerId];
};

const getSnapshotPlayerCache = {};
const getSnapshotPlayer = (playerId) => {
  if (!getSnapshotPlayerCache[playerId]) {
    getSnapshotPlayerCache[playerId] = () => store.players[playerId];
  }
  return getSnapshotPlayerCache[playerId];
};

const subscribeList = (onStoreChange) => {
  const hook = { type: 'list', onStoreChange };
  hooks.add(hook);
  return () => {
    hooks.delete(hook);
  };
};

const getSnapshotList = () => store.playerOrder;

export const usePlayer = (playerId) =>
  useSyncExternalStore(subscribePlayer(playerId), getSnapshotPlayer(playerId));
export const usePlayerIds = () =>
  useSyncExternalStore(subscribeList, getSnapshotList);

window.setPlayers = setPlayers;
window.setPlayerOrder = setPlayerOrder;
window.hooks = () => hooks;
