import React, { useState } from "react";
import styles from "./Grid.module.css";

const Grid = ({ entries }) => {
    const items = [...entries.entries()];
    const totalItems = items.length < 10
        ? [...items, ...Array(10 - items.length).fill(["No Data", ""])]
        : items;

    const [data, setData] = useState(Object.fromEntries(items));

    const handleChange = (key, newValue) => {
        setData(prevData => ({
            ...prevData,
            [key]: newValue
        }));
    };

    return (
        <div className={styles.grid}>
            {totalItems.map(([key, value], index) => (
                <div key={index} className={styles.gridItem}>
                    <strong>{key}</strong>:
                    <input
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
