import React from "react";
import styles from "./header.css";

const Header = () => {
    return (
        <div className="header">
            <div className="navbar">
                <div className="url-div">
                    <a className="website-url">OurWebsite.com</a>
                </div>
                <div className="other-links">
                    <a className="how-it-works">Evaluation</a>
                    <a className="investment-simulator">Investment simulator</a>
                    <a className="investment-simulator">How system works</a>
                    <a className="comparison-tool">Other link</a>
                </div>
            </div>
        </div>
    );
}

export default Header;