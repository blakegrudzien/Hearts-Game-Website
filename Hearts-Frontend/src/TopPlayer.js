
import './index.css';


const TopPlayer = () => {
  
  return (
    <div className="topHand">
      {Array(13).fill().map((_, i) => (
        <div key={i} className="top-card" style={{ zIndex: i }}></div>
      ))}
    </div>
  );
}

export default TopPlayer;