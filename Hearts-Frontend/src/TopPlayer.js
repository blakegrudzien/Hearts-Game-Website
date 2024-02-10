 /**
 * This component deals with printing the top player's hand
 */


import React, { useState, useEffect } from 'react';
import './index.css';
import API_URL from './config';




const TopPlayer = () => {
  const [cardCount, setCardCount] = useState(0);


   /**
 * This function is called when the component is first called and it displays 13 cards for the top player
 */
  useEffect(() => {
    const getComputerHand = async () => {
      const response = await fetch(`${API_URL}/getComputerHand?playerName=p3`, { method: 'GET' });
      const cardCount = await response.text();
      setCardCount(parseInt(cardCount, 10));      
    };

    getComputerHand();
  }, []);

  return (
    <div className="topHand">
      {Array(cardCount).fill().map((_, i) => (
        <img key={i} src="/Backs/Card-Back-04.jpg" alt="Card back" className="top-card" style={{ zIndex: 13-i }} />
      ))}
    </div>
  );
}

export default TopPlayer;