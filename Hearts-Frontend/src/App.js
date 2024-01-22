

import Header from './Header';
import Content from './Content';
import Footer from './Footer'

function App() {
  
 
  
  return (
    <div className="App">
      <Header />
      <Content />
      <Footer/>
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
