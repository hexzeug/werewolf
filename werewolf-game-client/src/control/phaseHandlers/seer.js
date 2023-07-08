import { narrate } from '../../model/narrator';
import { updateEachPlayer, updatePlayers } from '../../model/player';
import api, { bodyIfOk } from '../api';
import { cache } from '../logic';

export const startSeer = async () => {
  await narrate('narrator.seer.awake');
  if (cache.me.role === 'seer') {
    updatePlayers((players) => (players[cache.ownId].status = 'awake'));
  }
  await narrate('narrator.seer.action');
  if (cache.me.role !== 'seer') return;
  let resolveSeen;
  const seenPromise = new Promise((resolve) => (resolveSeen = resolve));
  const handleClick = (id) => resolveSeen(id);
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    player.onClick = handleClick;
  });
  const seen = await seenPromise;
  api.post('/seer', seen);
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    delete player.onClick;
  });
};

export const endSeer = async () => {
  if (cache.me.role === 'seer') {
    const igtime = cache.igtime;
    bodyIfOk(api.get('/seer')).then((seens) =>
      updatePlayers((players) => {
        for (const id in seens) {
          if (seens[id].igtime !== igtime) continue;
          players[id].role = seens[id].role;
        }
      })
    );
  }
  await narrate('narrator.seer.done');
  await narrate('narrator.seer.asleep');
  if (cache.me.role !== 'seer') return;
  updatePlayers((players) => (players[cache.ownId].status = 'sleeping'));
};
