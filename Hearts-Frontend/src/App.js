import './index.css';
import Header from './Header';
import Content from './Content';
import Footer from './Footer';
import TopPlayer from './TopPlayer';
import { useState } from 'react';
import RightPlayer from './RightPlayer';
import LeftPlayer from './LeftPlayer';
import PlayedCards from './PlayedCards';
import BottomPlayer from './BottomPlayer';

function App() {
  const [imageURLs, setImageUrls] = useState([]); // Add this line
  

  const handleClick = () => {
    fetch('/')
      .then(response => response.json())
      .then(data => console.log(data))
      .catch(error => console.error("Error:", error));
  };

  const startNewGame = () => {
    fetch('http://localhost:8080/startGame', { method: 'POST' })
      .then(response => response.json())
      .then(data => {
        // Update the state with the new game state
        setImageUrls(data);
      })
      .catch(error => {
        console.error('Error:', error);
      });
  };

  return (
    <div className="App">
      <Header />
      <TopPlayer/>
      
    <div className="middle-row">
      <LeftPlayer/>
      <PlayedCards/>  
      <RightPlayer/>
    </div>
    <button onClick={startNewGame}>Start New Game</button>
    <BottomPlayer imageURLs={imageURLs} />
      
      <Content />
      <Footer />
      
   
    </div>
  );
}

export default App;