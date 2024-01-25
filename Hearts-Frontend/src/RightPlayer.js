import './index.css';


const RightPlayer = () => {
  
  return (
    <div className>
      {Array(13).fill().map((_, i) => (
        <div key={i} className="side-card" style={{ zIndex: i }}></div>
      ))}
    </div>
  );
}

export default RightPlayer;