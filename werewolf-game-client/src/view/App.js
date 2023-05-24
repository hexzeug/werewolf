import { usePlayerIds } from '../model/player';
import './App.css';
import Interaction from './interaction/Interaction';
import Chat from './chat/Chat';
import Narrator from './narrator/Narrator';
import Player, { OwnPlayer } from './player/Player';
import { useTranslation } from 'react-i18next';

const App = () => {
  const { t } = useTranslation();
  const playerIds = usePlayerIds();
  return (
    <div className="App">
      <Narrator />
      <div className="App__player-container">
        {playerIds.slice(1).map((playerId) => (
          <Player key={playerId} playerId={playerId} />
        ))}
      </div>
      <Chat />
      <Interaction
        question={t('witch.healing.question')}
        options={[
          {
            text: t('witch.healing.answer.yes'),
            action: () => {
              alert('yes');
            },
          },
          {
            text: t('witch.healing.answer.no'),
            action: () => {
              alert('no');
            },
          },
        ]}
      />
      <OwnPlayer playerId={playerIds[0]} />
    </div>
  );
};

export default App;
