import React from "react";
import styles from "./information.module.css";
import cardsData from "./cardsData.js";
import Card from "./Card.js";

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