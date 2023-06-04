import { render } from '@testing-library/react';
import PlayerList from './PlayerList';
import Player from '../player/Player';
import { usePlayerIds } from '../../model/player';

jest.mock('../../model/player');
jest.mock('../player/Player');

describe('player list component', () => {
  it('should render all players in correct order except first one', () => {
    usePlayerIds.mockReturnValue(['abc', 'def', 'ghi', 'jkl', 'mno']);
    render(<PlayerList />);
    expect(Player).toBeCalledTimes(4);
    // not using toHaveBeenNthCalledWith as it requires to expect all arguments
    expect(Player.mock.calls[0][0].playerId).toBe('def');
    expect(Player.mock.calls[1][0].playerId).toBe('ghi');
    expect(Player.mock.calls[2][0].playerId).toBe('jkl');
    expect(Player.mock.calls[3][0].playerId).toBe('mno');
  });
});
