import React from 'react';
import Body from './Body/Body.js';
import Footer from './Footer/Footer.js';
import Header from './Header/header.js';
import styles from "./app.css";

function App() {

  return (
    <div className={styles.app}>
      <Header />
      <Body />
      <Footer />
    </div>
  );
}

export default App;