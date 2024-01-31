import './index.css';
import PlayedCards from './PlayedCards';
import { useState, useEffect } from 'react';

const BottomPlayer = ({ gameState, setGameState, turn, setTurn, triggerApp }) => {
  const [selectedCards, setSelectedCards] = useState([]);
  const [showSwapButton, setShowSwapButton] = useState(false);
  const [imageUrls, setImageUrls] = useState([]);

  const [hand, setHand] = useState([]);
  const [valid, setValid] = useState(Array(13).fill(true));
  const [renderKey, setRenderKey] = useState(Math.random());
  




  console.log('Bottom Player called'); // Log the value of gameState before setting it

  useEffect(() => {
    console.log('gameState after setting:', gameState); // Log the value of gameState after setting it
  }, [gameState]); // This hook runs whenever gameState changes


  const fetchGameState = async () => {
    try {
      const response = await fetch('http://localhost:8080/getGameState', { method: 'GET' });
      const gameState = await response.text();
      setGameState(gameState); // replace with actual data property
      return gameState;
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const fetchTurn = async () => {
    try {
      const response = await fetch('http://localhost:8080/getturn', { method: 'GET' });
      const turnData = await response.json();
      setTurn(turnData); // Update the turn state with the new data
      return turnData;
    } catch (error) {
      console.error('Error:', error);
    }
  };

useEffect(() => {
  console.log("Turn in Bottom Player is: " + turn);
  console.log("Gamestate in Bottom Player is: " + gameState);
  
  
  const fetchAndCheckGameState = async () => {
    await fetchTurn();
    await fetchGameState();
    console.log("Gamestate in fetchAndCheckGameState is: " + gameState);
    console.log("Turn in fetchAndCheckGameState is: " + turn);
    if (gameState === 'Play') {
      console.log("Play state was passed");
      if (turn === 1) {
        const validArray = await playCard();
        setValid(validArray);
      } else {
        console.log("attempt to reload playedcards   " + gameState);
        triggerApp();
      }
    }
    await fetchTurn();
  };

  fetchAndCheckGameState();
}, [turn, gameState]);



const playCard = async () => {
  try {
    // Call the play_card function in the backend

    console.log("playCard function called from bottomplayer");
    const response = await fetch('http://localhost:8080/playCard', {
      method: 'POST',
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // Parse the response as JSON
    const data = await response.json();
    setValid(data.validArray); // Update the valid state with the new data
   
    // Return the boolean array from the response
    
  } catch (error) {
    console.error('Error:', error);
  }
};


const player_plays = async (index) => {
  console.count('player_plays called')
  try {
    // Call the Player_plays function in the backend
    const response = await fetch('http://localhost:8080/player_plays', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ index }),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // No need to parse the response as JSON or handle the response data
    // since the Player_plays function doesn't return anything
  } catch (error) {
    console.error('Error:', error);
  }
};

  useEffect(() => {
    const startNewGame = async () => {
      try {
        let response = await fetch('http://localhost:8080/startGame', { method: 'POST' });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        // After starting a new game, get the current game state
      response = await fetch('http://localhost:8080/getGameState', { method: 'GET' });
      let gameState = await response.text();
      setGameState(gameState); // replace with actual data property

// Fetch the player's hand
      response = await fetch('http://localhost:8080/getPlayerHand', { method: 'GET' });
      let handData = await response.text();
      setImageUrls(JSON.parse(handData)); // replace with actual data property
      } catch (error) {
        console.error('Error:', error);
      }
    };

    startNewGame();
  }, []);



  const handleSwap = async () => {
    try {
      const response = await fetch('http://localhost:8080/performSwap', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(selectedCards),
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
       // After the swap operation is done, get the updated player's hand
      const handResponse = await fetch('http://localhost:8080/getPlayerHand');
    const handData = await handResponse.json();
    setImageUrls(handData); // replace with actual data property

    // Fetch the updated game state
    const gameStateResponse = await fetch('http://localhost:8080/getGameState');
    const gameStateData = await gameStateResponse.text();
    setGameState(gameStateData); // replace with actual data property

  } catch (error) {
    console.error('Error:', error);
  }

    // Reset the selectedCards array and hide the swap button
    setValid(Array(imageUrls.length).fill(true));
    setSelectedCards([]);
    setShowSwapButton(false);
  };


  
  const handleCardSelection = (index) => {
    // If the card is already selected, deselect it
    if (selectedCards.includes(index)) {
      setSelectedCards(selectedCards.filter(cardIndex => cardIndex !== index));
    } else {
      // If less than 3 cards are selected, select the card
      if (selectedCards.length < 3) {
        setSelectedCards([...selectedCards, index]);
      }
    }
  };

  
  console.log("BottomPlayer is rendering");
  return (
    <div className="bottom-player">
      {turn !== 1 && gameState === 'Play' ? (
        <PlayedCards />
      ) : (
        <>
          {imageUrls.map((imageUrl, index) => (
            <button 
              key={index}
              onClick={() => {
                if (gameState === 'Swap') {
                  handleCardSelection(index);
                } else if (gameState === 'Play' && valid[index]) {
                  player_plays(index);
                }
              }}
            >
              <img src={imageUrl} className={`Player-card ${selectedCards.includes(index) ? 'selected' : ''}`}/>
            </button>
          ))}
          {gameState === 'Swap' && (
            <button onClick={handleSwap} disabled={selectedCards.length !== 3} >
              Perform Swap
            </button>
          )}
        </>
      )}
    </div>
  );
  };

export default BottomPlayer;