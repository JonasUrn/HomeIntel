import React from "react";
import styles from "./information.module.css";
import {cardsData, Card} from "../imports/importFiles.js";

const Information = () => {
    return (
        <div className={styles.container}>
            {cardsData.map((card, index) => (
                <Card key={index} {...card} />
            ))}
        </div>
    );
};

export default Information;