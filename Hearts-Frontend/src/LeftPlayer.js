import './index.css';


const LeftPlayer = () => {
  
  return (
    <div>
      {Array(13).fill().map((_, i) => (
        <div key={i} className="side-card" style={{ zIndex: i }}></div>
      ))}
    </div>
  );
}

export default LeftPlayer;