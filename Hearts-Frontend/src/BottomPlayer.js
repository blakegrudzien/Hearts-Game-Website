import './index.css';
import PlayedCards from './PlayedCards';
import { useState, useEffect } from 'react';

const BottomPlayer = ({ gameState, setGameState, turn, setTurn, triggerApp }) => {
  const [selectedCards, setSelectedCards] = useState([]);
  const [showSwapButton, setShowSwapButton] = useState(false);
  const [imageUrls, setImageUrls] = useState([]);

  const [hand, setHand] = useState([]);
  const [valid, setValid] = useState([]);
  const [renderKey, setRenderKey] = useState(Math.random());
  const [roundNumber, setRoundNumber] = useState(1);
  


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
    
    if (gameState === 'Play') {
     
      if (turn === 1) {
        playCard().then(validArray => {
          if (validArray) {
            setValid(validArray);
          }
        });
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
    console.log("trying to get valid array");
    console.log("playCard function called from bottomplayer");
    const response = await fetch('http://localhost:8080/playCard', {
      method: 'POST',
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log("just got valid array");
    console.log("Response data: ", data);
    console.log("Valid array length: ", data.length);

    if (data) {
      return data;
    } else {
      console.error('Data is undefined');
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

useEffect(() => {
  console.log("useEffect ran");
  console.log("Valid array: ", valid);
}, [valid]);


const player_plays = async (index) => {
  console.log('player_plays called');
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
    console.log("before printing trick" + imageUrls);

    printTrick();
    fetchTurn();


    console.log("ImageUrls before calling  hand: " + imageUrls);

  const response2 = await fetch('http://localhost:8080/getPlayerHand', { method: 'GET' });
  let handData = await response2.text();
  await setValid(Array(JSON.parse(handData).length).fill(false));
  await setImageUrls(JSON.parse(handData)); // replace with actual data property
  console.out("ImageUrls after playing: " + imageUrls);

    
  } catch (error) {
    console.error('Error:', error);
  }

};


useEffect(() => {
  console.log("ImageUrls after playing: ", imageUrls);
}, [imageUrls]);

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


      response = await fetch('http://localhost:8080/getPlayerHand', { method: 'GET' });
      let handData = await response.text();
      setImageUrls(JSON.parse(handData)); // replace with actual data property
      } catch (error) {
        console.error('Error:', error);
      }
    };

    startNewGame();
  }, []);


  const printTrick = async () => {
    try {
      const response = await fetch('http://localhost:8080/getTrick');
      const trick = await response.json();  // Parse the response as JSON
      console.log("Trick:", trick);
     // setCardUrls(trick);  // Store the trick in the cardUrls state
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleSkipSwap = async () => {
    try {
      const response = await fetch('http://localhost:8080/performSwap', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify([-1, -1, -1]),  // Send [-1, -1, -1] to the backend
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
    setValid(Array(imageUrls.length).fill(true));
    setSelectedCards([]);
    setShowSwapButton(false);
  };




  

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
   
    if (selectedCards.includes(index)) {
      setSelectedCards(selectedCards.filter(cardIndex => cardIndex !== index));
    } else {
 
      if (selectedCards.length < 3) {
        setSelectedCards([...selectedCards, index]);
      }
    }
  };

  useEffect(() => {
    const fetchHandData = async () => {
      if(gameState === 'Swap'){
        
        let roundResponse = await fetch('http://localhost:8080/getRoundNumber', { method: 'GET' });
        if (roundResponse.ok) {
          
          let roundNum = await roundResponse.text();
          setRoundNumber(parseInt(roundNum));
          console.log("Round number = " + roundNumber);
  

            const handResponse = await fetch('http://localhost:8080/getPlayerHand');
          
            if (handResponse.ok) {
              const handData = await handResponse.json();
              setImageUrls(handData); // replace with actual data property
              
            } else {
              console.error('Error:', handResponse.status, handResponse.statusText);
            }
          
        } else {
          console.error('Error:', roundResponse.status, roundResponse.statusText);
        }
      }
    };
  
    fetchHandData();
  }, [gameState]);

  
  console.log("BottomPlayer is rendering");
  console.log("Valid is:", valid);
  return (
    <div className="bottom-player">
      <>
        {imageUrls.map((imageUrl, index) => (
          imageUrl && (
            <button 
              key={index}
              disabled={gameState === 'Play' && !valid[index]}
              onClick={() => {
                if (gameState === 'Swap' && roundNumber % 4 !== 0) {
                  handleCardSelection(index);
                } else if (gameState === 'Play' && valid[index]) {
                  player_plays(index);
                }
              }}
            >
              <img src={imageUrl} className={`Player-card ${selectedCards.includes(index) ? 'selected' : ''}`}/>
            </button>
          )
        ))}
        {gameState === 'Swap' && roundNumber % 4 !== 0 &&(
          <button onClick={handleSwap} className = "Swap-Button" disabled={selectedCards.length !== 3} >
            Perform Swap
          </button>
        )}
        {gameState === 'Swap' && roundNumber % 4 === 0 && (
    <button onClick={handleSkipSwap} className="Swap-Button">
    SkipSwap
    </button>
)}
      </>
    </div>
  );
  };

export default BottomPlayer;