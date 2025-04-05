import React, { useState } from "react";
import styles from "./ResultsPage.module.css";
import Navbar from "./NavBar";
import ImageSection from "./ImageSection";
import Grid from "./Grid";
import BottomSection from "./BottomSection";
import InputField from "./InputField";
import { useLocation } from "react-router-dom";
import axios from 'axios';

const ResultsPage = () => {
    const location = useLocation();

    const { geminiResponse, PredictedPrice, PredictedScore } = location.state || {};
    const realEstateInfo = geminiResponse || {};

    const predictedPrice = PredictedPrice || "N/A";
    const predictedScore = PredictedScore || "N/A";
    const price = geminiResponse?.Price || "N/A";

    const realEstateDetails = {
        Address: realEstateInfo?.Addres || "Address not available",
        City: realEstateInfo?.City || "City not available",
        Country: realEstateInfo?.Country || "Country not available",
        Description: realEstateInfo?.Description || "Description not available",
        EnergyClass: realEstateInfo?.EnergyClass || "Energy Class not available",
        FloorNr: realEstateInfo?.FloorNr || "Floor number not available",
        HasBalcony: realEstateInfo?.HasBalcony !== null ? realEstateInfo?.HasBalcony : "Balcony info not available",
        Heating: realEstateInfo?.Heating || "Heating info not available",
        Latitude: realEstateInfo?.Latitude || "Latitude not available",
        Longitude: realEstateInfo?.Longitude || "Longitude not available",
        NearestSchool: realEstateInfo?.NearestSchool || "School info not available",
        NearestShop: realEstateInfo?.NearestShop || "Shop info not available",
        NumOfFloors: realEstateInfo?.NumOfFloors || "Number of floors not available",
        PropertyType: realEstateInfo?.PropertyType || "Property type not available",
        RoomCount: realEstateInfo?.RoomCount || "Room count not available",
        YearBuilt: realEstateInfo?.YearBuilt || "Year built not available",
    };

    const [gridData, setGridData] = useState(Object.entries(realEstateDetails));

    const handleGridDataChange = (newData) => {
        setGridData(Object.entries(newData));
    };

    const reevaluationSubmitHandler = async (prompt) => {

        const requestData = {
            prompt: prompt,
            gridData: Object.fromEntries(gridData),
            price: price,
        };

        try {
            const response = await axios.post("http://localhost:8080/evaluate/reevaluate", requestData, {
                headers: {
                    "Content-Type": "application/json",
                },
            });

            console.log(response.data);
        } catch (error) {
            console.error("Error sending request:", error);
        }
    }

    return (
        <div className={styles.container}>
            <Navbar />
            <ImageSection predPrice={predictedPrice} price={price} score={predictedScore} />
            <Grid entries={realEstateDetails} onDataChange={handleGridDataChange} />
            <InputField onSubmit={reevaluationSubmitHandler} />
            <BottomSection />
        </div>
    );
};

export default ResultsPage;
