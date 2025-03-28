import React from 'react';
import Body from './Body/Body.js';
import Footer from './Footer/Footer.js';
import Header from './Header/header.js';
import styles from "./app.css";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

const LandingPage = () => {
  return (
    <div className={styles.app}>
      <Header />
      <Body />
      <Footer />
    </div>
  );
}

const ResultsPage = () => {
  return <h1>ResultsPage</h1>
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/contact" element={<ResultsPage />} />
      </Routes>
    </Router>
  );
}

export default App;