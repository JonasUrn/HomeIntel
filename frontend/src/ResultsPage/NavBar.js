import React from "react";
import styles from "./NavBar.module.css";
import { Link } from "react-router-dom";

const Navbar = () => (
    <nav className={styles.navbar}>
        <div className={styles.text}>TEXT</div>
        <div className={styles.links}>
            <Link to="/">Home</Link>
            <Link to="/about">About</Link>
            <Link to="/contact">Contact</Link>
        </div>
    </nav>
);

export default Navbar;