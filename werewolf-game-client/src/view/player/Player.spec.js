import {
  render,
  screen,
  cleanup,
  fireEvent,
  queryByAttribute,
} from '@testing-library/react';
import Player from './Player';
import { usePlayer } from '../../model/player';

jest.mock('../../model/player');

afterAll(cleanup);

describe('player component', () => {
  it('should render empty without throwing', () => {
    render(<Player />);
  });

  it('should always render player name and if given role', () => {
    const mockDetails = {
      name: 'test-playername',
      role: 'test-role',
    };
    usePlayer
      .mockReturnValueOnce({
        ...mockDetails,
      })
      .mockReturnValueOnce({
        ...mockDetails,
        onClick: jest.fn(),
      })
      .mockReturnValueOnce({
        ...mockDetails,
        status: 'sleeping',
        role: undefined,
      })
      .mockReturnValueOnce({
        ...mockDetails,
        status: 'dead',
      });
    const { rerender } = render(<Player />);
    expect(screen.getByText('test-playername')).toBeInTheDocument();
    expect(screen.getByText(/test-role/i)).toBeInTheDocument();
    // clickable
    rerender(<Player />);
    expect(screen.getByText('test-playername')).toBeInTheDocument();
    expect(screen.getByText(/test-role/i)).toBeInTheDocument();
    // sleeping + no role
    rerender(<Player />);
    expect(screen.getByText('test-playername')).toBeInTheDocument();
    expect(screen.queryByText(/test-role/i)).not.toBeInTheDocument();
    // dead
    rerender(<Player />);
    expect(screen.getByText('test-playername')).toBeInTheDocument();
    expect(screen.getByText(/test-role/i)).toBeInTheDocument();
  });

  it('should render and update player status', () => {
    usePlayer
      .mockReturnValueOnce({ status: 'awake' })
      .mockReturnValueOnce({ status: 'sleeping' })
      .mockReturnValueOnce({ status: 'awake' })
      .mockReturnValueOnce({ status: 'dead' });
    const { rerender, container } = render(<Player />);
    const queryByDataDead = queryByAttribute.bind(
      null,
      'data-dead',
      container,
      /.*/
    );
    const getByDataDead = queryByDataDead;
    // awake
    expect(screen.queryByTestId('snore')).not.toBeInTheDocument();
    expect(queryByDataDead()).not.toBeInTheDocument();
    // sleeping
    rerender(<Player />);
    expect(screen.getByTestId('snore')).toBeInTheDocument();
    expect(queryByDataDead()).not.toBeInTheDocument();
    // awake
    rerender(<Player />);
    expect(screen.queryByTestId('snore')).not.toBeInTheDocument();
    expect(queryByDataDead()).not.toBeInTheDocument();
    // dead
    rerender(<Player />);
    expect(screen.queryByTestId('snore')).not.toBeInTheDocument();
    expect(getByDataDead()).toBeInTheDocument();
  });

  it('should display in love status', () => {
    usePlayer
      .mockReturnValueOnce({ inLove: false })
      .mockReturnValueOnce({ inLove: true });
    const { rerender } = render(<Player />);
    expect(screen.queryByTitle('player.alt.in_love')).not.toBeInTheDocument();
    rerender(<Player />);
    expect(screen.getByTitle('player.alt.in_love')).toBeInTheDocument();
  });

  it('should display accusation', () => {
    usePlayer
      .mockReturnValueOnce({ accused: false })
      .mockReturnValueOnce({ accused: true });
    const { rerender } = render(<Player />);
    expect(screen.queryByText('§')).not.toBeInTheDocument();
    rerender(<Player />);
    expect(screen.getByText('§')).toBeInTheDocument();
  });

  it('should be markable', () => {
    usePlayer
      .mockReturnValueOnce({ marked: false })
      .mockReturnValueOnce({ marked: true });
    const { rerender, container } = render(<Player />);
    const queryByDataMarked = queryByAttribute.bind(
      null,
      'data-marked',
      container,
      /.*/
    );
    const getByDataMarked = queryByDataMarked;
    expect(queryByDataMarked()).not.toBeInTheDocument();
    rerender(<Player />);
    expect(getByDataMarked()).toBeInTheDocument();
  });

  it('should render as button if clickable or disabled', () => {
    usePlayer
      .mockReturnValueOnce({})
      .mockReturnValueOnce({ disabled: true })
      .mockReturnValueOnce({ onClick: jest.fn() });
    const { rerender } = render(<Player />);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
    rerender(<Player />);
    expect(screen.getByRole('button')).toBeInTheDocument();
    rerender(<Player />);
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  it('should register onclick event handler', () => {
    const mockOnClick = jest.fn();
    usePlayer.mockReturnValueOnce({ onClick: mockOnClick });
    render(<Player />);
    expect(mockOnClick).toBeCalledTimes(0);
    fireEvent.click(screen.getByRole('button'));
    expect(mockOnClick).toBeCalledTimes(1);
  });
});

describe('player tags', () => {
  it('should render player name', () => {
    usePlayer
      .mockReturnValueOnce({ playerTags: ['test-id'] })
      .mockReturnValueOnce({ name: 'test-name' });
    render(<Player />);
    expect(screen.getByText('test-name')).toBeInTheDocument();
    expect(screen.queryByText('test-id')).not.toBeInTheDocument();
    expect(usePlayer).toBeCalledWith('test-id');
  });

  it('should render in correct order', () => {
    usePlayer
      .mockReturnValueOnce({ playerTags: ['test-a', 'test-b', 'test-c'] })
      .mockImplementation((playerId) => ({ name: `name-of-${playerId}` }));
    render(<Player />);
    expect(screen.getAllByText(/name-of-/i)).toHaveLength(3);
    expect(screen.getAllByText(/name-of-/i)[0].textContent).toEqual(
      'name-of-test-a'
    );
    expect(screen.getAllByText(/name-of-/i)[1].textContent).toEqual(
      'name-of-test-b'
    );
    expect(screen.getAllByText(/name-of-/i)[2].textContent).toEqual(
      'name-of-test-c'
    );
  });
});
