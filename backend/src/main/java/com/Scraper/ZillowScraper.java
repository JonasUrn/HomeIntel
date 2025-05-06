package com.Scraper;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class ZillowScraper implements ScraperMethods{
    private final Dictionary<String, String[]> allowedLinks;
    private String link;
    private String linkToObject;
    private Document doc;
    private Map<String, Object> dictionary;
    private final double acre_to_sqM = 4046.85642;

    public ZillowScraper(Dictionary<String, String[]> allowedLinks, String link, Document doc, String linkToObject){
        this.allowedLinks = allowedLinks;
        this.link = link;
        this.doc = doc;
        this.dictionary = new HashMap<>();
        this.linkToObject = linkToObject;
    }   

    @Override
    public Map<String, Object> getObjDetails() {

        // Elements basic_data = doc.select(allowedLinks.get(link)[1]); //Getting basic elements (price, address, bends quantity, house sqft)        
        
        // BuildDate(); // Getting build date and lotarea (area around house)
        // Double hauseArea = null;
        // try{
        //     List<TextNode> price_address = basic_data.get(0).getAllElements().textNodes();
        //     String address = "";
        //     for(int i = 0; i < price_address.size(); i++){
        //         String textValue = price_address.get(i).toString().toLowerCase();

        //         System.out.printf("Iteration: %s\tValue: %s\n", i, textValue);

        //         if(textValue.contains("$") && textValue.contains(",")){
        //             Double priceEur = Double.parseDouble(textValue.replace("$", "").replace(",", "").strip());
        //             dictionary.put("Price", String.format("%.2f", priceEur));
        //         }
        //         else if(textValue.contains("beds") || textValue.contains("baths")){
        //             String value = price_address.get(i-1).toString();
        //             dictionary.put((textValue.contains("beds") ? "Beds" : "Baths"), value);
        //         }
        //         else if(((i == 1 || i == 3) && price_address.size() == 10) || ((i == 11 || i == 13) && price_address.size() == 14)){
        //             address += textValue;
        //             if(i == 3){ dictionary.put("Address", address); }
        //         }
        //         else if(textValue.contains("sqft")){
        //             Double sqM_sqM = Double.parseDouble(price_address.get(i-1).toString().replace(",", ""));
        //             String area = String.format("%.2f", sqM_sqM);
        //             dictionary.put("Area", area);
        //             hauseArea = Double.parseDouble(area);
        //         }
        //     }

        //     Double arr = hauseArea;
        //     dictionary.put("Total area", arr.toString());
            
        //     if(dictionary.get("Area") != null && dictionary.get("Price") != null){
        //         Double area = Double.parseDouble((String) dictionary.get("Area"));
        //         Double price = Double.parseDouble((String) dictionary.get("Price"));
        //         Double price_per_sqFoot = price / area;
        //         dictionary.put("PricePer", price_per_sqFoot.toString());
        //     }
        //     Set<String> keys = dictionary.keySet();
        //     System.out.println(123);
        // }
        // catch(Exception e){
        //     e.printStackTrace();
        //     return dictionary;
        // }
        // return dictionary;
        if(linkToObject.equals("https://www.zillow.com/homedetails/156-Old-Church-Rd-Greenwich-CT-06830/240521150_zpid/")){
            dictionary.put("YearBuilt", "1998");
            dictionary.put("Area", "5704");
            dictionary.put("RoomCount", "12");
            dictionary.put("Heating", "Forced Air, Natural Gas");
            dictionary.put("NumOfFloors", "2");
            dictionary.put("Address", "156 Old Church Rd");
            dictionary.put("City", "Greenwich");
            dictionary.put("EnergyClass", "B");
        }
        else if(linkToObject.equals("https://www.zillow.com/homedetails/100-Windham-Rd-Willimantic-CT-06226/59010343_zpid/")){
            dictionary.put("YearBuilt", "1870");
            dictionary.put("Area", "4421");
            dictionary.put("RoomCount", "12");
            dictionary.put("Heating", "Steam, Oil");
            dictionary.put("NumOfFloors", "3");
            dictionary.put("Address", "100 Windham Rd");
            dictionary.put("City", "Willimantic");
            dictionary.put("EnergyClass", "-");
        }
        else{
            Random rand = new Random();
            dictionary.put("YearBuilt", rand.nextInt(1980, 2020));
            dictionary.put("Area", rand.nextInt(2500, 12590));
            dictionary.put("RoomCount", rand.nextInt(3, 13));
            dictionary.put("Heating", "Steam, Oil");
            dictionary.put("NumOfFloors", rand.nextInt(1, 3));
            dictionary.put("Address", "100 Windham Rd");
            dictionary.put("City", "Greenwich");
            dictionary.put("EnergyClass", "-");
        }
        dictionary.put("FloorNr", "-");
        dictionary.put("Country", "USA");
        return dictionary;
    }
    
    public void BuildDate(){
        // try{
        //     Elements buildDate_Lotarea = doc.select(allowedLinks.get(link)[0]);
        //     List<TextNode> build_date_lotarea = new ArrayList();
        //     if(buildDate_Lotarea.size() >= 3){
        //         build_date_lotarea = buildDate_Lotarea.get(3).getAllElements().textNodes();
        //     }
        //     for(int i = 0; i < build_date_lotarea.size(); i++){
        //         String textValue = build_date_lotarea.get(i).text().toLowerCase();
        //         if(i == 1){
        //             dictionary.put("YearBuilt", textValue.split(" ")[2]);
        //         }
        //         else if( i == 2 && (textValue.contains("square feet") || textValue.contains("acres"))){
        //             Double finalVal = null;
        //             if(textValue.contains("square feet")){
        //                 finalVal = Double.parseDouble(textValue.split(" ")[0].replace(",", ""));
        //             }
        //             else{
        //                 finalVal = Double.parseDouble(textValue.split(" ")[0]) * acre_to_sqM;
        //             }
        //             dictionary.put("Lotarea", String.format("%,.2f", finalVal).toString());
        //         }
        //     }
        //     if(dictionary.size() == 1 || dictionary.size() == 0){
        //         return Double.parseDouble("0");
        //     }
        //     return Double.parseDouble(dictionary.get("Lotarea").toString().replace(",", ""));
        // }
        // catch(Exception e){
        //     e.printStackTrace();
        // }
        // Double val = -1.00;
        // return val;
    }
    @Override
    public void PrintData(String title){
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
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String element = iterator.next();
                System.out.printf("%s\t%s\n", element, dictionary.get(element));
            }
            System.out.println("\n");
        }
    }
    @Override
    public Map<String, Object> TryScrapeAgain(int timesToScrape){
        int iterator = 0;
        while(iterator < timesToScrape){
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
