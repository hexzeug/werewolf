import { usePlayerIds } from '../../model/player';
import './OwnPlayer.css';
import Player from '../player/Player';

const OwnPlayer = () => {
  const playerIds = usePlayerIds();
  return (
    <div className="OwnPlayer">
      <Player playerId={playerIds[0]} />
    </div>
  );
};

export default OwnPlayer;
