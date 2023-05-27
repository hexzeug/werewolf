import { useSyncExternalStore } from 'react';
import Question from '../question/Question';
import './Interaction.css';
import Chat from '../chat/Chat';
import { getSnapshot, subscribe } from '../../model/interaction';

const Interaction = () => {
  const interaction = useSyncExternalStore(subscribe, getSnapshot);
  return (
    <div className="Interaction">
      <div className="Interaction__element">
        <Question
          question={interaction.question}
          options={interaction.options}
        />
      </div>
      <div className="Interaction__element">{interaction.chat && <Chat />}</div>
    </div>
  );
};

export default Interaction;
