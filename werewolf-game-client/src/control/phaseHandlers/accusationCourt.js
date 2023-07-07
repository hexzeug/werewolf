import { updateInteraction } from '../../model/interaction';
import { narrate } from '../../model/narrator';
import { updateEachPlayer, updatePlayers } from '../../model/player';
import { conditionalAsyncFunction } from '../../utils';
import api, { bodyIfOk } from '../api';
import { addEventListener, removeEventListener } from '../eventReceiver';
import { animateDeaths, cache } from '../logic';

const internalCache = {
  accusations: {},
};

const handleAccusationEvent = ({ accuser, accused }) => {
  if (accuser === cache.ownId) return;
  displayAccusation({ accuser, accused });
};

const displayAccusation = ({ accuser, accused }) => {
  updatePlayers((players) => {
    const oldAccused = internalCache.accusations[accuser];
    if (oldAccused) {
      players[oldAccused].accused =
        players[oldAccused].marked =
        players[oldAccused].disabled =
          false;
      players[oldAccused].playerTags = [];
    }
    internalCache.accusations[accuser] = accused;
    if (accused) {
      players[accused].accused = true;
      players[accused].marked = true;
      if (accuser !== cache.ownId) {
        players[accused].disabled = true;
      }
      players[accused].playerTags = [accuser];
    }
  });
};

const handleAccusationClick = (id) => {
  if (id === internalCache.accusations[cache.ownId]) {
    api.delete('/accusation');
    displayAccusation({ accuser: cache.ownId, accused: null });
  } else {
    api.put('/accusation', id);
    displayAccusation({ accuser: cache.ownId, accused: id });
  }
};

export const startAccusation = async () => {
  internalCache.accusations = {};
  const loadingAccusations = bodyIfOk(api.get('/accusations'));
  addEventListener('accusationevent', handleAccusationEvent);
  await narrate('narrator.accusation.action');
  const accusations = await loadingAccusations;
  accusations.forEach((accusation) => {
    displayAccusation(accusation);
  });
  updateInteraction((interaction) => (interaction.chat = true));
  if (cache.me.status === 'dead') return;
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    player.onClick = handleAccusationClick;
  });
};

export const endAccusation = async () => {
  removeEventListener('accusationevent', handleAccusationEvent);
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    delete player.onClick;
    player.playerTags = [];
    player.marked = player.disabled = false;
  });
  updateInteraction((interaction) => (interaction.chat = false));
  if (Object.keys(internalCache.accusations).length > 0) {
    await narrate('narrator.accusation.done');
  } else {
    await narrate('narrator.accusation.no_accusations');
  }
};

const handleVoteEvent = ({ voter, vote }) => {
  if (voter === cache.ownId) return;
  displayVote({ voter, vote });
};

const displayVote = ({ voter, vote }) => {
  updatePlayers((players) => {
    if (!players[vote].playerTags) players[vote].playerTags = [];
    players[vote].playerTags.push(voter);
    players[vote].marked = true;
  });
};

export const startCourt = async () => {
  const missingAccusations =
    Object.keys(internalCache.accusations).length === 0;
  const loadingAccusations = conditionalAsyncFunction(missingAccusations, () =>
    bodyIfOk(api.get('/accusations'))
  );
  const loadingVotes = bodyIfOk(api.get('/votes'));
  addEventListener('courtvoteevent', handleVoteEvent);

  await narrate('narrator.court.action');

  if (missingAccusations) {
    const accusations = await loadingAccusations;
    updatePlayers((players) => {
      accusations.forEach(({ accused }) => {
        players[accused].accused = true;
      });
    });
  }

  const votes = await loadingVotes;
  votes.forEach(displayVote);

  if (cache.me.status === 'dead') return;

  let setVote;
  const votePromise = new Promise((resolve) => (setVote = resolve));
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    if (player.accused) {
      player.onClick = setVote;
    } else {
      player.disabled = true;
    }
  });

  const vote = await votePromise;
  api.post('/vote', vote);
  displayVote({ voter: cache.ownId, vote: vote });
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    delete player.onClick;
    player.disabled = false;
  });
};

export const endCourt = async () => {
  const loadingDeaths = bodyIfOk(api.get('/deaths'));
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    player.accused = player.marked = false;
    player.playerTags = [];
  });
  await narrate('narrator.court.done');
  await animateDeaths(loadingDeaths);
};
