/**
 * This function deals with the cards that have been played in 
 * the trick, displaying and tracking them, it is also where 
 * the computer makes its decisions for the card to play
 */

import React, { useState, useEffect } from 'react';
import API_URL from './config';

const PlayedCards = ({ setGameState, gameState, turn , setTurn}) => {
  const [cardUrls, setCardUrls] = useState([]);
  


  /**
 * This function calls the backend function playCard, which plays a card and updates the trick
 */

  const playCard = async () => {
    return fetch(`${API_URL}/playCard`, { method: 'POST' })
      .catch(error => console.error('Error:', error));  
  };

 
  /**
 * This function calls the backend function clearTrick, 
 * which clears the trick and updates the gamestate, 
 * the gamestate is then updated here and the updated trick is printed
 */

  const clearTrick = async () => {
    
    try {
      const response = await fetch(`${API_URL}/clearTrick`, { method: 'POST' });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      await setGameState('Scoring');  
    } catch (error) {
      console.error('Error:', error);
    }
    printTrick();
  };



  /**
 * This function gets the turn from the backend and updates the turn state
 */
  const fetchTurn = async () => {
    try {
      const response = await fetch(`${API_URL}/getturn`);
      const newTurn = await response.json();
      setTurn(newTurn);
    } catch (error) {
      console.error('Error:', error);
    }
  };


  /**
 * This function has the computer play a card if it is not the user's 
 * turn, by calling the playCard function and then printing the trick
 */
  useEffect(() => {
    console.log("Turn: " + turn + " GameState: " + gameState);
    const playAndPrint = async () => {
      if (gameState === "Play" && turn !== 1) {
        await playCard();
        console.log("just played card with Turn: " + turn + " GameState: " + gameState);

        printTrick();
      }
      fetchTurn();
    };
  
    playAndPrint();
  }, [turn , gameState]);



  /**
 * This function prints the trick, if the trick is full it will set the gamestate to "Clearing", the first step to reset it
 */
  const printTrick = async () => {
    try {
      const response = await fetch(`${API_URL}/getTrick`);
      const trick = await response.json();  // Parse the response as JSON
      
      setCardUrls(trick);  // Store the trick in the cardUrls state

      console.log("Trick length:" + trick.length);
   

      if(trick.length === 4){
        
        await setGameState("Clearing");
        
      }

    } catch (error) {
      console.error('Error:', error);
    }
  };

 
  
/**
 * This loads the cards in the trick, as well as the button to clear the trick
 */
  return (
    <div  style={{ display: 'flex', justifyContent: 'center' }}>
      {cardUrls.map((url, index) => (
        url && <img key={index} src={url} alt="Card back" className="played-cards" style={{ zIndex: index + 1 }} />
      ))}
      { (
        <button className = "Clear-Trick" onClick={clearTrick}>Clear Trick</button>
      )}
    </div>
  );
};


export default PlayedCards;

























