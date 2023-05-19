import { usePlayerIds } from '../model/player';
import './App.css';
import Player from './player/Player';

const App = () => {
  const playerIds = usePlayerIds();
  return (
    <div className="App">
      <div className="player-dev-wrapper">
        {playerIds.map((playerId) => (
          <Player key={playerId} playerId={playerId} />
        ))}
      </div>
    </div>
  );
};

export default App;
