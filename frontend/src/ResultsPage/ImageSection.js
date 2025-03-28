import React from "react";
import styles from "./ImageSection.module.css";
import ScoreCircle from "./ScoreCircle";

const ImageSection = () => (
    <div className={styles.imageSection}>
        <div className={styles.circlesContainer}>
            <ScoreCircle text1="Title 1" score={8.5} outoften={false} />
            <ScoreCircle text1="Title 2" score={6.2} outoften={true} />
        </div>
    </div>
);

export default ImageSection;
