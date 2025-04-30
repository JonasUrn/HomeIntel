import React from "react";
import styles from "./ImageSection.module.css";
import ScoreCircle from "./ScoreCircle";

const ImageSection = ({ predPrice, price, score }) => {
    let demoPrice = 0;
    let priceDifference = "";

    if (price === 0 || price === undefined || price === null || price === "N/A") {
        demoPrice = 10; // Default value if price is not available
    } else {
        // Calculate the ratio of the smaller price to the larger price
        const ratio = predPrice < price
            ? predPrice / price
            : price / predPrice;

        // Scale to 0-10 where 10 is a perfect match (ratio = 1)
        demoPrice = (ratio * 10).toFixed(1);

        // Add indication of whether property is overvalued or undervalued
        if (predPrice > price) {
            priceDifference = "Overvalued";
        } else if (predPrice < price) {
            priceDifference = "Undervalued";
        } else {
            priceDifference = "Fair";
        }
    }

    return (
        <div className={styles.imageSection}>
            <div className={styles.circlesContainer}>
                <ScoreCircle text1="Score" score={score} outoften={true} />
                <ScoreCircle
                    text1="Fair Price"
                    score={demoPrice}
                    outoften={false}
                    text2={predPrice}
                    additionalInfo={priceDifference}
                />
            </div>
        </div>
    );
};

export default ImageSection;