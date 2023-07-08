import { cache } from './control/logic';

export const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

export const conditionalAsyncFunction = async (condition, fn) => {
  if (!condition) return;
  return await fn();
};

export const isDay = (phase) => {
  return ['accusation', 'court', 'hunter', 'game_start', 'game_end'].includes(
    phase
  );
};

export const roleIs = (role) => cache.me.role === role;

export const removeItemFromArray = (array, item) => {
  if (array.includes(item)) array.splice(array.indexOf(item), 1);
};
