import React, { useState } from "react";
import styles from "./InputField.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowRight, faArrowLeft } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";

const InputField = () => {
    const [value, setValue] = useState("");
    const navigate = useNavigate();

    const handleSubmit = () => {
        console.log("Submitted:", value);
    };

    return (
        <div className={styles.wrapper}>
            <h3 className={styles.reevaluationTitle}>Reevaluate the property</h3>

            <div className={styles.inputFieldContainer}>
                <textarea
                    className={styles.input}
                    value={value}
                    placeholder="Enter reevaluation input..."
                    onChange={(e) => setValue(e.target.value)}
                    rows={4}
                />
                <FontAwesomeIcon
                    icon={faArrowRight}
                    className={styles.arrow}
                    onClick={handleSubmit}
                />
            </div>

            <button className={styles.homeButton} onClick={() => navigate("/")}>
                <FontAwesomeIcon icon={faArrowLeft} />
                <span>Back to Home</span>
            </button>
        </div>
    );
};

export default InputField;
