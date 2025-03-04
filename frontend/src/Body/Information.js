import React from "react";

import styles from "./information.module.css";

import cardData from "./cardsData";

import Card from "./Card";

const Information = () => {
    return (
        <div className={styles.container}>
            {cardData.map((card, index) => (
                <Card key={index} {...card} />
            ))}
        </div>
    );
};

export default Information;