 /**
 * This component deals with printing the left player's hand
 */



import './index.css';
import React, { useState, useEffect } from 'react';

const LeftPlayer = () => {
  const [cardCount, setCardCount] = useState(0);




   /**
 * This function is called when the component is first called and it displays 13 cards for the left player
 */
  useEffect(() => {
    const getComputerHand = async () => {
      const response = await fetch('http://localhost:8080/getComputerHand?playerName=p3', { method: 'GET' });
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

export default LeftPlayer;