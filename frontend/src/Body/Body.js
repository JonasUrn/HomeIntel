import React from "react";

import styles from "./body.module.css";

import Information from "./Information";
import TopPart from "./TopPart";

const Body = () => {
    return <div className={styles.body}>
        <TopPart />
        <Information />
    </div>
}

export default Body;