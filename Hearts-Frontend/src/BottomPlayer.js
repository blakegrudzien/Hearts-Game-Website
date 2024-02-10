import './index.css';
import { useState, useEffect } from 'react';

const BottomPlayer = ({ gameState, setGameState, turn, setTurn, triggerApp }) => {
  const [selectedCards, setSelectedCards] = useState([]);
  const [showSwapButton, setShowSwapButton] = useState(false);
  const [imageUrls, setImageUrls] = useState([]);

  const [hand, setHand] = useState([]);
  const [valid, setValid] = useState([]);
  const [renderKey, setRenderKey] = useState(Math.random());
  const [roundNumber, setRoundNumber] = useState(1);
  


  
  /**
 * This function is called whenever the game is started, and it fetches the gamestate, the player's hand and the round number
 */
  useEffect(() => {
    const startNewGame = async () => {
      try {
       /*let response = await fetch('http://localhost:8080/startGame', { method: 'POST' });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        } */

      let response = await fetch('http://localhost:8080/getGameState', { method: 'GET' });
      let gameState = await response.text();
      setGameState(gameState);


      response = await fetch('http://localhost:8080/getPlayerHand', { method: 'GET' });
      let handData = await response.text();
      setImageUrls(JSON.parse(handData)); 
      } catch (error) {
        console.error('Error:', error);
      }
   };

    startNewGame();
  }, []);


  /**
 * This function gets the gamestate from the backend and changes it in the frontend
 */
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



  /**
 * This function gets the turn from the backend and changes it in the frontend
 */
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
          triggerApp();
        }
      }
      await fetchTurn();
    };

    fetchAndCheckGameState();
  }, [turn, gameState]);




  /**
 * This function calls the playCard function in the backend
 */
  const playCard = async () => {
    try {
      const response = await fetch('http://localhost:8080/playCard', {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      if (data) {
        return data;
      } else {
        console.error('Data is undefined');
      }
    } catch (error) {
      console.error('Error:', error);
    }
  }



/**
 * This function calls the player_plays function in the backend which lets the player play a card 
 * that was deemed valid by the valid array returned from the playcard function
 */
  const player_plays = async (index) => {
    try {
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

      printTrick();
      fetchTurn();

    const response2 = await fetch('http://localhost:8080/getPlayerHand', { method: 'GET' });
    let handData = await response2.text();
    await setValid(Array(JSON.parse(handData).length).fill(false));
    await setImageUrls(JSON.parse(handData)); // replace with actual data property
 
    } catch (error) {
      console.error('Error:', error);
    }
  };



  
/**
 * This function gets the trick from the backend and prints it to the console
 */
  const printTrick = async () => {
    try {
      const response = await fetch('http://localhost:8080/getTrick');
      const trick = await response.json();  
    } catch (error) {
      console.error('Error:', error);
    }
  };





  /**
 * This function sends the 3 selected cards to the backend to then swap, 
 * this function then prints the new cards and starts the playing phase
 */

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

      const handResponse = await fetch('http://localhost:8080/getPlayerHand');
    const handData = await handResponse.json();
    setImageUrls(handData); 


    const gameStateResponse = await fetch('http://localhost:8080/getGameState');
    const gameStateData = await gameStateResponse.text();
    setGameState(gameStateData); 

  } catch (error) {
    console.error('Error:', error);
  }

    setValid(Array(imageUrls.length).fill(true));
    setSelectedCards([]);
    setShowSwapButton(false);
  };

  
 /**
 * This is the same as the function above, but for the cacse where there is no swapping (every 4th round) the 
 * function automatically sends three impossible cards so the backend function knows to skip the swapping
 */
  const handleSkipSwap = async () => {
    try {
      const response = await fetch('http://localhost:8080/performSwap', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify([-1, -1, -1]),  
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const handResponse = await fetch('http://localhost:8080/getPlayerHand');
      const handData = await handResponse.json();
      setImageUrls(handData); 
    

      const gameStateResponse = await fetch('http://localhost:8080/getGameState');
      const gameStateData = await gameStateResponse.text();
      setGameState(gameStateData);
    } catch (error) {
      console.error('Error:', error);
    }
    setValid(Array(imageUrls.length).fill(true));
    setSelectedCards([]);
    setShowSwapButton(false);
  };
  

   /**
 * This function is called whenever the player selects a card, and it adds the card to the selected cards array
 */
  const handleCardSelection = (index) => {
   
    if (selectedCards.includes(index)) {
      setSelectedCards(selectedCards.filter(cardIndex => cardIndex !== index));
    } else {
 
      if (selectedCards.length < 3) {
        setSelectedCards([...selectedCards, index]);
      }
    }
  };


   /**
 * This function is called whenever the gamestate changes, and it fetches the 
 * player's hand and the round number and prints the users cards
 */

  useEffect(() => {
    const fetchHandData = async () => {
      if(gameState === 'Swap'){
        
        let roundResponse = await fetch('http://localhost:8080/getRoundNumber', { method: 'GET' });
        if (roundResponse.ok) {
          
          let roundNum = await roundResponse.text();
          setRoundNumber(parseInt(roundNum));
  

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

  
/**
 * Returns the Cards of the player and the swap button if the game state is swap
 */


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
          {roundNumber % 4 === 1 ? 'Pass Cards Left' : 
           roundNumber % 4 === 2 ? 'Pass Cards Right' : 
           roundNumber % 4 === 3 ? 'Pass Cards Across' : 'Perform Swap'}
        </button>
        )}
        {gameState === 'Swap' && roundNumber % 4 === 0 && (
    <button onClick={handleSkipSwap} className="Swap-Button">
    No Swap
    </button>
)}
      </>
    </div>
  );
  };

export default BottomPlayer;