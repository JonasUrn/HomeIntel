import React from 'react';
import Body from './Body/Body.js';
import Footer from './Footer/Footer.js';
import Header from './Header/header.js';
import styles from "./app.css";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ResultsPage from './ResultsPage/ResultsPage.js';

const LandingPage = () => {
  return (
    <div className={styles.app}>
      <Header />
      <Body />
      <Footer />
    </div>
  );
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/results" element={<ResultsPage />} />
      </Routes>
    </Router>
  );
}

export default App;