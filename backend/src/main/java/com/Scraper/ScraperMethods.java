package com.Scraper;

import java.util.Dictionary;
import java.util.Enumeration;

public interface ScraperMethods {

    public Dictionary<String, String> getObjDetails();
    public void PrintData(String title) throws InterruptedException;
    public Dictionary<String, String> TryScrapeAgain(int timesToScrape) throws InterruptedException;
}
