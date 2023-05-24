import { useTranslation } from 'react-i18next';
import './Question.css';

const Question = ({ question, options }) => {
  const { t } = useTranslation();
  return (
    <div className="Interaction">
      <p className="Interaction__text">
        {t(question)}
        {options.map((option, i) => (
          <span className="Interaction__options">
            {i === 0 ? ' ' : ' / '}
            <button
              className="Interaction__option"
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
