package com.Scraper;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class ZillowScraper implements ScraperMethods{
    private final Dictionary<String, String[]> allowedLinks;
    private String link;
    private String linkToObject;
    private Document doc;
    private Dictionary<String, String> dictionary;
    private final double feet_meter_mult_value = 0.092903;
    private final double usdToEurRate = 0.92;
    private final double acre_to_sqM = 4046.85642;

    public ZillowScraper(Dictionary<String, String[]> allowedLinks, String link, Document doc, String linkToObject){
        this.allowedLinks = allowedLinks;
        this.link = link;
        this.doc = doc;
        this.dictionary = new Hashtable<>();
        this.linkToObject = linkToObject;
    }   

    @Override
    public Dictionary<String, String> getObjDetails() {

        Elements basic_data = doc.select(allowedLinks.get(link)[1]); //Getting basic elements (price, address, bends quantity, house sqft)        
        
        Double lotarea = BuildDate(); // Getting build date and lotarea (area around house)
        Double hauseArea = null;
        try{
            List<TextNode> price_address = basic_data.get(0).getAllElements().textNodes();
            String address = "";
            for(int i = 0; i < price_address.size(); i++){
                String textValue = price_address.get(i).toString().toLowerCase();

                if(textValue.contains("$") && textValue.contains(",")){
                    Double priceEur = Double.parseDouble(textValue.replace("$", "").replace(",", "").strip())*usdToEurRate;
                    dictionary.put("Price", String.format("%.2f", priceEur));
                }
                else if(textValue.contains("beds") || textValue.contains("baths")){
                    String value = price_address.get(i-1).toString();
                    dictionary.put((textValue.contains("beds") ? "Beds" : "Baths"), value);
                }
                else if(((i == 1 || i == 3) && price_address.size() == 10) || ((i == 11 || i == 13) && price_address.size() == 14)){
                    address += textValue;
                    if(i == 3){ dictionary.put("Address", address); }
                }
                else if(textValue.contains("sqft")){
                    Double sqM_sqM = Double.parseDouble(price_address.get(i-1).toString().replace(",", ""))*feet_meter_mult_value;
                    dictionary.put("House area", String.format("%,.2f", sqM_sqM));
                    hauseArea = sqM_sqM;
                }
                // System.out.printf("\n%s)\t%s", i+1, price_address.get(i).toString());
            }
            Double totalArea = lotarea + hauseArea;
            dictionary.put("Total area", totalArea.toString());
        }
        catch(Exception e){
            System.out.printf("\nError  of taking data. Target link: %s\n", linkToObject);
            return dictionary;
        }
        // System.out.println(dictionary);
        return dictionary;
    }
    
    public double BuildDate(){
        try{
            Elements buildDate_Lotarea = doc.select(allowedLinks.get(link)[0]); //Getting building date and house lotarea
            List<TextNode> build_date_lotarea = buildDate_Lotarea.get(3).getAllElements().textNodes();

            for(int i = 0; i < build_date_lotarea.size(); i++){
                String textValue = build_date_lotarea.get(i).text().toLowerCase();
                if(i == 1){
                    dictionary.put("Buid date", textValue.split(" ")[2]);
                }
                else if( i == 2 && (textValue.contains("square feet") || textValue.contains("acres"))){
                    Double finalVal = null;
                    if(textValue.contains("square feet")){
                        finalVal = Double.parseDouble(textValue.split(" ")[0].replace(",", "")) * feet_meter_mult_value;
                    }
                    else{
                        finalVal = Double.parseDouble(textValue.split(" ")[0]) * acre_to_sqM;
                    }
                    dictionary.put("Lotarea", String.format("%,.2f", finalVal).toString());
                }
            }
            return Double.parseDouble(dictionary.get("Lotarea").replace(",", ""));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Double val = -1.00;
        return val;
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
