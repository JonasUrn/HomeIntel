import React, { useState } from "react";
import styles from "./InputField.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowRight, faArrowLeft } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";

const InputField = ({ onSubmit }) => {
    const [value, setValue] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async () => {
        setValue("");
        onSubmit(value);
    };

    return (
        <div className={styles.wrapper} id="reevaluate">
            <h3 className={styles.reevaluationTitle}>Reevaluate the property</h3>

            <div className={styles.inputFieldContainer2}>
                <textarea
                    className={styles.input2}
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
