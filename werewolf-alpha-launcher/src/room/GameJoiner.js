import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import api, { startPolling } from '../api';

function GameJoiner() {
  const { t } = useTranslation();
  const [name, setName] = useState('');
  const [help, setHelp] = useState(null);
  const [tooltip, setTooltip] = useState(null);
  const [waiting, setWaiting] = useState(false);

  const handleChange = (event) => {
    const value = event.target.value;
    setName(value);
    setTooltip(null);
    if (value.length < 2 || value.length > 20) {
      setHelp('room.join.help.error');
    } else {
      setHelp(null);
    }
  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    if (name.length < 2 || name.length > 20) return;
    const { ok, status } = await api.post(`/name?name=${name}`);
    if (ok || status === 403) {
      setWaiting(true);
      startPolling();
    } else {
      setTooltip('room.join.tooltip.error');
    }
  };

  return (
    <div className="block">
      {waiting ? (
        <div className="content has-text-centered">
          <h2>{t('room.join.waiting.title', { name })}</h2>
          <p>{t('room.join.waiting.info')}</p>
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          <div className="field has-addons">
            <div className="control is-expanded">
              <input
                className={`input is-large ${help !== null ? 'is-danger' : ''}`}
                type="text"
                value={name}
                onChange={handleChange}
                placeholder={t('room.form.name.placeholder')}
              />
              <p className="help is-danger">{help && t(help)}</p>
            </div>
            <div className="control">
              <button
                className="button is-large has-tooltip-arrow has-tooltip-active"
                type="submit"
                disabled={help !== null}
                data-tooltip={tooltip ? t(tooltip) : null}
              >
                {t('room.form.send.value')}
              </button>
            </div>
          </div>
        </form>
      )}
    </div>
  );
}

export default GameJoiner;
