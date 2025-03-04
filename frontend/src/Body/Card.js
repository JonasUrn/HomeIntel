import React from "react";
import styles from "./card.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faKeyboard, faSlidersH, faRobot, faDollarSign, faStar, faLightbulb } from "@fortawesome/free-solid-svg-icons";

const iconMap = {
    "fa-solid fa-keyboard": faKeyboard,
    "fa-solid fa-sliders-h": faSlidersH,
    "fa-solid fa-robot": faRobot,
    "fa-solid fa-dollar-sign": faDollarSign,
    "fa-solid fa-star": faStar,
    "fa-solid fa-lightbulb": faLightbulb
};

const Card = ({ title, text, icon, backText }) => {
    return (
        <div className={styles.card}>
            <div className={styles.cardInner}>
                <div className={styles.cardFront}>
                    <div className={styles.cardLeft}>
                        <h3>{title}</h3>
                        <p>{text}</p>
                    </div>
                    <div className={styles.cardRight}>
                        <FontAwesomeIcon icon={iconMap[icon]} className={styles.icon} />
                    </div>
                </div>
                <div className={styles.cardBack}>
                    <p>{backText}</p>
                </div>
            </div>
        </div>
    );
};

export default Card;
