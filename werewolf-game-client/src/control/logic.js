import { receiveMessage, setMessages } from '../model/chat';
import { setPlayerOrder, setPlayers } from '../model/player';
import api, { bodyIfOk } from './api';
import { addEventListener } from './eventReceiver';

export const loadGame = async () => {
  addEventListener('phaseevent', handlePhaseEvent);
  addEventListener('chatevent', receiveMessage);
  const loadingNarration = bodyIfOk(api.get('/narrator'));
  const loadingSelf = bodyIfOk(api.get('/me'));
  const loadingPlayers = bodyIfOk(api.get('/players'));
  const { currentPhase } = await loadingNarration;
  let players, self, ownId;
  if (currentPhase !== 'game_start') {
    const loadingDeaths = bodyIfOk(api.get('/deaths'));
    let loadingChat;
    if (currentPhase === 'accusation') loadingChat = bodyIfOk(api.get('/chat'));
    const loadingCouple = api.get('/couple');
    self = await loadingSelf;
    ownId = destructOwnId(self);
    let loadingSeer;
    if (self[ownId].role === 'seer') loadingSeer = bodyIfOk(api.get('/seer'));
    const coupleRes = await loadingCouple;
    if (coupleRes.ok) {
      if (coupleRes.body.includes(ownId)) {
        const coupleRoles = await bodyIfOk(api.get('/couple/roles'));
        ({ players } = await loadingPlayers);
        for (const id in coupleRoles) {
          players[id].role = coupleRoles[id].role;
        }
      }
      if (!players) ({ players } = await loadingPlayers);
      coupleRes.body.forEach((id) => (players[id].inLove = true));
    }
    if (!players) ({ players } = await loadingPlayers);
    if (loadingSeer) {
      const seer = await loadingSeer;
      for (const id in seer) {
        players[id].role = seer[id].role;
      }
    }
    const deaths = await loadingDeaths;
    for (const id in deaths) {
      players[id].role = deaths[id].role;
      players[id].status = 'dead';
    }
    if (loadingChat) setMessages(await loadingChat);
  }
  if (!players) ({ players } = await loadingPlayers);
  if (!self) {
    self = await loadingSelf;
    ownId = destructOwnId(self);
  }
  const { ids: playerOrder } = await loadingPlayers;

  // rotate ids so ownId is at index 0
  playerOrder.push(...playerOrder.splice(0, playerOrder.indexOf(ownId)));
  players[ownId].role = self[ownId].role;
  const status = ['accusation', 'court', 'hunter'].includes(currentPhase)
    ? 'awake'
    : 'sleeping';
  playerOrder.forEach((id) => {
    if (!players[id].status) players[id].status = status;
  });
  setPlayers(players);
  setPlayerOrder(playerOrder);
};

const destructOwnId = (self) => Object.keys(self)[0];

const handlePhaseEvent = (phase) => {};
