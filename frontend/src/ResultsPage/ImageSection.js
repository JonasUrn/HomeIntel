import React from "react";
import styles from "./ImageSection.module.css";
import ScoreCircle from "./ScoreCircle";

const ImageSection = () => {
    var demoPrice = ((165000 / 170000) * 10).toFixed(2);
    console.log(demoPrice);
    return <div className={styles.imageSection}>
        <div className={styles.circlesContainer}>
            <ScoreCircle text1="Score" score={8.5} outoften={true} />
            <ScoreCircle text1="Fair Price" score={demoPrice} outoften={false} />
        </div>
    </div>
};

export default ImageSection;
