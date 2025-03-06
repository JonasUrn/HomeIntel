import styles from "./searchBar.module.css";
import {FontAwesomeIcon, faArrowRight} from "../imports/importFiles.js";
import { useState, useEffect } from "react";

const statistics_data = {
    1: ["Properties evaluated", "4,114"],
    2: ["Visirors today", "16"],
    3: ["Happy customers", "1381"],
    4: ["Random text", "26"]
}

const ShowForm = () => {
    useEffect(() => {
        alert("This is form");
    }, []);
    return <div className="form">This is random text</div>
}
const SearchBar = () => {
    const [clicked, setClick] = useState(false);

    return (
        <div className={styles.searchBarStatistics}>
            <div className={styles.searchDiv}>
                <div className={styles.searchBoxText}>Begin the evaluation of your home here</div>
                <div className={styles.searchBarDiv}>
                    <input type="text" placeholder="Enter property listing URL" className={styles.inputField}></input>
                    <button className={styles.searchButton} onClick={() => {setClick(!clicked)}}>
                        <FontAwesomeIcon icon={faArrowRight} className={styles.arrow}/>
                    </button>
                </div>
            </div>
            {clicked && <ShowForm/>}
        </div>
    )
}
export default SearchBar;