import { narrate } from '../../model/narrator';
import { updateEachPlayer, updatePlayers } from '../../model/player';
import api, { bodyIfOk } from '../api';
import { cache } from '../logic';

const coupleFactory = () => {
  const couple = [];
  let resolveCouple;
  const couplePromise = new Promise((resolve) => (resolveCouple = resolve));
  const clickHandler = (id) => {
    if (couple.length > 1) return;
    else if (couple.length === 0) {
      couple.push(id);
      updatePlayers(
        (players) => (players[id].disabled = players[id].marked = true)
      );
    } else if (couple[0] !== id) {
      couple.push(id);
      updatePlayers((players) => {
        players[couple[0]].disabled = false;
        players[id].marked = true;
      });
      resolveCouple(couple);
    }
  };
  return { couplePromise, clickHandler };
};

export const startCupid = async () => {
  await narrate('narrator.cupid.awake');
  await narrate('narrator.cupid.action');
  if (cache.me.role !== 'cupid') return;
  updatePlayers((players) => (players[cache.ownId].status = 'awake'));
  const { clickHandler, couplePromise } = coupleFactory();
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    player.onClick = clickHandler;
  });
  const couple = await couplePromise;
  api.post('/couple', couple);
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    delete player.onClick;
  });
};

export const endCupid = async () => {
  const loadingCouple = api.get('/couple');
  if (cache.me.role === 'cupid') {
    updateEachPlayer((player) => {
      if (player.marked) {
        player.marked = false;
        player.inLove = true;
      }
    });
  }
  await narrate('narrator.cupid.asleep');
  if (cache.me.role === 'cupid') {
    updatePlayers((players) => (players[cache.ownId].status = 'sleeping'));
  }
  await narrate('narrator.couple.awake');
  const { ok: canReadCouple, body: couple } = await loadingCouple;
  const inCouple = canReadCouple && couple.includes(cache.ownId);
  if (inCouple) {
    bodyIfOk(api.get('/couple/roles')).then((roles) => {
      updatePlayers((players) => {
        players[couple[0]].role = roles[couple[0]].role;
        players[couple[1]].role = roles[couple[1]].role;
      });
    });
    updatePlayers((players) => {
      players[couple[0]].status = players[couple[1]].status = 'awake';
      players[couple[0]].inLove = players[couple[1]].inLove = true;
    });
  }
  await narrate('narrator.couple.action');
  if (inCouple) {
    updatePlayers((players) => {
      players[couple[0]].status = players[couple[1]].status = 'sleeping';
    });
  }
  await narrate('narrator.couple.asleep');
};
