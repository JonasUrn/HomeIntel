import {React, useState} from "react";
import {FontAwesomeIcon, faBars, header_styles} from "../imports/importFiles.js";
import SideNav, { NavItem, NavText } from '@trendmicro/react-sidenav';
import '@trendmicro/react-sidenav/dist/react-sidenav.css';

const Header = () => {
    const [showSideBar, setSideBar] = useState(false);

    return (
        <div className={header_styles.header}>
            <div className={header_styles.navbar}>
                <div className={header_styles.urlDiv}>
                    <a className={header_styles.websiteUrl} href="http://localhost:3000/">OurWebsite.com</a>
                </div>
                <div className={header_styles.otherLinks}>
                    <a className={header_styles.howItWorks}>Evaluation</a>
                    <a className={header_styles.investmentSimulator}>Investment simulator</a>
                    <a className={header_styles.howSystemWorks}>How system works</a>
                    <a className={header_styles.comparisonTool}>Other link</a>
                </div>
            </div>
            <button className={header_styles.button} onClick={ () => setSideBar(!showSideBar)}>
                <FontAwesomeIcon icon={faBars} className={header_styles.bars} />
            </button>
            {showSideBar && (
                <SideNav className={header_styles.mainSideBar}>
                    <SideNav.Nav className={header_styles.sidebarMenu}>
                        <NavItem><NavText className={header_styles.otherLink_} style={{fontSize: '20px', fontWeight: '350'}}> Evaluation </NavText></NavItem>
                        <NavItem><NavText className={header_styles.investmentSimulator_} style={{fontSize: '20px', fontWeight: '350'}}> Investment simulator </NavText></NavItem>
                        <NavItem><NavText className={header_styles.howSystemWorks_} style={{fontSize: '20px', fontWeight: '350'}}> How system works </NavText></NavItem>
                        <NavItem><NavText className={header_styles.comparisonTool_} style={{fontSize: '20px', fontWeight: '350'}}> Other link </NavText></NavItem>
                    </SideNav.Nav>
                </SideNav>
            )}
        </div>
    );
}

export default Header;