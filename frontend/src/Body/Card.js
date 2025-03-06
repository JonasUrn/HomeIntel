import React from "react";
import styles from "./card.module.css";

const Card = ({ title, text, backText, align }) => {
    return (
        <div className={styles.card}>
            <div className={styles.cardInner} style={align == "right" ? { marginLeft: "auto", marginRight: 0 } : {}}>
                <div className={styles.cardFront}>
                    <h3>{title}</h3>
                    <p>{text}</p>
                </div>
                <div className={styles.cardBack}>
                    <p>{backText}</p>
                </div>
            </div>
        </div>
    );
};

export default Card;
