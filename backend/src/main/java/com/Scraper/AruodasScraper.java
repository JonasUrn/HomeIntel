package com.Scraper;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AruodasScraper implements ScraperMethods{
    private final Dictionary<String, String[]> allowedLinks;
    private String link;
    private Document doc;
    private Map<String, Object> dictionary;
    private final double square_meter_to_square_feet = 10.764;
    private final double eur_to_usd = 1.08;


    public AruodasScraper(Dictionary<String, String[]> allowedLinks, String link, Document doc){
        this.allowedLinks = allowedLinks;
        this.link = link;
        this.doc = doc;
        this.dictionary = new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> getObjDetails() {
        try{
            Elements dataArr = doc.select(allowedLinks.get(link)[0]);
            Elements titles = dataArr.select(allowedLinks.get(link)[1]);
            Elements innerHTMLData = dataArr.select(allowedLinks.get(link)[2]);  
            Elements cityAddress = doc.select(allowedLinks.get(link)[5]);

            Object[] titlesArr = titles.toArray();
            Object[] innerDataArr = innerHTMLData.toArray();
            String cityAddressArr = cityAddress.get(0).text();

            int targetColLength = (titlesArr.length > innerDataArr.length && (titlesArr.length != innerDataArr.length)) ? innerDataArr.length : titlesArr.length;
            for(int i = 0; i < targetColLength; i++){
                String text = titles.get(i).text();
                if(text.contains("Area:")){
                    Double sqFt_area = Double.parseDouble(innerHTMLData.get(i).text().split(" ")[0].trim().replace(",", ".")) * square_meter_to_square_feet;
                    dictionary.put(CorrectKey(text), String.format("%.2f", sqFt_area));
                    continue;
                }
                String correctKey = CorrectKey(text);
                dictionary.put(correctKey, innerHTMLData.get(i).text());
            }
            
            String price = doc.select(allowedLinks.get(link)[3]).get(0).text();
            if(price.contains("€")){
                price = price.split("€")[0].trim().replace(" ", "");
            }

            Double usd_price = Double.parseDouble(price) * eur_to_usd;            
            dictionary.put("Price", usd_price.toString());
            if(dictionary.get("Area") != null){
                Double area = Double.parseDouble(dictionary.get("Area").toString());
                Double price_per_sqFoot = usd_price / area;
                dictionary.put("PricePer", price_per_sqFoot.toString());
            }
            dictionary.put("Country", "Lithuania"); //Because scraping only for aruodas.lt, otherwise - scrape page
            
            String[] splittedData = cityAddressArr.toString().split(", ");
            String addressNumber = splittedData[splittedData.length - 1].split(" ")[0].trim();
            String city = splittedData[0];
            String address = splittedData[2] + ", " + addressNumber;
            
            dictionary.put("City", city);
            dictionary.put("Address", address);
        }
        catch(Exception e){
            System.out.printf("Error  of taking data. Target link: %s", link);
            e.printStackTrace();
            return dictionary;
        }

        return dictionary;
    }
    
    @Override
    public void PrintData(String title) throws InterruptedException{
        Set<String> keys = null;
        try {
            if (dictionary == null || dictionary.isEmpty()) {
                System.out.println("Dictionary is empty. Trying again...");
                TryScrapeAgain(3);
            }
            keys = dictionary.keySet();
        } catch (Exception e) {
            TryScrapeAgain(3);
            keys = dictionary.keySet();
        } finally {
            if (keys == null) {
                System.out.println("Failed to scrape data.");
                return;
            }
            System.out.println(title);
            Iterator<String> iter = keys.iterator();
            while(iter.hasNext()){
                String key = iter.next();
                String element = (String) dictionary.get(key);
                System.out.printf("%s\t%s\n", key, element);
            }
            System.out.println("\n");
        }
    }
    @Override
    public Map<String, Object> TryScrapeAgain(int timesToScrape) throws InterruptedException{
        int iterator = 0;
        while(iterator < timesToScrape){
            Thread.sleep(1000);
            dictionary = getObjDetails();
            if(!dictionary.isEmpty()){break;}
            iterator++;
        }
        return dictionary;
    }

    public String CorrectKey(String websiteText){
        String lowercaseText = websiteText.split(":")[0].trim().toLowerCase();
        if(lowercaseText.contains("area")){
            return "Area";
        }
        else if(lowercaseText.contains("country")){
            return "Country";
        }
        else if(lowercaseText.contains("build year")){
            return "YearBuilt";
        }
        else if(lowercaseText.endsWith("floor")){
            return "FloorNr";
        }
        else if(lowercaseText.contains("no. of floors")){
            return "NumOfFloors";
        }
        else if(lowercaseText.contains("heating system")){
            return "Heating";
        }
        else if(lowercaseText.contains("efficiency class")){
            return "EnergyClass";
        }
        else if(lowercaseText.contains("number of rooms")){
            return "RoomCount";
        }
        return lowercaseText;
    }
}
