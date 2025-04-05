import React from "react";
import styles from "./ImageSection.module.css";
import ScoreCircle from "./ScoreCircle";

const ImageSection = ({ predPrice, price, score }) => {
    var demoPrice = ((predPrice / price) * 10).toFixed(2);
    return <div className={styles.imageSection}>
        <div className={styles.circlesContainer}>
            <ScoreCircle text1="Score" score={score} outoften={true} />
            <ScoreCircle text1="Fair Price" score={demoPrice} outoften={false} text2={predPrice} />
        </div>
    </div>
};

export default ImageSection;
