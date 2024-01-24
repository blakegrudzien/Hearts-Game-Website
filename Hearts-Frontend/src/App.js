import React from 'react';
import Header from './Header';
import Content from './Content';
import Footer from './Footer';

function App() {
  const handleClick = () => {
    fetch('/')
  .then(response => response.text())
  .then(text => console.log(text))
  .catch(error => console.error(error));
  };

  return (
    <div className="App">
      <Header />
      <Content />
      <Footer />
      <button id="myButton" onClick={handleClick}>Click me!</button>
    </div>
  );
}

/*startNewGame() {
  fetch('http://localhost:8080/startGame')
    .then(response => response.json())
    .then(game => {
      // Update the state with the new game state
      this.setState({ game });
    })
    .catch(error => {
      console.error('Error:', error);
    });
}*/


export default App;
