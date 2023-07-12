import { useState } from 'react';
import api from '../api';
import { useTranslation } from 'react-i18next';

const Feedback = () => {
  const { t } = useTranslation();
  const [feedback, setFeedback] = useState('');
  const [tooltip, setTooltip] = useState(null);

  const handleSubmit = (event) => {
    event.preventDefault();
    if (feedback === '') {
      setTooltip('home.feedback.no_empty');
      return;
    }
    api.post('/feedback', feedback);
    setFeedback('');
    setTooltip('home.feedback.sent');
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="field">
        <div className="control">
          <textarea
            className="textarea has-fixed-size"
            placeholder={t('home.feedback.placeholder')}
            rows={Math.max(feedback.split(/\r\n|\r|\n/).length, 5)}
            onChange={(e) => setFeedback(e.target.value)}
            value={feedback}
          />
        </div>
      </div>
      <div className="field">
        <div className="control">
          <button
            className="button has-tooltip-arrow"
            type="submit"
            onMouseLeave={() => setTooltip(null)}
            data-tooltip={tooltip ? t(tooltip) : null}
          >
            {t('home.feedback.send')}
          </button>
        </div>
      </div>
    </form>
  );
};

export default Feedback;
