package com.Scraper;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.UserAgent.UserAgentGenerator;
import com.Scraper.AruodasScraper;

public class Scraper{
    private final Dictionary<String, String[]> allowedLinks = new Hashtable<>();
    private String[] links;
    private final int timeoutSeconds = 10;
    private String linkDomain;
    private String link;
    private String linkToObject;
    private Document doc;
    private Dictionary<String, String> dictionary;
    private String agentData;
    private boolean isCorrectData = true;
    
    public Scraper(String linkToRealEstate){

        AddInformation();
        this.linkToObject = linkToRealEstate;
        for(int i = 0; i < links.length; i++){
            if(linkToRealEstate.contains(links[i])){
                this.link = links[i];
                try {
                    UserAgentGenerator userAgentObj = new UserAgentGenerator(1);
                    String agent = userAgentObj.getUserAgent();
                    this.agentData = agent;
                    
                    if(linkToRealEstate.contains("aruodas")){
                        boolean containsWWW = linkToRealEstate.split("https://")[1].contains("www");
                        if(containsWWW){
                            linkToRealEstate = linkToRealEstate.replace("https://www.", "https://en.") ;
                        }
                    }
                    String[] urlParts = linkToRealEstate.toString().split("\\.");
                    String mainUrl = urlParts[0] + "." + urlParts[1] + "." + urlParts[2].split("/")[0];
                    this.linkDomain = mainUrl;
                    
                    BypassCloudflare avoidCloudflare = new BypassCloudflare(linkToRealEstate, this.agentData);
                    this.doc = avoidCloudflare.getHTML();
                    
                    if(this.doc == null && !this.linkDomain.contains("zillow")){
                        System.out.println("Error during scraping data");
                        return;
                    }
                    this.dictionary = new Hashtable<>();
                } catch (Exception e) {
                    isCorrectData = false;
                    e.printStackTrace();
                }
            }
        }
    }

    public void AddInformation(){
        allowedLinks.put("https://www.aruodas.lt", new String[] {"dl[class='obj-details']", "dt", "span[class='fieldValueContainer']", "span[class='price-eur']", "span[class='price-per']", "h1[class='obj-header-text']"});
        allowedLinks.put("https://en.aruodas.lt", new String[] {"dl[class='obj-details']", "dt", "span[class='fieldValueContainer']", "span[class='price-eur']", "span[class='price-per']"});
        allowedLinks.put("https://www.zillow.com/homedetails", new String[] {"div[class='styles__StyledDataModule-fshdp-8-106-0__sc-14rfp2w-0 kDCWqg']", "div[data-testid='home-details-chip-container']"});

        this.links = new String[allowedLinks.size()];
        Enumeration<String> keys = allowedLinks.keys();
        int i = 0;
        while(keys.hasMoreElements()){
            this.links[i++] = keys.nextElement();
        }
    }

    public Map<String, Object> GetResults(){
        Map<String, Object> dict = new HashMap<>();
        if(isCorrectData){
            try{
                switch (this.linkDomain) {
                    case "https://en.aruodas.lt":
                        AruodasScraper aruodas = new AruodasScraper(allowedLinks, link, doc);
                        dict = aruodas.getObjDetails(); 
                        aruodas.PrintData("Jau printData() metode:\n");
                        break;
                    case "https://www.zillow.com":
                        ZillowScraper zillow = new ZillowScraper(allowedLinks, link, doc, this.linkToObject);
                        dict = zillow.getObjDetails();
                        zillow.PrintData("\n");
                        // String[] houseLinkParts = linkToObject.split("_zpid/")[0].split("/");
                        // String housePostId = houseLinkParts[houseLinkParts.length - 1];
                        // System.out.printf("House link ID: %s", housePostId);
                        // ZillowAPI zillow = new ZillowAPI(housePostId);
                        // dict = zillow.getStructuredData();

                        break;
                    default:
                        break;   
                }
            }
            catch (Exception e){
                e.printStackTrace();
                isCorrectData = false;
                System.out.println("Error in GetResults() method Scraper klases");
            }
        }
        return dict;
    }
    public boolean isCorrect(){
        return !this.dictionary.isEmpty();
    }
    public String getAgent(){
        return agentData;
    }
}