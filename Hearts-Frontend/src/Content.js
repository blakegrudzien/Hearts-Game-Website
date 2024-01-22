import React from 'react';
import { useState } from 'react';
import { FaTrashAlt } from 'react-icons/fa';
import cardImage from './images/c01.jpg'
import './index.css';

const Content = () => {
  const [items, setItems] = useState([
    {
      id: 1,
      checked: false,
      item:"One half pound bad of Cocoa Covered Almonds Unsalted"
    },
    {
      id: 2,
      checked: false,
      item:"item 2"
    },
    {
      id: 3,
      checked: false,
      item:"item 3"
    }
  ]);

  const handleCheckChange = (index) => {
    const newItems = [...items];
    newItems[index].checked = !newItems[index].checked;
    setItems(newItems);
  };

  return (
    <div>
      <img src={cardImage} alt="Card" className="cardImage" />
      {items.map((item, index) => (
        <input
          type="checkbox"
          checked={item.checked}
          onChange={() => handleCheckChange(index)}
        />
      ))}
    </div>
  );
}

export default Content;