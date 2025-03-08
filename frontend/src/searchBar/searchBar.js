import styles from "./searchBar.module.css";
import { faArrowRight, faKeyboard } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useState } from "react";
import ModalWindow from "../ModalWindow/modal.js";

const SearchBar = () => {
    const [clicked, setClick] = useState(false);
    const [switchToTextBox, setSwitch] = useState(false);

    const HandleInputField = () => {
        return <input type="text" placeholder="Enter property listing URL" className={styles.inputField}></input>
    }
    const HandleTextBoxField = () => {
        return <textarea type="text" placeholder="Describe your poperty" className={styles.inputTextBox}></textarea>
    }
    return (
        <div className={styles.searchBarStatistics}>
            <div className={styles.searchDiv}>
                <div className={styles.searchBoxText}>Begin the evaluation of your home here</div>
                <div className={styles.searchBarDiv}>
                    <button className={styles.keyboardBtn} onClick={() => setSwitch(!switchToTextBox)}>
                        <FontAwesomeIcon icon={faKeyboard} className={styles.keyboard} />
                    </button>
                    {!switchToTextBox && <HandleInputField />}
                    {switchToTextBox && <HandleTextBoxField />}
                    <button className={styles.searchButton} onClick={() => setClick(true)}>
                        <FontAwesomeIcon icon={faArrowRight} className={styles.arrow} />
                    </button>
                </div>
            </div>
            {clicked && <ModalWindow isOpen={clicked==true} onClose={clicked==false}/>}
        </div>
    )
}
export default SearchBar;