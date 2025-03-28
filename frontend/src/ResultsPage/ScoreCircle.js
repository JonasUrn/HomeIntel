import React from "react";
import styles from "./ScoreCircle.module.css";

const ScoreCircle = ({ text1, score, outoften }) => {
    const fillPercentage = (score / 10) * 100;
    const outlineGradient = getOutlineGradient(score);

    return (
        <div className={styles.circleWrapper}>
            <div
                className={styles.outline}
                style={{
                    background: `conic-gradient(${outlineGradient} ${fillPercentage}%, transparent ${fillPercentage}% 100%)`,
                }}
            ></div>
            <div className={styles.blackCircle}>
                <span className={styles.title}>{text1}</span>
                <span className={styles.score}>
                    {outoften ? `${score} / 10` : score}
                </span>
            </div>
        </div>
    );
};

const getOutlineGradient = (score) => {
    if (score <= 2) {
        return "#800080, #FF0000";
    }
    if (score <= 4) {
        return "#800080, #FF0000, #FF8C00";
    }
    if (score <= 6) {
        return "#800080, #FF0000, #FF8C00, #FFFF00";
    }
    if (score <= 8) {
        return "#800080, #FF0000, #FF8C00, #FFFF00,rgb(174, 212, 3)";
    }
    return "#800080, #FF0000, #FF8C00, #FFFF00,rgb(98, 199, 4), rgb(0, 255, 64)";
};

export default ScoreCircle;
