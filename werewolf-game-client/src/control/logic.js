import { Mutex } from 'async-mutex';
import { receiveMessage, setMessages } from '../model/chat';
import {
  getPlayerOrder,
  getPlayers,
  setPlayerOrder,
  setPlayers,
  updateEachPlayer,
  updatePlayers,
} from '../model/player';
import api, { bodyIfOk } from './api';
import { addEventListener } from './eventReceiver';
import { narrate } from '../model/narrator';
import { conditionalAsyncFunction, isDay } from '../utils';
import { endCupid, startCupid } from './phaseHandlers/cupid';
import { endGameStart, startGameEnd } from './phaseHandlers/gameStartEnd';
import { endSeer, startSeer } from './phaseHandlers/seer';
import { endWerewolves, startWerewolves } from './phaseHandlers/werewolves';
import {
  endWitchHeal,
  endWitchPoison,
  startWitchHeal,
  startWitchPoison,
} from './phaseHandlers/witch';
import { endHunter, startHunter } from './phaseHandlers/hunter';
import {
  endAccusation,
  endCourt,
  startAccusation,
  startCourt,
} from './phaseHandlers/accusationCourt';
import { setDaytime } from '../model/daytime';

export const cache = {
  get players() {
    return getPlayers();
  },
  get me() {
    return getPlayers()[getPlayerOrder()[0]];
  },
  get playerOrder() {
    return getPlayerOrder();
  },
  get ownId() {
    return getPlayerOrder()[0];
  },
};

const mutex = new Mutex();

export const loadGame = async () => {
  await mutex.runExclusive(async () => {
    addEventListener('phaseevent', handlePhaseEvent);
    addEventListener('chatmessageevent', receiveMessage);

    //loading
    const loadingNarration = bodyIfOk(
      api.get('/narrator', { priority: 'high' })
    );
    const loadingSelf = bodyIfOk(api.get('/me'));
    const loadingOwnId = loadingSelf.then((self) => Object.keys(self)[0]);
    const loadingPlayers = bodyIfOk(api.get('/players'));

    // conditional loading
    const loadingMidGameData = loadingNarration.then(({ currentPhase }) =>
      conditionalAsyncFunction(currentPhase !== 'game_start', () =>
        loadMidGameData({
          loadingOwnId,
          loadingSelf,
        })
      )
    );
    const loadingAndSettingChat = loadingNarration.then(({ currentPhase }) =>
      conditionalAsyncFunction(currentPhase !== 'game_start', () =>
        bodyIfOk(api.get('/chat')).then(setMessages)
      )
    );

    // await loading of general data
    const [
      { currentPhase, igtime },
      self,
      ownId,
      { players, ids: playerOrder },
    ] = [
      await loadingNarration,
      await loadingSelf,
      await loadingOwnId,
      await loadingPlayers,
    ];
    cache.igtime = igtime;
    cache.phase = currentPhase;

    // apply general data
    players[ownId].role = self[ownId].role;
    const status = isDay(currentPhase) ? 'awake' : 'sleeping';
    playerOrder.forEach((id) => (players[id].status = status));
    // rotate playerOrder so ownId is at index 0
    playerOrder.push(...playerOrder.splice(0, playerOrder.indexOf(ownId)));

    // await loading of mid-game data
    const midGameData = await loadingMidGameData;

    // apply mid-game data
    if (midGameData) transformMidGameData(players, midGameData);

    // set data
    await loadingAndSettingChat;
    setDaytime(isDay(currentPhase) ? 'day' : 'night');
    setPlayers(players);
    setPlayerOrder(playerOrder);
  });
  if (cache.phase !== 'game_start') await handlePhaseStart(cache.phase);
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
      players[id].role = deaths[id].role;
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

const phaseStartHandler = {
  game_end: startGameEnd,
  cupid: startCupid,
  seer: startSeer,
  werewolves: startWerewolves,
  witch_heal: startWitchHeal,
  witch_poison: startWitchPoison,
  hunter: startHunter,
  accusation: startAccusation,
  court: startCourt,
};

const phaseEndHandler = {
  game_start: endGameStart,
  cupid: endCupid,
  seer: endSeer,
  werewolves: endWerewolves,
  witch_heal: endWitchHeal,
  witch_poison: endWitchPoison,
  hunter: endHunter,
  accusation: endAccusation,
  court: endCourt,
};

const handlePhaseStart = async (phase) => {
  if (phaseStartHandler[phase]) await phaseStartHandler[phase]();
  else console.warn(`No handler for phase start of ${phase} found.`);
};

const handlePhaseEnd = async (phase) => {
  if (phaseEndHandler[phase]) await phaseEndHandler[phase]();
  else console.warn(`No handler for phase end of ${phase} found.`);
};

const handlePhaseEvent = async (phase) => {
  await mutex.runExclusive(async () => {
    console.log(`server-side phase start of ${phase}`);
    const oldPhase = cache.phase;
    await handlePhaseEnd(oldPhase);
    cache.igtime++;
    if (!isDay(oldPhase) && isDay(phase)) {
      const loadingDeaths = bodyIfOk(api.get('/deaths'));
      await narrate('narrator.generic.sunrise');
      setDaytime('day');
      updateEachPlayer((player) => {
        if (player.status !== 'dead') player.status = 'awake';
      });
      const deaths = await animateDeaths(loadingDeaths);
      if (deaths > 0) cache.igtime++;
    } else if (isDay(oldPhase) && !isDay(phase)) {
      await narrate('narrator.generic.sunset');
      setDaytime('night');
      updateEachPlayer((player) => {
        if (player.status !== 'dead') player.status = 'sleeping';
      });
    }
    console.log(`client-side phase start of ${phase}`);
    cache.phase = phase;
    await handlePhaseStart(phase);
  });
};

export const animateDeaths = async (loadingDeaths) => {
  const deaths = await loadingDeaths;
  const normalDeaths = [];
  const heartbreakDeaths = [];
  for (const id in deaths) {
    if (deaths[id].igtime !== cache.igtime) continue;
    if (deaths[id].reason !== 'heartbreak') normalDeaths.push(id);
    else heartbreakDeaths.push(id);
  }
  if (normalDeaths.length + heartbreakDeaths.length === 0) return 0;
  for (const id of normalDeaths) {
    if (id === cache.ownId) {
      await narrate(`narrator.death.own.${deaths[id].reason}`);
    } else {
      await narrate(`narrator.death.${deaths[id].reason}`, {
        player: { ...cache.players[id], role: deaths[id].role },
      });
    }
    updatePlayers(({ [id]: player }) => {
      player.status = 'dead';
      player.role = deaths[id].role;
    });
  }
  if (heartbreakDeaths.length === 0) return normalDeaths.length;
  const heartbrokenId = heartbreakDeaths[0];
  const couple = await bodyIfOk(api.get('/couple'));
  if (heartbrokenId === cache.ownId) {
    await narrate('narrator.death.own.heartbreak');
  } else {
    await narrate('narrator.death.heartbreak', {
      player: {
        ...cache.players[heartbrokenId],
        role: deaths[heartbrokenId].role,
      },
    });
  }
  updatePlayers((players) => {
    players[heartbrokenId].status = 'dead';
    players[heartbrokenId].role = deaths[heartbrokenId].role;
    players[couple[0]].inLove = true;
    players[couple[1]].inLove = true;
  });
  return normalDeaths.length + 1;
};
