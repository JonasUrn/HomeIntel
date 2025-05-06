import React, { useState } from "react";
import styles from "./Grid.module.css";

const Grid = ({ entries, onDataChange }) => {
    const importanceOrder = [
        "Address",
        "Area",
        "City",
        "Country",
        "YearBuilt",
        "FloorNr",
        "NumOfFloors",
        "RoomCount",
        "BedroomCount",
        "BathroomCount",
        "Heating",
        "EnergyClass",
        "State",
        "ParkingSpotAvailable",
        "HasBalcony",
        "Longitude",
        "Latitude",
    ];
    //console.log(entries);
    const items = entries instanceof Map ? [...entries.entries()] : Object.entries(entries);

    const filteredItems = importanceOrder
        .map((key) => {
            const entry = items.find(([field]) => field === key);
            return entry ? entry : null;
        })
        .filter((item) => item !== null)
        .slice(0, 10);

    const [data, setData] = useState(Object.fromEntries(filteredItems));

    const handleChange = (key, newValue) => {
        const newData = { ...data, [key]: newValue };
        setData(newData);
        onDataChange(newData);
    };

    return (
        <div className={styles.grid}>
            {filteredItems.map(([key, value], index) => (
                <div key={index} className={styles.gridItem}>
                    <strong>{key}</strong>:
                    <input
                        className={styles.gridInput}
                        type="text"
                        value={data[key] !== undefined ? data[key] : value}
                        onChange={(e) => handleChange(key, e.target.value)}
                    />
                </div>
            ))}
        </div>
    );
};

export default Grid;
