import React from "react";
import styles from "./footer.module.css";

import { faEnvelope } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFacebookF, faTwitter } from "@fortawesome/free-brands-svg-icons";

const Footer = () => {
    return (
        <footer className={styles.footer}>
            <div className={styles.socialMedia}>
                <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
                    <FontAwesomeIcon icon={faTwitter} />
                </a>
                <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
                    <FontAwesomeIcon icon={faFacebookF} />
                </a>
                <a href="mailto:someone@example.com">
                    <FontAwesomeIcon icon={faEnvelope} />
                </a>
            </div>
            <div className={styles.copyRight}>
                <p>&copy; 2025 HomeIntel. All rights reserved.</p>
            </div>
            <div className={styles.footerLinks}>
                <a href="/privacy-policy">Privacy Policy</a>
                <a href="/terms-of-service">Terms of Service</a>
            </div>
        </footer>
    );
};

export default Footer;
