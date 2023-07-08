import Question from '../question/Question';
import './Interaction.css';
import Chat from '../chat/Chat';
import { useInteraction } from '../../model/interaction';

const Interaction = () => {
  const { question, options, chat, chatReadOnly } = useInteraction();
  return (
    <div className="Interaction">
      <div className="Interaction__element">
        {question && <Question question={question} options={options} />}
      </div>
      <div className="Interaction__element">
        {chat && <Chat readOnly={chatReadOnly} />}
      </div>
    </div>
  );
};

export default Interaction;
