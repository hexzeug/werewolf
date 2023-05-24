import { useTranslation } from 'react-i18next';
import './Question.css';

const Question = ({ question, options }) => {
  const { t } = useTranslation();
  return (
    <div className="Question">
      <p className="Question__text">
        {t(question)}
        <span className="Question__options">
          {options.map((option, i) => (
            <>
              {i === 0 ? ' ' : ' / '}
              <button
                key={i}
                className="Question__option"
                onClick={option.action}
              >
                {t(option.text)}
              </button>
            </>
          ))}
        </span>
      </p>
    </div>
  );
};

export default Question;
