import React from "react";

import styles from "./body.module.css";

import Information from "./Information";
import Header from "../header/header.js";

const Body = () => {
    return <div className={styles.body}>
        <Header />
        <Information />
    </div>
}

export default Body;