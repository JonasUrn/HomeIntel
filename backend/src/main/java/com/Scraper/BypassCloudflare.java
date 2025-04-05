package com.Scraper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.jsoup.nodes.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;

public class BypassCloudflare {
    private String link;
    private String userAgent;   
    private boolean isActiveSecurity;
    private ChromeOptions options;
    private List<String> optionsList = Arrays.asList("--window-size=100,100", "--disable-gpu", "--no-sandbox"); //if --headless=new is here, then failing to scrape "--headless=new"
     
    public BypassCloudflare(String linkURL, String userAgent){
        this.link = linkURL;
        this.userAgent = userAgent;
        this.isActiveSecurity = false;
        
        options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL); //Load only DOM (images can be still loading). To speed up try to use PageLoadStrategy.EAGER (it loads the DOM, exclude images)
        options.addArguments(optionsList);
        
    }
    public Document getHTML() throws IOException, InterruptedException{
        String data = EstablishConnection();
        return Jsoup.parse(data);
    }

    public String EstablishConnection() throws IOException, InterruptedException{
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        driver.get(this.link);
        Thread.sleep(1000); //Thread sleep must be if attribute "--headless=new" is in optionsList
        String html = driver.getPageSource();
        return html;
    }
    public boolean isActiveProtection(){
        return this.isActiveSecurity;
    }   
    public String solveChallenge(String code){
        return "";
    }
}
