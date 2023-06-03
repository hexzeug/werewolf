import { render } from '@testing-library/react';
import PlayerList from './PlayerList';
import Player from '../player/Player';
import { usePlayerIds } from '../../model/player';

jest.mock('../../model/player');
jest.mock('../player/Player');

describe('player list component', () => {
  it('should render all players in correct order except first one', () => {
    const ids = ['abc', 'def', 'ghi', 'jkl', 'mno'];
    usePlayerIds.mockReturnValue(ids);
    render(<PlayerList />);
    expect(Player).toBeCalledTimes(ids.length - 1);
    Player.mock.calls.forEach((args, i) => {
      const { playerId } = args[0];
      expect(playerId).toBe(ids[i + 1]);
    });
  });
});
