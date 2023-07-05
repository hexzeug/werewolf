export const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

export const conditionalAsyncFunction = async (condition, fn, ...args) => {
  if (!condition) return;
  return await fn(...args);
};

export const isDay = (phase) => {
  return ['accusation', 'court', 'hunter', 'game_start', 'game_end'].includes(
    phase
  );
};
