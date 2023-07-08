import { useSyncExternalStore } from 'react';
import { usePlayer } from './player';
import { cache } from '../control/logic';

// Storage

/*
daytime: string (night | day)
*/
const store = {
  daytime: 'day',
};

// Mutation

export const setDaytime = (daytime) => {
  store.daytime = daytime;
  hooks.forEach((hook) => hook());
};

// Access

const hooks = new Set();

const subscribe = (onStoreChange) => {
  hooks.add(onStoreChange);
  return () => hooks.delete(onStoreChange);
};

const getSnapshot = () => store.daytime;

export const useDaytime = () => {
  const daytime = useSyncExternalStore(subscribe, getSnapshot);
  const ownPlayer = usePlayer(cache.ownId);
  if (ownPlayer?.status === 'awake') {
    return daytime === 'day' ? 'day' : 'awake';
  } else {
    return daytime;
  }
};
