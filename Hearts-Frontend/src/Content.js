import React from 'react';
import { useState } from 'react';
import { FaTrashAlt } from 'react-icons/fa';
import cardImage from './images/c01.jpg'
import './index.css';

const Content = () => {
  
 
  return (
    <div>
      <img src={cardImage} alt="Card" className="cardImage" />
     
    </div>
  );
}

export default Content;