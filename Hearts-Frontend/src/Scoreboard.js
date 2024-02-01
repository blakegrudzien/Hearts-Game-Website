// ScoreBoard.js
import React, { useState, useEffect, useCallback } from 'react';

function ScoreBoard({ gameState, setGameState }) {
  const [scores, setScores] = useState([0, 0, 0, 0]);

  
  const fetchScores = useCallback(async () => {
    console.log("Fetching scores...");
    try {
      const response = await fetch('http://localhost:8080/getScores');
      const newScores = await response.json();  // Parse the response as JSON
  
      if (Array.isArray(newScores) && newScores.length === 4) {
        setScores(newScores);  // Update the scores state
        setGameState("Play");  // Update the gameState to 'Play'
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
      <p>Player 1: {scores[0]}</p>
      <p>Player 2: {scores[1]}</p>
      <p>Player 3: {scores[2]}</p>
      <p>Player 4: {scores[3]}</p>
    </div>
  );
}

export default ScoreBoard;