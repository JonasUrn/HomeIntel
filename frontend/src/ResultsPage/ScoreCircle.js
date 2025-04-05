import React from "react";
import styles from "./ScoreCircle.module.css";

const ScoreCircle = ({ text1, score, outoften, text2 }) => {
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
                    {outoften ? `${score} / 10` : text2}
                </span>
            </div>
        </div>
    );
};

const getOutlineGradient = (score) => {
    if (score <= 3) {
        return "#FF0000";
    }
    if (score <= 7) {
        return "rgb(190, 211, 4)";
    }
    return "rgb(0, 255, 64)";
};

export default ScoreCircle;
