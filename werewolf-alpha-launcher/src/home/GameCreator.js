import { useState } from 'react';
import api from '../api';
import { useTranslation } from 'react-i18next';

const GameCreator = ({ setTokens }) => {
  const { t } = useTranslation();
  const [input, setInput] = useState('');
  const [help, setHelp] = useState(null);
  const [tooltip, setTooltip] = useState(null);
  const [status, setStatus] = useState('idle');

  const handleChange = (event) => {
    const value = event.target.value;
    setInput(value);
    setTooltip(null);
    const number = Number(value);
    if (isNaN(number) || !value || number < 2 || number > 18) {
      setStatus('error');
      setHelp('home.form.help.error');
    } else {
      setStatus('valid');
      setHelp(null);
    }
  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    setTooltip(null);
    setStatus('loading');
    const { ok, body: tokens } = await api.post(`/game?players=${input}`);
    if (ok) {
      setTokens(tokens);
    } else {
      setStatus('valid');
      setTooltip('home.form.tooltip.cannot_create');
    }
  };

  return (
    <div className="hero is-medium">
      <div className="hero-body">
        <form onSubmit={handleSubmit}>
          <div className="field has-addons has-addons-centered">
            <div className="control is-large">
              <input
                className={`input is-large ${
                  status === 'error' ? 'is-danger' : ''
                }`}
                type="text"
                placeholder={t('home.form.players.placeholder')}
                value={input}
                onChange={handleChange}
                readOnly={status === 'loading'}
              />
              {help && <p className="help is-danger">{t(help)}</p>}
            </div>
            <div className="control is-large">
              <button
                className={`button is-large has-tooltip-arrow has-tooltip-active has-tooltip-danger ${
                  status === 'loading' ? 'is-loading' : ''
                }`}
                type="submit"
                disabled={status === 'error' && status === 'idle'}
                data-tooltip={tooltip ? t(tooltip) : null}
              >
                {t('home.form.send.value')}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default GameCreator;
