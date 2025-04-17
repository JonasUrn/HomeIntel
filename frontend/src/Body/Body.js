import React from "react";
import styles from "./body.module.css";
import Information from "./Information";

import { aboutUsCards, howToUseCards } from "./cardsData";

const Body = () => {
    return <div className={styles.body}>
        <Information id="how-to-use" cardsData={howToUseCards} title="How To Use" subtitle="Learn more about how to use our platform and how the evalaution works." />
        <div className={styles.divider}></div>
        <Information id="about-us" cardsData={aboutUsCards} title="About Us" subtitle="Learn more about our mission and vision behind the project." />
    </div>
}

export default Body;