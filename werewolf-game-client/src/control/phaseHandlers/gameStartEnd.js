import { narrate } from '../../model/narrator';
import api, { bodyIfOk } from '../api';

export const endGameStart = async () => {};

export const startGameEnd = async () => {
  const winner = await bodyIfOk(api.get('/winner'));
  await narrate(`narrator.winner.${winner}`);
};
