import React from "react";
import styles from "./body.module.css";
import Information from "./Information";

const Body = () => {
    return <div className={styles.body}>
        <Information />
    </div>
}

export default Body;