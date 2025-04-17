import React, { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars } from "@fortawesome/free-solid-svg-icons";
import styles from "./NavBar.module.css";
import SideNav, { NavItem, NavText } from '@trendmicro/react-sidenav';
import { logo } from "../imports/importPictures.js";
import '@trendmicro/react-sidenav/dist/react-sidenav.css';

const Navbar = ({ navLinks = [] }) => {
    const [showSideBar, setSideBar] = useState(false);

    const scrollToSection = (id) => {
        if (showSideBar) {
            setSideBar(false);
        }

        setTimeout(() => {
            const element = document.getElementById(id);
            if (element) {
                const headerHeight = document.querySelector(`.${styles.headerBar}`)?.offsetHeight || 0;
                const elementPosition = element.getBoundingClientRect().top + window.pageYOffset;
                const offsetPosition = elementPosition - headerHeight;

                window.scrollTo({
                    top: offsetPosition,
                    behavior: 'smooth'
                });
            } else {
                console.warn(`Element with id "${id}" not found`);
            }
        }, 100);
    };

    const scrollToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    };

    const defaultNavLinks = [
        { title: "How it works", id: "how-to-use" },
        { title: "About us", id: "about-us" },
        { title: "Contacts", id: "footer" }
    ];

    const links = navLinks.length > 0 ? navLinks : defaultNavLinks;

    return (
        <div className={styles.header}>
            <div className={styles.headerBar}>
                <div className={styles.navbar}>
                    <div className={styles.urlDiv}>
                        <a className={styles.websiteUrl} onClick={scrollToTop} style={{ cursor: 'pointer' }}>
                            <img src={logo} className={styles.logoURL} alt="Logo"></img>
                        </a>
                    </div>
                    <div className={styles.otherLinks}>
                        {links.map((link, index) => (
                            <a
                                key={index}
                                className={styles[link.id] || styles.navLink}
                                onClick={() => scrollToSection(link.id)}
                            >
                                {link.title}
                            </a>
                        ))}
                    </div>
                </div>
            </div>
            <button className={styles.button} onClick={() => setSideBar(!showSideBar)}>
                <FontAwesomeIcon icon={faBars} className={styles.bars} />
            </button>
            {showSideBar && (
                <SideNav className={styles.mainSideBar}>
                    <SideNav.Nav className={styles.sidebarMenu}>
                        {links.map((link, index) => (
                            <NavItem key={index}>
                                <NavText
                                    className={styles[`${link.id}_`] || styles.navLink_}
                                    style={{ fontSize: '20px', fontWeight: '350', cursor: 'pointer' }}
                                    onClick={() => scrollToSection(link.id)}
                                >
                                    {link.title}
                                </NavText>
                            </NavItem>
                        ))}
                    </SideNav.Nav>
                </SideNav>
            )}

        </div>
    );
}

export default Navbar;