import HomeApp from './home/HomeApp';
import RoomApp from './room/RoomApp';
import './App.css';
import { useTranslation } from 'react-i18next';

function App({ inRoom }) {
  const { t } = useTranslation();

  return (
    <div className="App">
      <header className="AppHeader">
        <h1>{t('document.title')}</h1>
      </header>
      <body className="AppBody">{inRoom ? <RoomApp /> : <HomeApp />}</body>
    </div>
  );
}

export default App;
