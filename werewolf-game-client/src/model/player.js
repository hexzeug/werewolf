import { produce } from 'immer';
import { useEffect, useState } from 'react';

const model = {
  hooks: {
    playerIds: new Set(),
    playerMap: new Set(),
  },
  playerIds: ['abc1', 'abc2', 'abc3'],
  playerMap: {
    abc1: {
      name: 'hexszeug',
      status: 'dead',
      role: 'werewolf',
      marked: false,
      inLove: true,
      accused: false,
      playerTags: [],
      disabled: false,
      onClick: null,
    },
    abc2: {
      name: 'Sishidayako',
      status: 'sleeping',
      role: null,
      marked: true,
      inLove: false,
      accused: true,
      playerTags: ['abc3', 'abc1'],
      disabled: false,
      onClick: () => {
        updatePlayerMap((p) => {
          p.abc2.marked = !p.abc2.marked;
        });
      },
    },
    abc3: {
      name: 'Ikree',
      status: 'awake',
      role: 'villager',
      marked: false,
      inLove: false,
      accused: true,
      playerTags: ['abc2'],
      disabled: false,
      onClick: null,
    },
  },
};

export const updatePlayerMap = (fn) =>
  setPlayerMap(produce(model.playerMap, (playerMap) => fn(playerMap)));

export const setPlayerMap = (players) => {
  model.playerMap = players;
  model.hooks.playerMap.forEach((hook) => hook(players));
};

export const usePlayerMap = () => {
  const [playerMap, setPlayerMap] = useState(model.playerMap);
  useEffect(() => {
    model.hooks.playerMap.add(setPlayerMap);
    return () => {
      model.hooks.playerMap.delete(setPlayerMap);
    };
  }, []);
  return playerMap;
};

export const usePlayerIds = () => {
  const [playerIds, setPlayerIds] = useState(model.playerIds);
  useEffect(() => {
    model.hooks.playerIds.add(setPlayerIds);
    return () => {
      model.hooks.playerIds.delete(setPlayerIds);
    };
  }, []);
  return playerIds;
};
