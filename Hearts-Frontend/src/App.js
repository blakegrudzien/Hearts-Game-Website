import './index.css';
import Header from './Header';
import Content from './Content';
import Footer from './Footer';
import TopPlayer from './TopPlayer';
import { useState } from 'react';
import RightPlayer from './RightPlayer';
import LeftPlayer from './LeftPlayer';
import PlayedCards from './PlayedCards';
import BottomPlayer from './BottomPlayer';

function App() {
  const [gameStarted, setGameStarted] = useState(false);
  const [trigger, setTrigger] = useState(0);
  const [turn, setTurn] = useState(0);
  const [gameState, setGameState] = useState("");

  const triggerApp = () => {
    setTrigger(prevTrigger => prevTrigger + 1); // Update the state to trigger a re-render
  };

  const startNewGame = () => {
    fetch('http://localhost:8080/startGame', { method: 'POST' })
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
      })
      .catch(error => {
        console.error('Error:', error);
      });
    setGameStarted(true);
  };

  console.log('App is re-rendering'); 

  return (
    <div className="App">
      <Header />
      <button onClick={startNewGame}>Start New Game</button>
      {gameStarted && <TopPlayer />}
      
      <div className="middle-row">
        {gameStarted && <LeftPlayer />}
        {gameStarted && <PlayedCards setTurn={setTurn} turn={turn} gameState={gameState} />} 
        {gameStarted && <RightPlayer />}
      </div>
    
      {gameStarted && <BottomPlayer setTurn={setTurn} triggerApp={triggerApp} turn={turn} gameState={gameState} setGameState={setGameState} />}
      
      <Content />
      <Footer />
    </div>
  );
}

export default App;