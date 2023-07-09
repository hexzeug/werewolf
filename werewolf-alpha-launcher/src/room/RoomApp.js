import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import api from '../api';

function RoomApp() {
  const { t } = useTranslation();
  const [name, setName] = useState('');
  const [done, setDone] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (name === '') return;
    setName('');
    const { ok, status } = await api.post(`/name?name=${name}`);
    if (ok) setDone(true);
    else console.error(`failed setting name: ${status}`);
  };

  return (
    <div className="RoomApp">
      <h2>{t('room.title')}</h2>
      {!done && (
        <section>
          <p>{t('room.cta')}</p>
          <form onSubmit={handleSubmit}>
            <label>
              {t('room.form.name.label')}
              <input
                type="text"
                name="name"
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder={t('room.form.name.placeholder')}
              />
            </label>
            <input
              type="submit"
              className="button"
              value={t('room.form.send.label')}
            />
          </form>
        </section>
      )}
    </div>
  );
}

export default RoomApp;
