import './index.css';


const BottomPlayer = ({ imageURLs }) => {
  
  
  return (
    <div className="bottom-player">
      {imageURLs.map((url, index) => (
        <button key={index} onClick={() => console.log(`Card ${index + 1} clicked`)}>
          <img src={url} alt="Card" className="Player-card" style={{ zIndex: index }} />
        </button>
      ))}
    </div>
    
  );
  }


export default BottomPlayer;