import styles from "./searchBar.module.css";
import { faArrowRight } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useState, useEffect } from "react";

const ShowForm = () => {
    useEffect(() => {
        alert("This is form");
    }, []);
    return <div className="form"></div>
}
const SearchBar = () => {
    const [clicked, setClick] = useState(false);

    const handleModalForm = () => {
        setClick(!clicked);
    }
    return (
        <div className={styles.searchBarStatistics}>
            <div className={styles.searchDiv}>
                <div className={styles.searchBoxText}>Begin the evaluation of your home here</div>
                <div className={styles.searchBarDiv}>
                    <input type="text" placeholder="Enter property listing URL" className={styles.inputField}></input>
                    <button className={styles.searchButton} onClick={handleModalForm}>
                        <FontAwesomeIcon icon={faArrowRight} className={styles.arrow}/>
                    </button>
                </div>  
            </div>
            {clicked && <ShowForm/>}
        </div>
    )
}
export default SearchBar;