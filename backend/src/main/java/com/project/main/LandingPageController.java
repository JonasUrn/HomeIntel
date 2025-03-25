package com.project.main;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import com.PromptAPI.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.*;

import com.Scraper.Scraper;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class LandingPageController {
    private PromptService promptApi = new PromptService();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public LandingPageController() {
    }

    @GetMapping("/")
    public String initial() {
        System.out.println("Hello world");
        return "Hello World";
    }

    @PostMapping("/evaluate/link")
    public String evaluateLink(@RequestBody Map<String, Object> data) {
        String link = (String) data.get("link");
        Map<String, String> selectedValues = (Map<String, String>) data.get("selectedValues");

        System.out.println("Link entered: " + link);
        System.out.println(selectedValues);
        return link;
    }

    @PostMapping("/evaluate/prompt")
    public JsonObject evaluatePrompt(@RequestBody Map<String, Object> data) {
        String prompt = (String) data.get("prompt");
        Map<String, String> selectedValues = (Map<String, String>) data.get("selectedValues");

        System.out.println("Prompt entered: " + prompt);
        System.out.println("Selected values: " + selectedValues);

        try {
            // Call the Gemini API using PromptService
            JsonObject answer = promptApi.getStructuredResponse(prompt);
            System.out.println("Gemini API Response: ");
            System.out.println(gson.toJson(answer));
            return answer;
        } catch (IOException e) {
            return null;
        }
    }

    // Describe a 3-bedroom apartament in vilnius centre with A economic class in a
    // three floor house when the apartamaent is in the 2nd floor.

    @PostMapping("/evaluate/scraper")
    public Dictionary<String, String> getScraper(@RequestBody Map<String, Object> linkToRealEstate)
            throws InterruptedException {
        // By getting results, the scraper gets this.doc = null and in mpst cases it
        // throws TimeOutException
        Scraper scraper = new Scraper(linkToRealEstate.get("data").toString());
        Dictionary<String, String> realEstateData = new Hashtable<>();
        try {
            realEstateData = scraper.GetResults();
            Thread.sleep(500);
            if (realEstateData.size() > 0) {
                Enumeration<String> keys = realEstateData.keys();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement().toString();
                    System.out.printf("\n%s\t%s", key, realEstateData.get(key));
                }
                return realEstateData;
            } else {
                System.out.printf("Didn't found eny data on link: %s", linkToRealEstate);
            }
        } catch (Error e) {
            System.out.println("Error in method getScraper() backend");
        }
        return realEstateData;
    }
}
