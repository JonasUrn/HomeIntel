import React, { useEffect, useState } from "react";
import styles from "./card.module.css";

function getDimensions() {
    return { width: window.innerWidth, height: window.innerHeight };
}

const Card = ({ title, text, backText, align }) => {
    const [dimensions, setDimensions] = useState(getDimensions());

    useEffect(() => {
        function handleResize() {
            setDimensions(getDimensions());
        }

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, [])

    return (
        <div className={styles.card}>
            <div className={styles.cardInner} style={align === "right" && dimensions['width'] > 800 ? { marginLeft: "auto", marginRight: 0 } : {}}>
                <div className={styles.cardFront}>
                    <h3>{title}</h3>
                    <p>{text}</p>
                </div>
                <div className={styles.cardBack}>
                    <p>{backText}</p>
                </div>
            </div>
        </div>
    );
};

export default Card;
