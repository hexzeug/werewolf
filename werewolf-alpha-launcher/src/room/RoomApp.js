import { useTranslation } from 'react-i18next';
import GameJoiner from './GameJoiner';

function RoomApp({ wat }) {
  const { t } = useTranslation();

  return (
    <main>
      <section className="section">
        <GameJoiner token={wat} />
      </section>
      ;
    </main>
  );
}

export default RoomApp;
