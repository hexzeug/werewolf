import { receiveMessage, setMessages } from '../model/chat';
import { setPlayerOrder, setPlayers } from '../model/player';
import api, { bodyIfOk } from './api';
import { addEventListener } from './eventReceiver';

export const loadGame = async () => {
  addEventListener('phaseevent', handlePhaseEvent);
  addEventListener('chatevent', receiveMessage);

  //loading
  const loadingNarration = bodyIfOk(api.get('/narrator', { priority: 'high' }));
  const loadingSelf = bodyIfOk(api.get('/me'));
  const loadingOwnId = loadingSelf.then((self) => Object.keys(self)[0]);
  const loadingPlayers = bodyIfOk(api.get('/players'));

  // conditional loading
  const loadingMidGameData = loadingNarration.then(({ currentPhase }) =>
    conditionalAsyncFunction(currentPhase !== 'game_start', loadMidGameData, {
      loadingOwnId,
      loadingSelf,
    })
  );
  const loadingAndSettingChat = loadingNarration.then(({ currentPhase }) =>
    conditionalAsyncFunction(currentPhase === 'accusation', () =>
      bodyIfOk(api.get('/chat')).then(setMessages)
    )
  );

  // await loading of general data
  const [{ currentPhase }, self, ownId, { players, ids: playerOrder }] = [
    await loadingNarration,
    await loadingSelf,
    await loadingOwnId,
    await loadingPlayers,
  ];

  // apply general data
  players[ownId].role = self[ownId].role;
  const status = [
    'accusation',
    'court',
    'hunter',
    'game_start',
    'game_end',
  ].includes(currentPhase)
    ? 'awake'
    : 'sleeping';
  playerOrder.forEach((id) => (players[id].status = status));
  // rotate playerOrder so ownId is at index 0
  playerOrder.push(...playerOrder.splice(0, playerOrder.indexOf(ownId)));

  // await loading of mid-game data
  const midGameData = await loadingMidGameData;

  // apply mid-game data
  if (midGameData) transformMidGameData(players, midGameData);

  // set data
  await loadingAndSettingChat;
  setPlayers(players);
  setPlayerOrder(playerOrder);
};

const conditionalAsyncFunction = async (condition, fn, ...args) => {
  if (!condition) return;
  return await fn(...args);
};

const loadMidGameData = async ({ loadingOwnId, loadingSelf }) => {
  const loadingDeaths = bodyIfOk(api.get('/deaths'));
  const loadingCoupleRes = api.get('/couple');
  const [ownId, self] = [await loadingOwnId, await loadingSelf];
  const loadingSeer = conditionalAsyncFunction(
    self[ownId].role === 'seer',
    () => bodyIfOk(api.get('/seer'))
  );
  const { ok: canReadCouple, body: couple } = await loadingCoupleRes;
  const loadingCouple = conditionalAsyncFunction(
    canReadCouple,
    async () => couple
  );
  const loadingCoupleRoles = conditionalAsyncFunction(
    canReadCouple && couple.includes(ownId),
    () => bodyIfOk(api.get('/couple/roles'))
  );
  return {
    deaths: await loadingDeaths,
    seer: await loadingSeer,
    couple: await loadingCouple,
    coupleRoles: await loadingCoupleRoles,
  };
};

const transformMidGameData = (
  players,
  { deaths, seer, couple, coupleRoles }
) => {
  for (const id in players) {
    if (deaths[id]) {
      players[id].status = 'dead';
      players[id].role = deaths[id];
    }
    if (seer && seer[id]) {
      players[id].role = seer[id].role;
    }
    if (couple && couple.includes(id)) {
      players[id].inLove = true;
    }
    if (coupleRoles && coupleRoles[id]) {
      players[id].role = coupleRoles[id].role;
    }
  }
};

const handlePhaseEvent = (phase) => {};
