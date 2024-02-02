import React, { useState, useEffect } from 'react';

const PlayedCards = ({ setGameState, gameState, turn , setTurn}) => {
  const [cardUrls, setCardUrls] = useState([]);
  const [showButton, setShowButton] = useState(false);

  const playCard = async () => {
    console.log("Playing card...");
    return fetch('http://localhost:8080/playCard', { method: 'POST' })
      .catch(error => console.error('Error:', error));  // Handle any errors
  };

 
  const clearTrick = async () => {
    console.log("Clearing trick...");
    try {
      const response = await fetch('http://localhost:8080/clearTrick', { method: 'POST' });
  
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
  
      // After clearing the trick, update the gameState
      await setGameState('Scoring');  // Replace 'newGameState' with the actual new gameState
    } catch (error) {
      console.error('Error:', error);
    }
    printTrick();
  };

  const fetchTurn = async () => {
    try {
      const response = await fetch('http://localhost:8080/getturn');
      const newTurn = await response.json();
      setTurn(newTurn);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  useEffect(() => {
    console.log("UseEffect in PlayedCards is called");
    const playAndPrint = async () => {
      if (gameState === "Play" && turn !== 1) {
        await playCard();
        printTrick();
      }
      fetchTurn();
    };
  
    playAndPrint();
  }, [turn , gameState]);




  const printTrick = async () => {
    try {
      const response = await fetch('http://localhost:8080/getTrick');
      const trick = await response.json();  // Parse the response as JSON
      console.log("Trick:", trick);
      setCardUrls(trick);  // Store the trick in the cardUrls state

      if(trick.length === 4){
        console.log("Trick is full, clearing trick");
        await setGameState("Clearing");
        setShowButton(true);
      } else {
        setShowButton(false);
      }

    } catch (error) {
      console.error('Error:', error);
    }
  };

  console.log('PlayedCards is re-rendering');
  

  return (
    <div  style={{ display: 'flex', justifyContent: 'center' }}>
      {cardUrls.map((url, index) => (
        url && <img key={index} src={url} alt="Card back" className="played-cards" style={{ zIndex: index + 1 }} />
      ))}
      {showButton && (
        <button className = "Clear-Trick" onClick={clearTrick}>Clear Trick</button>
      )}
    </div>
  );
};


export default PlayedCards;

























