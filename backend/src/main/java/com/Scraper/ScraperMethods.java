package com.Scraper;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

public interface ScraperMethods {

    public Map<String, Object> getObjDetails();
    public void PrintData(String title) throws InterruptedException;
    public Map<String, Object> TryScrapeAgain(int timesToScrape) throws InterruptedException;
}
