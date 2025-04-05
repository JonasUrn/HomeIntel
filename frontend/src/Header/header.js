import React, { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars } from "@fortawesome/free-solid-svg-icons";
import styles from "./header.module.css";
import SideNav, { NavItem, NavText } from '@trendmicro/react-sidenav';
import { logo, logoTransparent } from "../imports/importPictures.js";
import SearchBar from "../searchBar/searchBar.js";
import '@trendmicro/react-sidenav/dist/react-sidenav.css';

const Header = () => {
    const [showSideBar, setSideBar] = useState(false);

    return (
        <div className={styles.header}>
            <div className={styles.headerBar}>
                <div className={styles.navbar}>
                    <div className={styles.urlDiv}>
                        <a className={styles.websiteUrl} href="http://localhost:3000/">
                            <img src={logo} className={styles.logoURL}></img>
                        </a>
                    </div>
                    <div className={styles.otherLinks}>
                        <a className={styles.howItWorks}>Evaluation</a>
                        <a className={styles.investmentSimulator}>How it works</a>
                        <a className={styles.howSystemWorks}>About us</a>
                        <a className={styles.comparisonTool}>Contacts</a>
                    </div>
                </div>
            </div>
            <button className={styles.button} onClick={() => setSideBar(!showSideBar)}>
                <FontAwesomeIcon icon={faBars} className={styles.bars} />
            </button>
            {showSideBar && (
                <SideNav className={styles.mainSideBar}>
                    <SideNav.Nav className={styles.sidebarMenu}>
                        <NavItem><NavText className={styles.otherLink_} style={{ fontSize: '20px', fontWeight: '350' }}> Evaluation </NavText></NavItem>
                        <NavItem><NavText className={styles.investmentSimulator_} style={{ fontSize: '20px', fontWeight: '350' }}> Investment simulator </NavText></NavItem>
                        <NavItem><NavText className={styles.howSystemWorks_} style={{ fontSize: '20px', fontWeight: '350' }}> How system works </NavText></NavItem>
                        <NavItem><NavText className={styles.comparisonTool_} style={{ fontSize: '20px', fontWeight: '350' }}> Other link </NavText></NavItem>
                    </SideNav.Nav>
                </SideNav>
            )}
            <SearchBar />
        </div>
    );
}

export default Header;