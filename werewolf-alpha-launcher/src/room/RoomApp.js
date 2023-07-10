import { useTranslation } from 'react-i18next';
import GameJoiner from './GameJoiner';

function RoomApp() {
  const { t } = useTranslation();

  return (
    <main>
      <section className="section">
        <div className="container">
          <div className="columns is-centered">
            <div className="column is-half">
              <div className="block">
                <div className="content">
                  <h1 className="has-text-centered">{t('room.title')}</h1>
                  <p>{t('room.info')}</p>
                </div>
              </div>
              <div className="block">
                <GameJoiner />
              </div>
            </div>
          </div>
        </div>
      </section>
      ;
    </main>
  );
}

export default RoomApp;
