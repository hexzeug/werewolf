import { useTranslation } from 'react-i18next';
import './Question.css';

const Question = ({ question, options }) => {
  const { t } = useTranslation();
  return (
    <div className="Question">
      <p className="Question__text">
        {t(question.text, question.data)}
        <span className="Question__options">
          {options.map((option, i) => (
            <span key={i}>
              {i === 0 ? ' ' : ' / '}
              <button className="Question__option" onClick={option.action}>
                {t(option.text, option.data)}
              </button>
            </span>
          ))}
        </span>
      </p>
    </div>
  );
};

export default Question;
