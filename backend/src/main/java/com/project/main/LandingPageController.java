package com.project.main;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.Eval.EvalScore;
import com.Eval.PricePredictor;
import com.PromptAPI.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.Scraper.Scraper;
import java.io.IOException;

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
    public Map<String, Object> evaluatePrompt(@RequestBody Map<String, Object> data) {
        String prompt = (String) data.get("prompt");
        Map<String, String> selectedValues = (Map<String, String>) data.get("selectedValues");

        System.out.println("Prompt entered: " + prompt);
        System.out.println("Selected values: " + selectedValues);

        try {
            JsonObject answer = promptApi.getStructuredResponse(prompt);
            System.out.println("Gemini API Response: ");
            System.out.println(gson.toJson(answer));

            double predPrice = PricePredictor.getPredictPrice(answer);
            double evalScore = EvalScore.getEvalScor(answer, selectedValues);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("PredictedPrice", predPrice);
            responseMap.put("PredictedScore", evalScore);

            Gson gson = new Gson();
            Map<String, Object> geminiResponseMap = gson.fromJson(answer, new TypeToken<Map<String, Object>>() {
            }.getType());
            responseMap.put("geminiResponse", geminiResponseMap);

            return responseMap;
        } catch (IOException e) {
            return null;
        }
    }

    @RestController
    public class EvaluationController {

        @PostMapping("/evaluate/reevaluate")
        public Map<String, Object> reevaluate(@RequestBody Map<String, Object> data) {
            // Retrieve the prompt and selected values from the request body
            String prompt = (String) data.get("prompt");
            Map<String, String> newValues = (Map<String, String>) data.get("gridData");

            // Print the prompt and selected values
            System.out.println("Prompt entered: " + prompt);
            System.out.println("Grid values: " + newValues);

            // Process the data as needed (e.g., perform calculations, evaluate, etc.)

            // Return a response indicating success
            return Map.of("message", "Evaluation successful", "status", "OK");
        }
    }

    // 3 kambariu, VIlnius, Gedimino pr. 3, 3 aukstas 10 aukstu name, A++ ekonomine
    // klase, pastatymas 2023, centrinis sildymas

    // https://www.aruodas.lt/butai-vilniuje-justiniskese-kopenhagos-g-talino-modernus-komfortiskas-naujas-1-3387648/?pos=3&search_pos=16

    // https://www.zillow.com/homedetails/1111-Delsea-Dr-Westville-NJ-08093/38758443_zpid/

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
