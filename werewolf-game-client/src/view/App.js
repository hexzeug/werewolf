import './App.css';
import Interaction from './interaction/Interaction';
import Narrator from './narrator/Narrator';
import OwnPlayer from './ownplayer/OwnPlayer';
import PlayerList from './playerlist/PlayerList';

const App = () => {
  return (
    <div className="App">
      <Narrator />
      <PlayerList />
      <Interaction />
      <OwnPlayer />
    </div>
  );
};

export default App;
