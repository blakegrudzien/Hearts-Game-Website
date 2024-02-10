/**
 * This is the main component of the application. It is the parent component of all the other components.
 */



/**
 * Import all Necessary Components
 */
import './index.css';
import Header from './Header';
import Score from './Score';
import TopPlayer from './TopPlayer';
import { useState } from 'react';
import RightPlayer from './RightPlayer';
import LeftPlayer from './LeftPlayer';
import PlayedCards from './PlayedCards';
import BottomPlayer from './BottomPlayer';






function App() {
  localStorage.removeItem('gameStarted');
  const [gameStarted, setGameStarted] = useState(localStorage.getItem('gameStarted') === 'true' || false);
  const [trigger, setTrigger] = useState(0);
  const [turn, setTurn] = useState(0);
  const [gameState, setGameState] = useState("");




  /**
 * Triggers a reload of the app when the gamestate changes
 */
  const triggerApp = () => {
    setTrigger(prevTrigger => prevTrigger + 1); // Update the state to trigger a re-render
  };



  /**
  * Starts a new game by calling the startGame function from the backend, and sets the gameStarted state to true 
 */
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



  
  /**
  * Renders all the components of the game 
  */

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
        <Score setGameState = {setGameState} gameState={gameState}/>  
      </div>
    )}

    </div>
  );
}

export default App;