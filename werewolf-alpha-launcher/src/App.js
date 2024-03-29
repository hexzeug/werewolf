import HomeApp from './home/HomeApp';
import RoomApp from './room/RoomApp';
import { useTranslation } from 'react-i18next';

function App({ inRoom }) {
  const { t } = useTranslation();

  return (
    <div className="App">
      <header className="level section">
        <div className="level-item">
          <a href="/" className="title is-2">
            {t('document.title')}
          </a>
        </div>
      </header>
      {inRoom ? <RoomApp /> : <HomeApp />}
      <footer className="footer">
        <div className="content has-text-centered">
          <p>
            Werewolf by <a href="https://github.com/hexszeug">@hexszeug</a>
          </p>
          <p>&copy; 2023 Siemen Zielke</p>
        </div>
      </footer>
    </div>
  );
}

export default App;
