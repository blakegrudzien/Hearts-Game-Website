
/**
 * This function controls the scoreboard component of the game, calculating and displaying the scores of the players
 */

import React, { useState, useEffect, useCallback } from 'react';



function ScoreBoard({ gameState, setGameState }) {
  const [scores, setScores] = useState([0, 0, 0, 0]);
  const [roundFinished, setRoundFinished] = useState(false);



/**
 * After a round is finished and the scores are displayed, 
 * this function is called to start a new round, it does 
 * this by calling the backend function "startNewRound" and 
 * setting the gamestate to "Swap" and RoundFinished to false
 */
  const startNewRound = async () => {
    try {
      const response = await fetch('http://localhost:8080/startNewRound', {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error('HTTP error ' + response.status);
      }
      setRoundFinished(false);
      setGameState("Swap");
    } catch (error) {
      console.error('Error:', error);
    }
  };

  


  /**
 * This function fetches the scores from the backend and 
 * updates the scores state, if it is the final trick,
 *  it starts a newround, otherwise it just counts up the points
 */
  const fetchScores = useCallback(async () => {
    console.log("Fetching scores...");
    try {
      const response = await fetch('http://localhost:8080/getScores');
      const newScores = await response.json(); 
  
      if (Array.isArray(newScores) && newScores.length === 4) {
        setScores(newScores); 

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
        
          
      } else {
        console.error('Invalid scores:', newScores);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  }, [setScores, setGameState]); 
  

  /**
 * This is a useeffect function that will call the fetchScores 
 * function when the gamestate has been set to "Scoring"
 */
  useEffect(() => {
    
    if(gameState === 'Scoring'){
      fetchScores();
    }
  }, [gameState, fetchScores]);



  /**
 * This displays all of the scores and shows a button 
 * to start a new round if the round is finished
 */
  return (
    <div >
      {roundFinished && (
        <button className = "Round-Button" onClick={startNewRound}>Start Next Round</button>
      )}
      <p className = "Score">   Right: {scores[3]}</p>
      <p className = "Score">   Top: {scores[2]}</p>
      <p className = "Score">   Left: {scores[1]}</p>
      <p className = "Score">   You: {scores[0]}</p>
    </div>
  );
}

export default ScoreBoard;