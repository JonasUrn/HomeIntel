import React from "react";
import styles from "./ResultsPage.module.css";
import Navbar from "./NavBar";
import ImageSection from "./ImageSection";
import Grid from "./Grid";
import BottomSection from "./BottomSection";

const ResultsPage = () => {
    const placeholderTexts = Array(10).fill("Placeholder");

    return (
        <div className={styles.container}>
            <Navbar />
            <ImageSection />
            <Grid texts={placeholderTexts} />
            <BottomSection />
        </div>
    );
};

export default ResultsPage;