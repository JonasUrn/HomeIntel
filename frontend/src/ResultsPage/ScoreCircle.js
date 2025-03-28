import React from "react";
import styles from "./ScoreCircle.module.css";

const ScoreCircle = ({ text1, score, outoften }) => {
    // Determine the color and size of the outline based on the score
    const outlineColor = getOutlineColor(score);
    const outlineSize = getOutlineSize(score);

    return (
        <div className={styles.circleWrapper}>
            <div
                className={styles.outline}
                style={{
                    borderColor: outlineColor,
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

// Function to determine the outline color based on score
const getOutlineColor = (score) => {
    if (score <= 2) return "#800000"; // Red-Purple for very low score
    if (score <= 4) return "#FF0000"; // Red for low score
    if (score <= 6) return "#FF8C00"; // Orange for average score
    if (score <= 8) return "#FFCC00"; // Yellow for good score
    return "#FF6347"; // Light Red for excellent score
};

// Function to determine the outline size based on score
const getOutlineSize = (score) => {
    return 90 + score * 10; // The outline size increases with the score (size of black circle + 10px)
};

export default ScoreCircle;
