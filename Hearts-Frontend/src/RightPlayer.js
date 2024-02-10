 /**
 * This function deals with printing the right player's hand
 */


import './index.css';
import API_URL from '../config';
import React, { useState, useEffect } from 'react';

const RightPlayer = () => {
  const [cardCount, setCardCount] = useState(0);



   /**
 * This function prints out 13 cards for the right player upon the component being called
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
    <div className="sideHand">
      {Array(cardCount).fill().map((_, i) => (
        <img key={i} src="/Backs/Card-Back-04.jpg" alt="Card back" className="side-card" style={{ zIndex: 13-i }} />
      ))}
    </div>
  );
}

export default RightPlayer;