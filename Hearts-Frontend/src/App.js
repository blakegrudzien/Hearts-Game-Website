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
import ScoreBoard from './ScoreBoard';  // Import Scoreboard instead of ScoreBoard

function App() {
  localStorage.removeItem('gameStarted');
  const [gameStarted, setGameStarted] = useState(localStorage.getItem('gameStarted') === 'true' || false);
  const [trigger, setTrigger] = useState(0);
  const [turn, setTurn] = useState(0);
  const [gameState, setGameState] = useState("");

  const triggerApp = () => {
    setTrigger(prevTrigger => prevTrigger + 1); // Update the state to trigger a re-render
  };

  const startNewGame = () => {
    fetch('http://localhost:8080/startGame', {method: 'POST'})
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        } else {
          setGameStarted(true);
          localStorage.setItem('gameStarted', 'true');
        }
      })
      .catch(error => {
        console.error('Error:', error);
      });
  };

  

  return (
    

    <div className="App">
      <Header />
      {!gameStarted && <button className = "Start-Button" onClick={startNewGame}>Start New Game</button>}
     
      {gameStarted && <TopPlayer />}
      
      
      <div className="middle-row">
        {gameStarted && <LeftPlayer />}
        {gameStarted && <PlayedCards setGameState = {setGameState} setTurn={setTurn} turn={turn} gameState={gameState} />} 
        {gameStarted && <RightPlayer />}
      </div>
    
      {gameStarted && (
      <div className = "Bottom-row">
        <BottomPlayer setTurn={setTurn} triggerApp={triggerApp} turn={turn} gameState={gameState} setGameState={setGameState} />
        <ScoreBoard setGameState = {setGameState} gameState={gameState}/>  
      </div>
    )}
      
      
    </div>
  );
}

export default App;