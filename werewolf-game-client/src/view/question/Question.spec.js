import { fireEvent, render, screen } from '@testing-library/react';
import Question from './Question';

jest.useFakeTimers();

describe('quersion component', () => {
  it('should render empty without throwing', () => {
    render(<Question />);
  });

  it('should render the question', () => {
    render(<Question question={{ text: 'test-question' }} />);
    expect(screen.getByText('test-question')).toBeInTheDocument();
  });

  it('should render options and call handlers on click', () => {
    const option1 = {
      text: 'test-option-1',
      action: jest.fn(),
    };
    const option2 = {
      text: 'test-option-2',
      action: jest.fn(),
    };
    render(
      <Question
        question={{ text: 'test-question' }}
        options={[option1, option2]}
      />
    );
    expect(screen.getByText('test-option-1')).toBeInTheDocument();
    expect(screen.getByText('test-option-2')).toBeInTheDocument();
    expect(option1.action).not.toBeCalled();
    expect(option2.action).not.toBeCalled();
    fireEvent.click(screen.getByText('test-option-1'));
    expect(option1.action).toBeCalled();
    expect(option2.action).not.toBeCalled();
    fireEvent.click(screen.getByText('test-option-2'));
    expect(option1.action).toBeCalled();
    expect(option2.action).toBeCalled();
  });
});
