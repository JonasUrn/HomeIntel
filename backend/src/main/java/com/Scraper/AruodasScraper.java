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
    private final double square_meter_to_square_feet = 10.764;
    private final double eur_to_usd = 1.08;


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
                String text = titles.get(i).text();
                if(text.contains("Area:")){ //Nesuveikia
                    Double sqFt_area = Double.parseDouble(innerHTMLData.get(i).text().split(" ")[0].trim().replace(",", ".")) * square_meter_to_square_feet;
                    dictionary.put(text, sqFt_area.toString());
                    continue;
                }
                dictionary.put(text, innerHTMLData.get(i).text());
            }
            
            String price = doc.select(allowedLinks.get(link)[3]).get(0).text();
            if(price.contains("€")){
                price = price.split("€")[0].trim().replace(" ", "");
            }
            //Kovertuoti price eur i usd
            //Konvertuoti eur_price_per_meter i usd_price_per_sqFeet
            Double usd_price = Double.parseDouble(price) * eur_to_usd;            
            dictionary.put("Price", usd_price.toString());
            if(dictionary.get("Area:") != null){
                Double price_per_sqFoot = usd_price / Double.parseDouble(dictionary.get("Area:"));
                dictionary.put("PricePer", price_per_sqFoot.toString());
            }
        }
        catch(Exception e){
            System.out.printf("Error  of taking data. Target link: %s\n", link);
            return dictionary;
        }

        return dictionary;
    }
    
    @Override
    public void PrintData(String title) throws InterruptedException{
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
    public Dictionary<String, String> TryScrapeAgain(int timesToScrape) throws InterruptedException{
        int iterator = 0;
        while(iterator < timesToScrape){
            Thread.sleep(1000);
            dictionary = getObjDetails();
            if(!dictionary.isEmpty()){break;}
            iterator++;
        }
        return dictionary;
    }
}
