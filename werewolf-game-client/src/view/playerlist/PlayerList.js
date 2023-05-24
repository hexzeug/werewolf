import { usePlayerIds } from '../../model/player';
import './PlayerList.css';
import Player from '../player/Player';

const PlayerList = () => {
  const playerIds = usePlayerIds();
  return (
    <div className="PlayerList">
      {playerIds.slice(1).map((playerId) => (
        <Player key={playerId} playerId={playerId} />
      ))}
    </div>
  );
};

export default PlayerList;
