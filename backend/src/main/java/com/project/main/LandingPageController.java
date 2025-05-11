package com.project.main;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    public Map<String, Object> evaluateLink(@RequestBody Map<String, Object> data) {
        String link = (String) data.get("data");
        Map<String, String> selectedValues = (Map<String, String>) data.get("selectedValues");
        Scraper scraper = new Scraper(link);
        Map<String, Object> rez = scraper.GetResults();
        Gson gson_ = new Gson();
        JsonObject answer = gson_.toJsonTree(rez).getAsJsonObject();

        System.out.println("Scraper results: ");
        System.out.println(gson.toJson(answer));

        double predPrice = PricePredictor.getPredictPrice(answer);
        JsonObject evalObject = answer.deepCopy();
        evalObject.addProperty("Predicted Price", predPrice);
        double evalScore = EvalScore.getEvalScor(evalObject, selectedValues);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("PredictedPrice", predPrice);
        responseMap.put("PredictedScore", evalScore);

        Gson gson = new Gson();
        Map<String, Object> geminiResponseMap_ = gson.fromJson(answer, new TypeToken<Map<String, Object>>() {
        }.getType());
        responseMap.put("geminiResponse", geminiResponseMap_);

        System.out.printf("Response map: %s\t\nGemini answer: %s\n", responseMap, geminiResponseMap_);
        return responseMap;

        // System.out.println("Link entered: " + link);
        // System.out.println(selectedValues);
        // return null;
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
            JsonObject evalObject = answer.deepCopy();
            evalObject.addProperty("Predicted Price", predPrice);
            double evalScore = EvalScore.getEvalScor(evalObject, selectedValues);

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
            String originalPrompt = (String) data.get("prompt");
            Map<String, Object> newValues = (Map<String, Object>) data.get("gridData");

            Object priceObject = data.get("price");
            String price = priceObject != null ? String.valueOf(priceObject) : "";

            System.out.println("Price: " + price);

            // Generate a new prompt for Gemini based on the grid data
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Property Reevaluation Request\n\n");
            promptBuilder.append("I need to reevaluate a property with the following updated characteristics:\n\n");

            // Add each property characteristic from gridData
            for (Map.Entry<String, Object> entry : newValues.entrySet()) {
                String value = String.valueOf(entry.getValue());
                promptBuilder.append(entry.getKey()).append(": ").append(value).append("\n");
            }

            // Add price information if available
            if (!price.isEmpty()) {
                promptBuilder.append("Current Price: ").append(price).append("\n");
            }

            // Add any additional context from the original prompt
            if (originalPrompt != null && !originalPrompt.isEmpty()) {
                promptBuilder.append("\nAdditional Information:\n").append(originalPrompt);
            }

            // Instruct Gemini on what we need
            promptBuilder.append("\n\nPlease analyze these property details and provide a structured evaluation " +
                    "including a fair market value estimation and property assessment metrics. " +
                    "Also include a 'Price' field with your estimated fair market value.");

            String generatedPrompt = promptBuilder.toString();
            System.out.println("Generated Gemini Prompt: " + generatedPrompt);

            try {
                // Call Gemini API using the same method as the evaluation function
                JsonObject answer = promptApi.getStructuredResponse(generatedPrompt);
                System.out.println("Gemini API Response: ");
                System.out.println(gson.toJson(answer));

                // Check if Gemini returned a Price property
                String geminiPrice = price; // Default to original price
                if (answer.has("Price")) {
                    try {
                        // Try to get the price as a string
                        geminiPrice = answer.get("Price").getAsString();
                        System.out.println("Using Gemini provided price: " + geminiPrice);
                    } catch (Exception e) {
                        System.out.println("Error extracting Gemini price, using original price: " + e.getMessage());
                    }
                } else {
                    System.out.println("Gemini did not provide a Price property, using original price: " + price);
                }

                // Create the reevalObject with the original approach
                JsonObject reevalObject = new JsonObject();
                for (Map.Entry<String, Object> entry : newValues.entrySet()) {
                    String value = String.valueOf(entry.getValue());
                    reevalObject.addProperty(entry.getKey(), value);
                }

                // Use the Gemini price if available, otherwise use the original price
                reevalObject.addProperty("Price", geminiPrice);
                reevalObject.addProperty("Extra information", originalPrompt);

                // Calculate predicted price from Gemini response
                double predPrice = PricePredictor.getPredictPrice(answer);

                // Keep the original evaluation scoring but with potentially updated price
                Map<String, String> none = new HashMap<>();
                double evalScore = EvalScore.getEvalScor(reevalObject, none);

                System.out.println("Grid values: " + newValues);

                // Return enhanced response with Gemini insights
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Evaluation successful");
                responseMap.put("status", "OK");
                responseMap.put("PredictedPrice", predPrice);
                responseMap.put("PredictedScore", evalScore);
                responseMap.put("UsedPrice", geminiPrice);

                // Convert Gemini response to Map for JSON serialization
                Gson gson = new Gson();
                Map<String, Object> geminiResponseMap = gson.fromJson(answer, new TypeToken<Map<String, Object>>() {
                }.getType());
                responseMap.put("geminiResponse", geminiResponseMap);

                return responseMap;
            } catch (IOException e) {
                System.err.println("Error calling Gemini API: " + e.getMessage());
                return Map.of("message", "Evaluation failed", "status", "ERROR", "error", e.getMessage());
            }
        }
    }

    // 3 kambariu, VIlnius, Gedimino pr. 3, 3 aukstas 10 aukstu name, A++ ekonomine
    // klase, pastatymas 2023, centrinis sildymas, kaina 500000

    // https://www.aruodas.lt/butai-vilniuje-justiniskese-kopenhagos-g-talino-modernus-komfortiskas-naujas-1-3387648/?pos=3&search_pos=16

    // https://www.zillow.com/homedetails/1111-Delsea-Dr-Westville-NJ-08093/38758443_zpid/

    @PostMapping("/evaluate/scraper")
    public Map<String, Object> getScraper(@RequestBody Map<String, Object> linkToRealEstate)
            throws InterruptedException {
        // By getting results, the scraper gets this.doc = null and in mpst cases it
        // throws TimeOutException
        Scraper scraper = new Scraper(linkToRealEstate.get("data").toString());
        Map<String, Object> realEstateData = new Hashtable<>();
        try {
            realEstateData = scraper.GetResults();
            Thread.sleep(500);
            if (realEstateData.size() > 0) {
                Set<String> keys = realEstateData.keySet();
                Iterator<String> iter = keys.iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    System.out.printf("\n%s\t%s", key, (String) realEstateData.get(key));
                }
                return realEstateData;
            } else {
                System.out.printf("Didn't found eny data on link: %s", linkToRealEstate);
            }
        } catch (Error e) {
            System.out.println("Error in method getScraper() backend");
        }
        System.out.printf("Got data in getScraper(): %s", realEstateData);
        return realEstateData;
    }
}
