import { render } from '@testing-library/react';
import Interaction from './Interaction';
import { useInteraction } from '../../model/interaction';
import Question from '../question/Question';
import Chat from '../chat/Chat';

jest.mock('../../model/interaction');
jest.mock('../question/Question');
jest.mock('../chat/Chat');

describe('interaction component', () => {
  it('should render empty without throwing', () => {
    useInteraction.mockReturnValue({});
    render(<Interaction />);
  });

  it('should render question without chat', () => {
    useInteraction.mockReturnValue({ question: { text: 'test-question' } });
    render(<Interaction />);
    expect(Question).toBeCalled();
    expect(Chat).not.toBeCalled();
  });

  it('should render chat without question', () => {
    useInteraction.mockReturnValue({ chat: true });
    render(<Interaction />);
    expect(Question).not.toBeCalled();
    expect(Chat).toBeCalled();
  });

  it('should render question and chat', () => {
    useInteraction.mockReturnValue({
      question: { text: 'test-question' },
      chat: true,
    });
    render(<Interaction />);
    expect(Question).toBeCalled();
    expect(Chat).toBeCalled();
  });
});
