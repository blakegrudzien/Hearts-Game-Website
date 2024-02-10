// src/config.js

let API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
API_URL = API_URL.endsWith('/') ? API_URL.slice(0, -1) : API_URL;

export default API_URL;