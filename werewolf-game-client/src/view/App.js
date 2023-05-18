import { useState } from 'react';
import './App.css';
import Player from './player/Player';

const App = () => {
  const [count, setCount] = useState(6);
  window.setCount = setCount;
  return (
    <div className="App">
      <div className="player-dev-wrapper">
        {Array.from(Array(count)).map((i) => (
          <Player key={i} player={{}} />
        ))}
      </div>
    </div>
  );
};

export default App;
