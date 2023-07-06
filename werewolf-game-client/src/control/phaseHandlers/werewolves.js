import { narrate } from '../../model/narrator';
import { updateEachPlayer, updatePlayers } from '../../model/player';
import {
  conditionalAsyncFunction,
  removeItemFromArray,
  roleIs,
} from '../../utils';
import api, { bodyIfOk } from '../api';
import { addEventListener, removeEventListener } from '../eventReceiver';
import { cache } from '../logic';

const internalCache = {
  votes: {},
};

const updateVoteDisplay = ({ voter, vote }) => {
  updatePlayers((players) => {
    if (internalCache.votes[voter]) {
      // remove old vote
      const oldVote = internalCache.votes[voter];
      removeItemFromArray(players[oldVote].playerTags, voter);
      if (players[oldVote].playerTags.length === 0) {
        players[oldVote].marked = false;
      }
    }
    internalCache.votes[voter] = vote;
    if (vote) {
      // add new vote
      if (!players[vote].playerTags) players[vote].playerTags = [];
      players[vote].marked = true;
      players[vote].playerTags.push(voter);
    }
  });
};

const handleClick = (id) => {
  if (internalCache.votes[cache.ownId] === id) {
    api.delete('/werewolves/vote');
    updateVoteDisplay({ voter: cache.ownId, vote: null });
  } else {
    api.put('/werewolves/vote', id);
    updateVoteDisplay({ voter: cache.ownId, vote: id });
  }
};

const handleVoteEvent = ({ voter, vote }) => {
  console.log('werewolf vote: ', { voter, vote });
  if (voter === cache.ownId) return;
  updateVoteDisplay({ voter, vote });
};

export const startWerewolves = async () => {
  const loadingWerewolves = conditionalAsyncFunction(roleIs('werewolf'), () =>
    bodyIfOk(api.get('/werewolves'))
  );
  const loadingVotes = conditionalAsyncFunction(roleIs('werewolf'), () =>
    bodyIfOk(api.get('/werewolves/votes'))
  );
  await narrate('narrator.werewolves.awake');
  const werewolves = await loadingWerewolves;
  if (roleIs('werewolf')) {
    updatePlayers((players) => {
      werewolves.forEach((id) => {
        if (players[id].status === 'dead') return;
        players[id].status = 'awake';
      });
    });
  }
  await narrate('narrator.werewolves.action');
  if (!roleIs('werewolf')) return;
  internalCache.votes = {};
  const votes = await loadingVotes;
  console.log(votes);
  updatePlayers((players) => {
    for (const id in votes) {
      players[id].marked = true;
      players[id].playerTags = votes[id];
      votes[id].forEach((voter) => (internalCache.votes[voter] = id));
    }
  });
  addEventListener('werewolfvoteevent', handleVoteEvent);
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    player.onClick = handleClick;
  });
};

export const endWerewolves = async () => {
  const loadingVictim = conditionalAsyncFunction(roleIs('werewolf'), () =>
    bodyIfOk(api.get('/werewolves/victim'))
  );
  if (roleIs('werewolf')) {
    removeEventListener('werewolfvoteevent', handleVoteEvent);
    updateEachPlayer((player) => {
      if (player.status === 'dead') return;
      delete player.onClick;
      player.playerTags = [];
      player.marked = false;
    });
  }
  await narrate('narrator.werewolves.done');
  if (roleIs('werewolf')) {
    const vicitm = await loadingVictim;
    if (vicitm) updatePlayers((players) => (players[vicitm].marked = true));
  }
  await narrate('narrator.werewolves.asleep');
  if (!roleIs('werewolf')) return;
  updateEachPlayer((player) => {
    if (player.status === 'dead') return;
    player.status = 'sleeping';
    player.marked = false;
  });
};
