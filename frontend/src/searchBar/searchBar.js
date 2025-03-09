import styles from "./searchBar.module.css";
import { faArrowRight, faKeyboard } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useState } from "react";
import ModalWindow from "../ModalWindow/modal.js";

const SearchBar = () => {
    const [clicked, setClick] = useState(false);
    const [switchToTextBox, setSwitch] = useState(false);
    const [inputValue, setInputValue] = useState(""); // Stores user input

    const handleModalOpen = () => {
        setClick(true);
    };

    const handleModalClose = () => {
        setClick(false);
    };

    return (
        <div className={styles.searchBarStatistics}>
            <div className={styles.searchDiv}>
                <div className={styles.searchBoxText}>Begin the evaluation of your home here</div>

                <div className={styles.searchBarDiv}>
                    {!switchToTextBox ? (
                        <input
                            type="text"
                            placeholder="Enter property listing URL"
                            className={styles.inputField}
                            value={inputValue}
                            onChange={(e) => setInputValue(e.target.value)}
                        />
                    ) : (
                        <textarea
                            placeholder="Describe your property"
                            className={styles.inputTextBox}
                            value={inputValue}
                            onChange={(e) => setInputValue(e.target.value)}
                        />
                    )}
                    <button className={styles.searchButton} onClick={handleModalOpen}>
                        <FontAwesomeIcon icon={faArrowRight} className={styles.arrow} />
                    </button>
                </div>
                <div className={styles.toggleContainer}>
                    <button
                        className={`${styles.toggleButton} ${!switchToTextBox ? styles.active : ""}`}
                        onClick={() => { setSwitch(false); setInputValue(""); }}
                    >
                        Link
                    </button>
                    <button
                        className={`${styles.toggleButton} ${switchToTextBox ? styles.active : ""}`}
                        onClick={() => { setSwitch(true); setInputValue(""); }}
                    >
                        Enter Details
                    </button>
                </div>
            </div>
            {clicked && (
                <ModalWindow
                    isOpen={clicked}
                    onClose={handleModalClose}
                    data={inputValue}
                    isLink={!switchToTextBox}
                />
            )}
        </div>
    );
};

export default SearchBar;
