import React, { useState, useEffect } from 'react';

const PlayedCards = ({ gameState, turn , setTurn}) => {
  const [cardUrls, setCardUrls] = useState([]);

  const playCard = async () => {
    console.log("Playing card...");
    return fetch('http://localhost:8080/playCard', { method: 'POST' })
      .catch(error => console.error('Error:', error));  // Handle any errors
  };

  const printTrick = async () => {
    try {
      const response = await fetch('http://localhost:8080/getTrick');
      const trick = await response.json();  // Parse the response as JSON
      console.log("Trick:", trick);
      setCardUrls(trick);  // Store the trick in the cardUrls state
    } catch (error) {
      console.error('Error:', error);
    }
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
    const playAndPrint = async () => {
      if (gameState === "Play" && turn !== 1) {
        await playCard();
        printTrick();
      }
      fetchTurn();
    };
  
    playAndPrint();
  }, [turn]);

  console.log('PlayedCards is re-rendering');
  

  return (
    <div className="played-cards" style={{ display: 'flex', justifyContent: 'center' }}>
      {cardUrls.map((url, index) => (
        <img key={index} src={url} alt="Card back" className="Player-card" style={{ zIndex: index + 1 }} />
      ))}
    </div>
  );
};


export default PlayedCards;

























