import { render, screen } from '@testing-library/react';
import Narrator from './Narrator';
import { useNarrator } from '../../model/narrator';

jest.mock('../../model/narrator');

describe('narrator component', () => {
  it('should render the given text', () => {
    useNarrator.mockReturnValue([
      { text: 'test-narration' },
      { current: null },
    ]);
    render(<Narrator />);
    expect(screen.getByText('test-narration')).toBeInTheDocument();
  });

  it('should pass dom element to ref', () => {
    const ref = { current: null };
    useNarrator.mockReturnValue([{ text: 'test-narration' }, ref]);
    render(<Narrator />);
    expect(ref.current).toBe(screen.getByText('test-narration'));
  });
});
