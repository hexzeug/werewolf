import { usePlayerIds } from '../model/player';
import './App.css';
import Narrator from './narrator/Narrator';
import Player, { OwnPlayer } from './player/Player';

const App = () => {
  const playerIds = usePlayerIds();
  return (
    <div className="App">
      <Narrator />
      <div className="App__player-container">
        {playerIds.slice(1).map((playerId) => (
          <Player key={playerId} playerId={playerId} />
        ))}
      </div>
      <OwnPlayer playerId={playerIds[0]} />
    </div>
  );
};

export default App;
