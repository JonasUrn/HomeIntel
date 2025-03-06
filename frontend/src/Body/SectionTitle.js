import React from "react";

import styles from "./sectionTitle.module.css";

const SectionTitle = ({ title, subtitle }) => {
    return <div className={styles.sectionHeader}>
        <h1 className={styles.title}>{title}</h1>
        <p className={styles.subtitle}>
            {subtitle}
        </p>
    </div>
}

export default SectionTitle;