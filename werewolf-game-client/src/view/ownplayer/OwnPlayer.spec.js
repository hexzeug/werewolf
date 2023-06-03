import { render } from '@testing-library/react';
import Player from '../player/Player';
import { usePlayerIds } from '../../model/player';
import OwnPlayer from './OwnPlayer';

jest.mock('../../model/player');
jest.mock('../player/Player');

describe('own player component', () => {
  it('should render only own player', () => {
    usePlayerIds.mockReturnValue(['abc', 'def', 'ghi', 'jkl', 'mno']);
    render(<OwnPlayer />);
    expect(Player).toBeCalledTimes(1);
    expect(Player.mock.calls[0][0].playerId).toBe('abc');
  });
});
