import './index.css';


const PlayedCards = () => {
  
  return (
    <div className>
      {Array(4).fill().map((_, i) => (
        <div key={i} className="top-card" style={{ zIndex: i }}></div>
      ))}
    </div>
  );
}

export default PlayedCards;