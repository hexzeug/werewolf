import Question from '../question/Question';
import Chat from '../chat/Chat';
import { useSyncExternalStore } from 'react';
import { getSnapshot, subscribe } from '../../model/interaction';

const Interaction = () => {
  const interaction = useSyncExternalStore(subscribe, getSnapshot);
  return (
    <div className="Interaction">
      <Question question={interaction.question} options={interaction.options} />
      {interaction.chat && <Chat />}
    </div>
  );
};

export default Interaction;
