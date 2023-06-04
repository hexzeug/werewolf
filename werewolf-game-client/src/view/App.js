import './App.css';
import Interaction from './interaction/Interaction';
import Narrator from './narrator/Narrator';
import OwnPlayer from './ownplayer/OwnPlayer';
import PlayerList from './playerlist/PlayerList';

const App = () => {
  return (
    <div className="App">
      <div className="App__element">
        <Narrator />
      </div>
      <div className="App__element">
        <PlayerList />
      </div>
      <div className="App__element">
        <Interaction />
      </div>
      <div className="App__element">
        <OwnPlayer />
      </div>
    </div>
  );
};

export default App;
