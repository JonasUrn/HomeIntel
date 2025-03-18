package com.Scraper;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.UserAgent.UserAgentGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.None;

public class Scraper {
    private final String[] allowedLinks = new String[] {"https://www.aruodas.lt"}; 
    private final int timeoutSeconds = 3;
    private String link;
    private Document doc;
    private Dictionary<String, String> dictionary;
    private String agentData;

    public Scraper(String linkToRealEstate){
        for(int i = 0; i < allowedLinks.length; i++){
            if(linkToRealEstate.contains(allowedLinks[i])){
                this.link = linkToRealEstate;
                try {
                    UserAgentGenerator userAgentObj = new UserAgentGenerator(1);
                    String agent = userAgentObj.getUserAgent();
                    this.agentData = agent;
                    // System.out.printf("Got agent: %s\n", agent);
                    
                    this.doc = Jsoup.connect(linkToRealEstate)
                                    .userAgent(agent)
                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                                    .header("Accept-Language", "en-US,en;q=0.5")
                                    .header("Referer", "https://www.aruodas.lt/")
                                    .header("Connection", "keep-alive")
                                    .timeout(timeoutSeconds * 1000)
                                    .get();
                    this.dictionary = new Hashtable<>();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Dictionary<String, String> getObjDetails(){
        Elements dataArr = doc.select("dl[class='obj-details']");
        Elements titles = dataArr.select("dt");
        Elements innerHTMLData = dataArr.select("span[class='fieldValueContainer']");  
        
        Object[] titlesArr = titles.toArray();
        Object[] innerDataArr = innerHTMLData.toArray();

        int targetColLength = (titlesArr.length > innerDataArr.length && (titlesArr.length != innerDataArr.length)) ? innerDataArr.length : titlesArr.length;
        for(int i = 0; i < targetColLength; i++){
            dictionary.put(titles.get(i).text(), innerHTMLData.get(i).text());
        }
        String price = doc.select("span[class='price-eur']").get(0).text();
        String price_per_meter = doc.select("span[class='price-per']").get(0).text();

        dictionary.put("Kaina", price);
        dictionary.put("Kaina kv. m.", price_per_meter);
        return dictionary;
    }
    public boolean isCorrect(){
        return !this.dictionary.isEmpty();
    }
    public String getAgent(){
        return agentData;
    }
}
