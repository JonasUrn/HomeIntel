import React, { useState, useEffect } from "react";
import styles from "./ResultsPage.module.css";
import Navbar from "./NavBar";
import ImageSection from "./ImageSection";
import Grid from "./Grid";
import BottomSection from "./BottomSection";
import InputField from "./InputField";
import { useLocation } from "react-router-dom";
import axios from 'axios';
import LoadingSpinner from "../ModalWindow/LoadingSpinner";
import Footer from "../Footer/Footer";

var headerLinks = [
    { title: "Reevalaute", id: "reevalaute" },
    { title: "Housing Market", id: "housing-market" },
    { title: "Contacts", id: "footer" }
]

const ResultsPage = () => {
    const location = useLocation();
    const [isLoading, setIsLoading] = useState(false);

    // Initialize state with location data
    const [propertyData, setPropertyData] = useState({
        geminiResponse: location.state?.geminiResponse || {},
        PredictedPrice: location.state?.PredictedPrice || "N/A",
        PredictedScore: location.state?.PredictedScore || "N/A"
    });

    // Destructure current data for easier access
    const { geminiResponse, PredictedPrice, PredictedScore } = propertyData;
    const realEstateInfo = geminiResponse || {};
    const price = realEstateInfo?.Price || "N/A";

    // Create real estate details object based on current geminiResponse
    const buildRealEstateDetails = (data) => {
        return {
            Address: data?.Addres || "Address not available",
            Area: data?.Area || "Area not available",
            City: data?.City || "City not available",
            Country: data?.Country || "Country not available",
            Description: data?.Description || "Description not available",
            EnergyClass: data?.EnergyClass || "Energy Class not available",
            FloorNr: data?.FloorNr || "Floor number not available",
            HasBalcony: data?.HasBalcony !== null ? data?.HasBalcony : "Balcony info not available",
            Heating: data?.Heating || "Heating info not available",
            Latitude: data?.Latitude || "Latitude not available",
            Longitude: data?.Longitude || "Longitude not available",
            NearestSchool: data?.NearestSchool || "School info not available",
            NearestShop: data?.NearestShop || "Shop info not available",
            NumOfFloors: data?.NumOfFloors || "Number of floors not available",
            PropertyType: data?.PropertyType || "Property type not available",
            RoomCount: data?.RoomCount || "Room count not available",
            YearBuilt: data?.YearBuilt || "Year built not available",
        };
    };

    // Initialize details and grid data
    const [realEstateDetails, setRealEstateDetails] = useState(buildRealEstateDetails(realEstateInfo));
    const [gridData, setGridData] = useState(Object.entries(realEstateDetails));

    // Update details when geminiResponse changes
    useEffect(() => {
        const details = buildRealEstateDetails(realEstateInfo);
        setRealEstateDetails(details);
        setGridData(Object.entries(details));
    }, [realEstateInfo]);

    const handleGridDataChange = (newData) => {
        setGridData(Object.entries(newData));
        setRealEstateDetails(newData);
    };

    const reevaluationSubmitHandler = async (prompt) => {
        setIsLoading(true);

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

            // Parse and update the data with the new response
            const responseData = response.data;

            // Update the state with new data
            setPropertyData({
                geminiResponse: responseData.geminiResponse || geminiResponse,
                PredictedPrice: responseData.PredictedPrice || PredictedPrice,
                PredictedScore: responseData.PredictedScore || PredictedScore
            });

            console.log("Reevaluation successful:", responseData);

        } catch (error) {
            console.error("Error sending request:", error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <>
            {isLoading && (
                <div className={styles.loadingOverlay}>
                    <LoadingSpinner size="large" color="rgb(53,109,90)" />
                </div>
            )}
            <div className={styles.container}>
                <Navbar navLinks={headerLinks} />
                <ImageSection
                    predPrice={PredictedPrice}
                    price={price}
                    score={PredictedScore}
                />
                <Grid
                    entries={realEstateDetails}
                    onDataChange={handleGridDataChange}
                />
                <InputField onSubmit={reevaluationSubmitHandler} />
                <BottomSection />
                <Footer />
            </div>
        </>
    );
};

export default ResultsPage;