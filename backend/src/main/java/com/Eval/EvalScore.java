package com.Eval; 

import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit; // Import TimeUnit
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvalScore { 
    private final String apiKey;
    private final String geminiUrl;
    private final OkHttpClient client;
    private final Gson gson;

    // Constants for API parameters
    private static final double TEMPERATURE = 0.2;
    private static final int MAX_OUTPUT_TOKENS = 10; 

    // Regex to find the first integer in the response
    private static final Pattern SCORE_PATTERN = Pattern.compile("\\b(\\d+)\\b");

    public EvalScore() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("API_HOME_INTEL"); // Using the same key name
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            System.err.println("ERROR: API_HOME_INTEL environment variable not set.");
            // logger.error("API_HOME_INTEL environment variable not set.");
            throw new IllegalArgumentException("API Key is missing.");
        }
        this.geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + this.apiKey;
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    public EvalScore(String apiKey, OkHttpClient client, Gson gson) {
        this.apiKey = apiKey;
        this.geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + this.apiKey;
        this.client = client;
        this.gson = gson;
    }

    public static double getEvalScor(JsonObject inputData, Map<String, String> selectedValues) {
        EvalScore evaluator = new EvalScore(); // Create instance
        try {
            String scoreText = evaluator.getEvaluationScoreInternal(inputData, selectedValues); // Call internal method
            double score = extractScoreFromText(scoreText); // Use renamed extraction method

            if (score < 9) // magic number
                score += 2;

            // System.out.println("Raw API Response Text for Score: " + scoreText);

            System.out.println("Eval score: " + (score < 1 ? "Invalid/Unknown" : score));
            return score; // extractScore returns -1 on failure/invalid
        } catch (IOException e) {
            System.err.println("Error communicating with API: " + e.getMessage());
            // logger.error("Error communicating with API", e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during evaluation: " + e.getMessage());
            // logger.error("An unexpected error occurred during evaluation", e);
        }
        return -1; // Return -1 for any error
    }

    // Internal instance method to perform the evaluation 
    private String getEvaluationScoreInternal(JsonObject inputData, Map<String, String> selectedValues) throws IOException {

        String systemInstruction = """
            **CONTEXT:**
            You are a highly experienced real estate analyst specializing in the **USA property market**. Your task is to evaluate a property based on the provided data and return a single score reflecting its overall quality and market value appeal. Consider typical factors important in the US market like location (zip code, neighborhood desirability), square footage, bedrooms/bathrooms count, age/condition, school districts (if inferable), and common amenities.

            **INPUT FORMAT:**
            1.  **JSON Data:** Contains primary details about the property. Field names and values are expected in **English** (e.g., 'address', 'zip_code', 'area_sqft', 'bedrooms', 'bathrooms', 'year_built', 'price', 'property_type', 'condition').
            2.  **Selected Values:** A map indicating user preference for specific features/amenities (keys expected in English). The values indicate importance:
                *   'g' (green): Very important. Give significant positive weight if the property has this feature or characteristic.
                *   'y' (yellow): Somewhat important. Give moderate positive weight.
                *   'r' (red): Not important. Ignore this feature or give it minimal/no weight.

            **TASK:**
            1.  Analyze the JSON data thoroughly. Pay attention to details relevant to the US market.
            2.  Consider the user's preferences specified in "Selected Values". Features marked 'g' should strongly influence the score upwards if present/good, 'y' moderately, and 'r' minimally or not at all.
            3.  Use your expert knowledge of the **USA real estate market**, including regional variations in value drivers, to interpret the data.
            4.  Determine an overall quality and value score for the property on a scale of 1 to 10, where 1 is very poor and 10 is excellent.

            **OUTPUT FORMAT:**
            Return ONLY a single integer between 1 and 10, inclusive.
            *   NO explanation.
            *   NO additional text.
            *   NO currency symbols or units.
            *   Just the number.

            **EXAMPLE 1 (USA Focus):**
            Input JSON: {"address": "123 Main St, Anytown, CA 90210", "area_sqft": 2200, "bedrooms": 4, "bathrooms": 2.5, "year_built": 1995, "condition": "Updated", "property_type": "Single Family", "price": 950000}
            Selected Values: {'Good school district': 'g', 'Updated kitchen': 'g', 'Swimming pool': 'y', 'Needs landscaping': 'r'}
            Output: 9

            **EXAMPLE 2 (USA Focus):**
            Input JSON: {"address": "456 Oak Ave, Sometown, OH 44101", "area_sqft": 950, "bedrooms": 2, "bathrooms": 1, "year_built": 1962, "condition": "Needs renovation", "property_type": "Condo", "price": 150000}
            Selected Values: {'Low price': 'g', 'Close to downtown': 'y', 'Garage parking': 'r', 'Requires significant updates': 'y'}
            Output: 4
            """;

        String prompt = systemInstruction +
                "\n\n**PROPERTY DATA FOR EVALUATION:**\n" +
                "JSON Input: " + inputData.toString() + "\n" +
                "Selected Values (User Preferences): " + selectedValues.toString() + "\n\n" +
                "**SCORE (1-10 only):**";

        // --- API Request Body Construction 
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", TEMPERATURE);
        generationConfig.put("maxOutputTokens", MAX_OUTPUT_TOKENS);
        generationConfig.put("topP", 0.95);
        generationConfig.put("topK", 40);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(message));
        requestBody.put("generationConfig", generationConfig);

        Request request = new Request.Builder()
                .url(this.geminiUrl)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.get("application/json; charset=utf-8")))
                .build();

        // System.out.println("Sending Request Body: " + gson.toJson(requestBody));

        try (Response response = this.client.newCall(request).execute()) {
            String responseBodyString = response.body() != null ? response.body().string() : null;

            // System.out.println("Raw API Response: HTTP " + response.code() + " Body: " + responseBodyString);

            if (!response.isSuccessful() || responseBodyString == null) {
                System.err.println("API request failed: HTTP " + response.code() + " - " + response.message());
                if (responseBodyString != null) {
                    System.err.println("Response body: " + responseBodyString);
                }
                // logger.error("API request failed: HTTP {} - {}. Body: {}", response.code(), response.message(), responseBodyString);
                throw new IOException("Unexpected API response code: " + response.code());
            }

            return parseResponse(responseBodyString); // Use the same robust parser
        }
    }

    // Response Parsing Logic
    private String parseResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            // Check for promptFeedback (potential blocking)
            if (jsonObject.has("promptFeedback")) {
                JsonObject feedback = jsonObject.getAsJsonObject("promptFeedback");
                if (feedback.has("blockReason")) {
                    String reason = feedback.get("blockReason").getAsString();
                     System.err.println("Warning: Prompt potentially blocked. Reason: " + reason);
                     // logger.warn("Prompt potentially blocked. Reason: {}", reason);
                     if (!"SAFETY".equalsIgnoreCase(reason)) {
                        // return null;
                     }
                }
            }

            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                 System.err.println("No candidates found in API response.");
                 // logger.error("No candidates found in API response: {}", jsonResponse);
                return null;
            }

            JsonObject candidate = candidates.get(0).getAsJsonObject();

            // Check candidate finish reason
             if (candidate.has("finishReason")) {
                String finishReason = candidate.get("finishReason").getAsString();
                if (!"STOP".equalsIgnoreCase(finishReason) && !"MAX_TOKENS".equalsIgnoreCase(finishReason) ) {
                    // MAX_TOKENS is acceptable if we got the number, but log others
                    System.err.println("Warning: Candidate finish reason was: " + finishReason);
                    // logger.warn("Candidate finish reason was not STOP: {}", finishReason);
                }
             } else {
                 System.err.println("Warning: Candidate finish reason missing.");
                 // logger.warn("Candidate finish reason missing.");
             }

            JsonObject content = candidate.getAsJsonObject("content");
            if (content == null) { /* ... error handling ... */ System.err.println("No content found."); return null; }

            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.isEmpty()) { /* ... error handling ... */ System.err.println("No parts found."); return null; }

            JsonObject part = parts.get(0).getAsJsonObject();
            if (!part.has("text")) { /* ... error handling ... */ System.err.println("No text found in part."); return null; }

            String rawText = part.get("text").getAsString();
            // System.out.println("Extracted text from API: '" + rawText + "'"); // Debugging
            return rawText.trim();

        } catch (JsonSyntaxException | IllegalStateException | NullPointerException e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            System.err.println("Malformed JSON was: " + jsonResponse);
            // logger.error("Error parsing JSON response: {}", jsonResponse, e);
            return null;
        }
    }

    // Score Extraction and Validation
    private static double extractScoreFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.err.println("Error extracting score: API response text is null or empty.");
            return -1;
        }

        Matcher matcher = SCORE_PATTERN.matcher(text.trim());

        if (matcher.find()) {
            try {
                String scoreStr = matcher.group(1);
                int score = Integer.parseInt(scoreStr);

                if (score >= 1 && score <= 10) {
                    return score; // Return valid score as double
                } else {
                    System.err.println("Error extracting score: Extracted score (" + score + ") is outside the valid range (1-10). Text was: '" + text + "'");
                    return -1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error extracting score: Failed to parse matched digits '" + matcher.group(1) + "' as integer. Text was: '" + text + "'");
                return -1;
            }
        } else {
            System.err.println("Error extracting score: Could not find a valid integer score in the response text. Text was: '" + text + "'");
            return -1;
        }
    }
}
