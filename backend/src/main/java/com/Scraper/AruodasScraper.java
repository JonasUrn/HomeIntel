package com.Scraper;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AruodasScraper implements ScraperMethods{
    private final Dictionary<String, String[]> allowedLinks;
    private String link;
    private Document doc;
    private Dictionary<String, String> dictionary;
    
    public AruodasScraper(Dictionary<String, String[]> allowedLinks, String link, Document doc){
        this.allowedLinks = allowedLinks;
        this.link = link;
        this.doc = doc;
        this.dictionary = new Hashtable<>();
    }

    @Override
    public Dictionary<String, String> getObjDetails() {
        try{
            Elements dataArr = doc.select(allowedLinks.get(link)[0]);
            Elements titles = dataArr.select(allowedLinks.get(link)[1]);
            Elements innerHTMLData = dataArr.select(allowedLinks.get(link)[2]);  
            
            Object[] titlesArr = titles.toArray();
            Object[] innerDataArr = innerHTMLData.toArray();
    
            int targetColLength = (titlesArr.length > innerDataArr.length && (titlesArr.length != innerDataArr.length)) ? innerDataArr.length : titlesArr.length;
            for(int i = 0; i < targetColLength; i++){
                dictionary.put(titles.get(i).text(), innerHTMLData.get(i).text());
            }
    
            String price = null;
            String price_per_meter = null;
            price = doc.select(allowedLinks.get(link)[3]).get(0).text();
            price_per_meter = doc.select(allowedLinks.get(link)[4]).get(0).text();

            dictionary.put("Price", price);
            dictionary.put("PricePer", price_per_meter);
        }
        catch(Exception e){
            System.out.printf("Error  of taking data. Target link: %s\n", link);
            return dictionary;
        }

        return dictionary;
    }
    
    @Override
    public void PrintData(String title){
        Enumeration<String> keys = null;
        try {
            if (dictionary == null || dictionary.isEmpty()) {
                System.out.println("Dictionary is empty. Trying again...");
                TryScrapeAgain(3);
            }
            keys = dictionary.keys();
        } catch (Exception e) {
            TryScrapeAgain(3);
            keys = dictionary.keys();
        } finally {
            if (keys == null) {
                System.out.println("Failed to scrape data.");
                return;
            }
            System.out.println(title);
            while (keys.hasMoreElements()) {
                String element = keys.nextElement();
                System.out.printf("%s\t%s\n", element, dictionary.get(element));
            }
            System.out.println("\n");
        }
    }
    @Override
    public Dictionary<String, String> TryScrapeAgain(int timesToScrape){
        int iterator = 0;
        while(iterator < timesToScrape){
            dictionary = getObjDetails();
            if(!dictionary.isEmpty()){break;}
            iterator++;
        }
        return dictionary;
    }
}
