import React from 'react';
import {Body, Footer, Header} from "../imports/importFiles.js";
import styles from "./allComponents.module.css";

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