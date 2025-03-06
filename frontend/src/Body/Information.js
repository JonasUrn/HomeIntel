import React from "react";
import styles from "./information.module.css";
import Card from "./Card.js";

import SectionTitle from "./SectionTitle.js";

const Information = ({ cardsData, title, subtitle }) => {
    return (
        <div className={styles.container}>
            <SectionTitle title={title} subtitle={subtitle} />

            <div className={styles.row}>
                <div className={styles.columnLarge}>
                    <Card {...cardsData[0]} />
                </div>
                <div className={styles.columnSmall}>
                    <img src={cardsData[0].image} alt="image1" className={styles.image} />
                </div>
            </div>

            <div className={styles.row}>
                <div className={styles.columnSmall}>
                    <img src={cardsData[1].image} alt="image2" className={styles.image} />
                </div>
                <div className={styles.columnLarge}>
                    <Card {...cardsData[1]} align="right" />
                </div>
            </div>

            <div className={styles.row}>
                <div className={styles.columnLarge}>
                    <Card {...cardsData[2]} />
                </div>
                <div className={styles.columnSmall}>
                    <img src={cardsData[2].image} alt="image3" className={styles.image} />
                </div>
            </div>

        </div>
    );
};

export default Information;
