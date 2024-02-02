// ScoreBoard.js
import React, { useState, useEffect, useCallback } from 'react';

function ScoreBoard({ gameState, setGameState }) {
  const [scores, setScores] = useState([0, 0, 0, 0]);
  const [roundFinished, setRoundFinished] = useState(false);




  const startNewRound = async () => {
    try {
      const response = await fetch('http://localhost:8080/startNewRound', {
        method: 'POST',
      });
  
      if (!response.ok) {
        throw new Error('HTTP error ' + response.status);
      }
  
      // After successfully starting a new round, reset roundFinished state
      setRoundFinished(false);
      setGameState("Swap");
    } catch (error) {
      console.error('Error:', error);
    }
  };

  
  const fetchScores = useCallback(async () => {
    console.log("Fetching scores...");
    try {
      const response = await fetch('http://localhost:8080/getScores');
      const newScores = await response.json();  // Parse the response as JSON
  
      if (Array.isArray(newScores) && newScores.length === 4) {
        setScores(newScores);  // Update the scores state

        const trickResponse = await fetch('http://localhost:8080/getTrickNumber');
        const trickNumber = await trickResponse.json();
        console.log("Trick number is: " + trickNumber);
        if (trickNumber === 13) {
          setGameState("Swap");
          setRoundFinished(true);
          console.log("Round finished");
        } else {
          setGameState("Play");
          setRoundFinished(false);
        }
        
          // Update the gameState to 'Play'
      } else {
        console.error('Invalid scores:', newScores);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  }, [setScores, setGameState]);  // Add dependencies here
  
  useEffect(() => {
    console.log('ScoreBoard is re-rendering');
    if(gameState === 'Scoring'){
      fetchScores();
    }
  }, [gameState, fetchScores]);

  return (
    <div>
      {roundFinished && (
  <button onClick={startNewRound}>Start Next Round</button>
)}
      <p>Player 1: {scores[0]}</p>
      <p>Player 2: {scores[1]}</p>
      <p>Player 3: {scores[2]}</p>
      <p>Player 4: {scores[3]}</p>
    </div>
  );
}

export default ScoreBoard;