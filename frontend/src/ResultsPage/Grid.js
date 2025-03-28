import React from "react";
import styles from "./Grid.module.css";

const Grid = ({ texts }) => (
    <div className={styles.grid}>
        {texts.map((text, index) => (
            <div key={index} className={styles.gridItem}>{text}</div>
        ))}
    </div>
);

export default Grid;