import { useTranslation } from 'react-i18next';
import './Question.css';

const Question = ({ question, options }) => {
  const { t } = useTranslation();
  return (
    <div className="Question">
      <p className="Question__text">
        {t(question)}
        {options.map((option, i) => (
          <span className="Question__options">
            {i === 0 ? ' ' : ' / '}
            <button
              className="Question__option"
              key={i}
              onClick={option.action}
            >
              {t(option.text)}
            </button>
          </span>
        ))}
      </p>
    </div>
  );
};

export default Question;
