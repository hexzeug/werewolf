import { narrate } from '../../model/narrator';
import { updateEachPlayer } from '../../model/player';
import { roleIs } from '../../utils';
import api, { bodyIfOk } from '../api';
import { animateDeaths, cache } from '../logic';

export const startHunter = async () => {
  let hunter;
  cache.playerOrder.forEach((id) => {
    if (cache.players[id].role === 'hunter') hunter = id;
  });
  await narrate('narrator.hunter.action', { player: cache.players[hunter] });
  if (!roleIs('hunter')) return;
  let setPlayer;
  const playerPromise = new Promise((resolve) => (setPlayer = resolve));
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    player.onClick = setPlayer;
  });
  const player = await playerPromise;
  api.post('/hunter', player);
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    delete player.onClick;
  });
};

export const endHunter = async () => {
  const loadingDeaths = bodyIfOk(api.get('/deaths'));
  await narrate('narrator.hunter.done');
  await animateDeaths(loadingDeaths);
};
