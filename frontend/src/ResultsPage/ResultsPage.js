import React from "react";
import styles from "./ResultsPage.module.css";
import Navbar from "./NavBar";
import ImageSection from "./ImageSection";
import Grid from "./Grid";
import BottomSection from "./BottomSection";
import InputField from "./InputField";

const realEstateInfo = new Map([
    ["Address", "123 Maple St, Springfield"],
    ["Bedrooms", 4],
    ["Bathrooms", 3],
    ["Square Feet", 2150],
    ["Year Built", 2010],
    ["Garage", "2-car"],
    ["Lot Size (acres)", 0.35],
    ["Type", "Single-family home"],
    ["Heating", "Central"],
    ["Price ($)", 375000]
]);


const ResultsPage = () => {

    return (
        <div className={styles.container}>
            <Navbar />
            <ImageSection />
            <Grid entries={realEstateInfo} />
            <InputField />
            <BottomSection />
        </div>
    );
};

export default ResultsPage;