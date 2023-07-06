import { updateInteraction } from '../../model/interaction';
import { narrate } from '../../model/narrator';
import { updateEachPlayer, updatePlayers } from '../../model/player';
import { conditionalAsyncFunction, roleIs } from '../../utils';
import api, { bodyIfOk } from '../api';
import { cache } from '../logic';

const internalCache = {};

export const startWitchHeal = async () => {
  const loadingVictim = conditionalAsyncFunction(roleIs('witch'), () =>
    bodyIfOk(api.get('/werewolves/victim'))
  );
  const loadingHealPotion = conditionalAsyncFunction(roleIs('witch'), () =>
    bodyIfOk(api.get('/witch/heal'))
  );
  await narrate('narrator.witch.awake');
  if (roleIs('witch')) {
    updatePlayers((players) => (players[cache.ownId].status = 'awake'));
  }
  await narrate('narrator.witch.info');
  internalCache.victim = await loadingVictim;
  if (roleIs('witch')) {
    updatePlayers((players) => (players[internalCache.victim].marked = true));
  }
  await narrate('narrator.witch.action.heal');
  if (!roleIs('witch')) return;
  const healPotion = await loadingHealPotion;
  let setAnswer;
  const answerPromise = new Promise((resolve) => (setAnswer = resolve));
  updateInteraction((interaction) => {
    if (healPotion) {
      interaction.question = { text: 'witch.question.heal' };
      interaction.options = [
        { text: 'witch.answer.yes', action: () => setAnswer(true) },
        { text: 'witch.answer.no', action: () => setAnswer(false) },
      ];
    } else {
      interaction.question = { text: 'witch.info.no_heal' };
      interaction.options = [
        { text: 'witch.answer.continue', action: () => setAnswer(false) },
      ];
    }
  });
  const answer = await answerPromise;
  api.post('/witch/heal', answer);
  updateInteraction((interaction) => {
    delete interaction.question;
    delete interaction.options;
  });
  updatePlayers((players) => (players[internalCache.victim].marked = false));
};

export const endWitchHeal = async () => {
  await narrate('narrator.witch.done.heal');
};

export const startWitchPoison = async () => {
  const loadingPoison = conditionalAsyncFunction(roleIs('witch'), () =>
    bodyIfOk(api.get('/witch/poison'))
  );
  await narrate('narrator.witch.action.poison');
  if (!roleIs('witch')) return;
  const poisonPotion = await loadingPoison;
  let setAnswer;
  const answerPromise = new Promise((resolve) => (setAnswer = resolve));
  const handlePlayerClick = (id) => {
    updatePlayers((players) => (players[id].marked = true));
    setAnswer(id);
  };
  updateInteraction((interaction) => {
    if (poisonPotion) {
      interaction.question = { text: 'witch.question.poison' };
      interaction.options = [
        { text: 'witch.answer.no', action: () => setAnswer(null) },
      ];
      updatePlayers(
        (players) => (players[internalCache.victim].disabled = true)
      );
      updateEachPlayer((player) => {
        if (player.status === 'dead' || player.disabled) return;
        player.onClick = handlePlayerClick;
      });
    } else {
      interaction.question = { text: 'witch.info.no_poison' };
      interaction.options = [
        { text: 'witch.answer.continue', action: () => setAnswer(null) },
      ];
    }
  });
  internalCache.poisoned = await answerPromise;
  api.post('/witch/poison', internalCache.poisoned);
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    delete player.onClick;
    player.disabled = false;
  });
  updateInteraction((interaction) => {
    delete interaction.question;
    delete interaction.options;
  });
};

export const endWitchPoison = async () => {
  await narrate('narrator.witch.done.poison');
  if (roleIs('witch')) {
    updatePlayers((players) => {
      players[internalCache.poisoned].marked = false;
      players[cache.ownId].status = 'sleeping';
    });
  }
  await narrate('narrator.witch.asleep');
};
