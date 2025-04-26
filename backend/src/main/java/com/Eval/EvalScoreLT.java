package com.Eval;

import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvalScoreLT {
    private final String apiKey;
    private final String geminiUrl;
    private final OkHttpClient client;
    private final Gson gson;

    // Constants for API parameters
    private static final double TEMPERATURE = 0.2;
    private static final int MAX_OUTPUT_TOKENS = 10;

    // Regex to find the first integer in the response
    private static final Pattern SCORE_PATTERN = Pattern.compile("\\b(\\d+)\\b");

    public EvalScoreLT() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load(); // More robust loading
        this.apiKey = dotenv.get("API_HOME_INTEL");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            System.err.println("ERROR: API_HOME_INTEL environment variable not set.");
            throw new IllegalArgumentException("API Key is missing.");
        }
        this.geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + this.apiKey;
        // It's generally better to share OkHttpClient instances if possible
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // Example: Add timeouts
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

     // Constructor allowing injection of dependencies (better for testing)
     public EvalScoreLT(String apiKey, OkHttpClient client, Gson gson) {
        this.apiKey = apiKey;
        this.geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + this.apiKey;
        this.client = client;
        this.gson = gson;
    }


    // Static helper method remains similar but uses the instance internally
    public static double getEvalScorLT(JsonObject inputData, Map<String, String> selectedValues) {
        EvalScoreLT evaluator = new EvalScoreLT();
        try {
            String scoreText = evaluator.getEvaluationScoreLTInternal(inputData, selectedValues);
            double score = extractScoreFromTextLT(scoreText);
            
            if (score < 9) // magic number
                score += 2;
                
            // System.out.println("Raw API Response Text: " + scoreText); // Keep for debugging if needed
            System.out.println("Eval score: " + (score < 1 ? "Invalid/Unknown" : score)); // Use < 1 check now
            return score; // extractScore returns -1 on failure
        } catch (IOException e) {
            System.err.println("Error communicating with API: " + e.getMessage());
            // logger.error("Error communicating with API", e); // Using SLF4J example
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            // logger.error("An unexpected error occurred", e);
        }
        return -1; // Return -1 for any error
    }

    private String getEvaluationScoreLTInternal(JsonObject inputData, Map<String, String> selectedValues) throws IOException {

        String systemInstruction = """
            **CONTEXT:**
            You are a highly experienced real estate analyst specializing in the Lithuanian property market. Your task is to evaluate a property based on the provided data and return a single score.

            **INPUT FORMAT:**
            1.  **JSON Data:** Contains primary details about the property. Most field names and values are in Lithuanian (e.g., 'plotas' means area, 'kaina' means price, 'kambsk' means room count, 'vieta' means location/address).
            2.  **Selected Values:** A map indicating user preference for specific features/amenities. The keys are feature names (in Lithuanian), and the values indicate importance:
                *   'g' (green): Very important. Give significant positive weight.
                *   'y' (yellow): Somewhat important. Give moderate positive weight.
                *   'r' (red): Not important. Ignore this feature or give it minimal weight.

            **TASK:**
            1.  Analyze the JSON data thoroughly.
            2.  Consider the user's preferences specified in "Selected Values". Features marked 'g' should strongly influence the score upwards if present/good, 'y' moderately, and 'r' minimally or not at all.
            3.  Use your expert knowledge of the Lithuanian real estate market (typical values, desirable locations, construction quality standards, etc.) to interpret the data.
            4.  Determine an overall quality and value score for the property on a scale of 1 to 10, where 1 is very poor and 10 is excellent.

            **OUTPUT FORMAT:**
            Return ONLY a single integer between 1 and 10, inclusive.
            *   NO explanation.
            *   NO additional text.
            *   NO units or symbols.
            *   Just the number.

            **EXAMPLE 1:**
            Input JSON: { ... some good property data ... }
            Selected Values: {'Geras susisiekimas': 'g', 'Arti parko': 'y', 'Reikalingas remontas': 'r'}
            Output: 8

            **EXAMPLE 2:**
            Input JSON: { ... some poor property data ... }
            Selected Values: {'Ramioje vietoje': 'g', 'Nauja statyba': 'g'}
            Output: 3
            """;

        String prompt = systemInstruction +
                "\n\n**PROPERTY DATA FOR EVALUATION:**\n" + 
                "JSON Input: " + inputData.toString() + "\n" +
                "Selected Values (User Preferences): " + selectedValues.toString() + "\n\n" +
                "**SCORE (1-10 only):**"; 


        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", TEMPERATURE);
        generationConfig.put("maxOutputTokens", MAX_OUTPUT_TOKENS);
        generationConfig.put("topP", 0.9);
        generationConfig.put("topK", 40); 

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(message));
        requestBody.put("generationConfig", generationConfig);

        Request request = new Request.Builder()
                .url(this.geminiUrl)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.get("application/json; charset=utf-8"))) // Specify charset
                .build();

        // System.out.println("Sending Request Body: " + gson.toJson(requestBody));

        try (Response response = this.client.newCall(request).execute()) {
            String responseBodyString = response.body() != null ? response.body().string() : null; // Handle null body

            // System.out.println("Raw API Response: HTTP " + response.code() + " Body: " + responseBodyString);

            if (!response.isSuccessful() || responseBodyString == null) {
                System.err.println("API request failed: HTTP " + response.code() + " - " + response.message());
                System.err.println("Response body: " + responseBodyString);
                 // logger.error("API request failed: HTTP {} - {}. Body: {}", response.code(), response.message(), responseBodyString);
                throw new IOException("Unexpected API response code: " + response.code());
            }

            return parseResponse(responseBodyString);
        }
    }

    // Enhanced parsing logic
    private String parseResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            // Check for promptFeedback first (indicates potential blocking)
            if (jsonObject.has("promptFeedback")) {
                JsonObject feedback = jsonObject.getAsJsonObject("promptFeedback");
                if (feedback.has("blockReason")) {
                     System.err.println("Warning: Prompt potentially blocked. Reason: " + feedback.get("blockReason").getAsString());
                     // logger.warn("Prompt potentially blocked. Reason: {}", feedback.get("blockReason").getAsString());
                }
            }


            JsonArray candidates = jsonObject.getAsJsonArray("candidates");

            if (candidates == null || candidates.isEmpty()) {
                 System.err.println("No candidates found in API response.");
                 // logger.error("No candidates found in API response: {}", jsonResponse);
                return null; // No candidates
            }

            JsonObject candidate = candidates.get(0).getAsJsonObject(); // Get the first candidate

            // Check candidate finish reason
             if (candidate.has("finishReason") && !"STOP".equals(candidate.get("finishReason").getAsString())) {
                System.err.println("Warning: Candidate finish reason was not STOP: " + candidate.get("finishReason").getAsString());
                // logger.warn("Candidate finish reason was not STOP: {}", candidate.get("finishReason").getAsString());
             }


            JsonObject content = candidate.getAsJsonObject("content");
            if (content == null) {
                 System.err.println("No content found in candidate.");
                 // logger.error("No content found in candidate: {}", candidate.toString());
                return null;
            }

            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.isEmpty()) {
                 System.err.println("No parts found in content.");
                 // logger.error("No parts found in content: {}", content.toString());
                return null;
            }

            JsonObject part = parts.get(0).getAsJsonObject();
            if (!part.has("text")) {
                System.err.println("No text found in part.");
                // logger.error("No text found in part: {}", part.toString());
                return null;
            }

            String rawText = part.get("text").getAsString();
            // System.out.println("Extracted text from API: '" + rawText + "'"); // Debugging
            return rawText.trim();

        } catch (JsonSyntaxException | IllegalStateException | NullPointerException e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            System.err.println("Malformed JSON: " + jsonResponse);
            // logger.error("Error parsing JSON response: {}", jsonResponse, e);
            return null; // Indicate parsing failure
        }
    }

    // Helper method to extract and validate the score from the response text
    private static double extractScoreFromTextLT(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.err.println("Error extracting score: API response text is null or empty.");
            return -1;
        }

        Matcher matcher = SCORE_PATTERN.matcher(text.trim());

        if (matcher.find()) {
            try {
                // Extract the first number found
                String scoreStr = matcher.group(1);
                int score = Integer.parseInt(scoreStr);

                // Validate the score is within the expected range
                if (score >= 1 && score <= 10) {
                    return score; // Return as double for consistency with original method signature
                } else {
                    System.err.println("Error extracting score: Extracted score (" + score + ") is outside the valid range (1-10). Text was: '" + text + "'");
                    return -1; // Score out of range
                }
            } catch (NumberFormatException e) {
                // This might happen if regex matches something huge, though unlikely with \d+
                System.err.println("Error extracting score: Failed to parse the matched digits as an integer. Text was: '" + text + "'");
                return -1;
            }
        } else {
            // If no number is found at all
            System.err.println("Error extracting score: Could not find a valid integer score in the response text. Text was: '" + text + "'");
            return -1; // Score not found
        }
    }
}
