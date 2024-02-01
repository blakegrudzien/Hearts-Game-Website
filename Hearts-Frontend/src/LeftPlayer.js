import './index.css';
import React, { useState, useEffect } from 'react';

const LeftPlayer = () => {
  const [cardCount, setCardCount] = useState(0);

  useEffect(() => {
    const getComputerHand = async () => {
      // Replace with your actual fetch call
      const response = await fetch('http://localhost:8080/getComputerHand?playerName=p3', { method: 'GET' });
      const cardCount = await response.text();
      setCardCount(parseInt(cardCount, 10));

      console.log(cardCount);
    };

    getComputerHand();
  }, []);


  console.log("Left Player called");
  return (
    <div className="sideHand">
      {Array(cardCount).fill().map((_, i) => (
        <img key={i} src="/Backs/Card-Back-04.jpg" alt="Card back" className="side-card" style={{ zIndex: 13-i }} />
      ))}
    </div>
  );
}

export default LeftPlayer;