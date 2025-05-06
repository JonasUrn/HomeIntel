package com.Eval; // Assuming same package

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

public class PricePredictor {
    private final String apiKey;
    private final String geminiUrl;
    private final OkHttpClient client;
    private final Gson gson;

    private static final String MODEL_NAME = "gemini-1.5-flash";
    private static final double TEMPERATURE = 0.0;
    private static final int MAX_OUTPUT_TOKENS = 20;

    private static final Pattern PRICE_PATTERN = Pattern.compile("\\$? *([\\d,]+(?:\\.\\d+)?)");

    public PricePredictor() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("API_HOME_INTEL");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            String errorMsg = "ERROR: API_HOME_INTEL environment variable not set.";
            System.err.println(errorMsg);
            // logger.error(errorMsg);
            throw new IllegalArgumentException("API Key is missing.");
        }
        this.geminiUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", MODEL_NAME, this.apiKey);

        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS) // Allow slightly longer for potential complex analysis
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    public PricePredictor(String apiKey, OkHttpClient client, Gson gson, String modelName) {
        this.apiKey = apiKey;
        this.geminiUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", modelName, this.apiKey);
        this.client = client;
        this.gson = gson;
    }

    public static double getPredictPrice(JsonObject propertyData) {
        if (propertyData == null) {
            System.err.println("Input property data cannot be null.");
            return -1;
        }

        // Create a deep copy to avoid modifying the original object
        JsonObject inputData = propertyData.deepCopy();

        // Remove Price field if it exists - crucial for prediction task
        inputData.remove("Price"); // No exception thrown, returns removed element or null
        inputData.remove("price"); // Also check for lowercase version

        PricePredictor predictor = new PricePredictor(); // Or use dependency injection / Singleton
        try {
            String predictedPriceText = predictor.getPredictedPriceInternal(inputData);
            double predictedPrice = extractPriceFromText(predictedPriceText);

            // System.out.println("Raw API Response Text for Price: " + predictedPriceText);
            System.out.println("Predicted Price: " + (predictedPrice < 0 ? "Unknown/Invalid" : String.format("%.2f", predictedPrice))); // Format output
            return predictedPrice; // extractPrice returns -1 on failure
        } catch (IOException e) {
            System.err.println("Error communicating with Prediction API: " + e.getMessage());
            // logger.error("Error communicating with Prediction API", e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during price prediction: " + e.getMessage());
            // logger.error("An unexpected error occurred during price prediction", e);
        }
        return -1; // Return -1 for any error
    }

    // Internal instance method
    private String getPredictedPriceInternal(JsonObject inputData) throws IOException {

        String systemInstruction = """
            **CONTEXT:**
            You are an expert USA real estate market analyst and appraiser AI. Your sole task is to predict the fair market value (price) in USD for a given property based *only* on the provided JSON data.

            **INPUT:**
            A JSON object containing property details. Key fields influencing price include (but are not limited to):
            *   Location: `address`, `city`, `state`, `zip_code` (CRITICAL - use ZIP code heavily)
            *   Size: `area_sqft` or similar (e.g., `living_area`)
            *   Structure: `bedrooms`, `bathrooms` (often `beds`, `baths`), `stories`
            *   Age & Condition: `year_built`, `condition` (e.g., "Excellent", "Good", "Needs TLC", "Fixer-upper")
            *   Type: `property_type` (e.g., "Single Family", "Condo", "Townhouse", "Multi-Family")
            *   Specific Features: `lot_size_acres`, `garage_spaces`, `pool` (often boolean), `hoa_monthly`

            **TASK:**
            1.  Analyze the provided JSON data meticulously.
            2.  Leverage your extensive knowledge of **USA real estate market trends**, regional price variations, neighborhood values associated with **ZIP codes**, and the typical impact of property characteristics (size, condition, age, type, features) on price.
            3.  Predict the most likely fair market price for this property in **USD**.

            **OUTPUT FORMAT:**
            Return ONLY a single **numerical value** representing the predicted price.
            *   NO dollar signs ($).
            *   NO commas (,).
            *   NO explanatory text.
            *   NO words like "USD" or "dollars".
            *   Just the number (integer preferred, decimals allowed if necessary).

            **EXAMPLE 1:**
            Input JSON: {"address": "100 Elm St", "city": "Springfield", "state": "IL", "zip_code": "62704", "area_sqft": 1600, "beds": 3, "baths": 2, "year_built": 1985, "condition": "Good", "property_type": "Single Family", "garage_spaces": 2}
            Output: 215000

            **EXAMPLE 2:**
            Input JSON: {"address": "555 Bay Dr Apt 3B", "city": "Miami", "state": "FL", "zip_code": "33139", "area_sqft": 950, "beds": 1, "baths": 1.5, "year_built": 2005, "condition": "Excellent", "property_type": "Condo", "hoa_monthly": 650, "waterfront": true}
            Output: 780000

            **EXAMPLE 3:**
            Input JSON: {"address": "23 Industrial Way", "city": "Cleveland", "state": "OH", "zip_code": "44115", "area_sqft": 2100, "beds": 4, "baths": 2, "year_built": 1950, "condition": "Fixer-upper", "property_type": "Multi-Family", "lot_size_acres": 0.15}
            Output: 110000
            """; // End Text Block

        String prompt = systemInstruction +
                "\n\n**PROPERTY DETAILS FOR PRICE PREDICTION:**\n" +
                "JSON Input: " + inputData.toString() + "\n\n" +
                "**PREDICTED PRICE (USD Number Only):**"; // Final cue

        // --- API Request Body Construction ---
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", TEMPERATURE);
        generationConfig.put("maxOutputTokens", MAX_OUTPUT_TOKENS);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(message));
        requestBody.put("generationConfig", generationConfig);

        // --- API Call ---
        Request request = new Request.Builder()
                .url(this.geminiUrl)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.get("application/json; charset=utf-8")))
                .build();

        // System.out.println("Sending Price Prediction Request Body: " + gson.toJson(requestBody));

        try (Response response = this.client.newCall(request).execute()) {
            String responseBodyString = response.body() != null ? response.body().string() : null;
            // System.out.println("Raw Price Prediction API Response: HTTP " + response.code() + " Body: " + responseBodyString);

            if (!response.isSuccessful() || responseBodyString == null) {
                 String errorDetails = "API request failed: HTTP " + response.code() + " - " + response.message();
                 if(responseBodyString != null) errorDetails += "\nResponse body: " + responseBodyString;
                 System.err.println(errorDetails);
                 // logger.error(errorDetails);
                throw new IOException("Unexpected API response code: " + response.code());
            }

            return parseResponse(responseBodyString); // Use robust parser
        }
    }

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
                }
            }

            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) { /* ... error handling ... */ System.err.println("No candidates found."); return null; }

            JsonObject candidate = candidates.get(0).getAsJsonObject();

            // Check candidate finish reason
            if (candidate.has("finishReason")) {
                String finishReason = candidate.get("finishReason").getAsString();
                // STOP or MAX_TOKENS are generally acceptable here
                if (!"STOP".equalsIgnoreCase(finishReason) && !"MAX_TOKENS".equalsIgnoreCase(finishReason)) {
                     System.err.println("Warning: Candidate finish reason was: " + finishReason);
                     // logger.warn("Candidate finish reason was not STOP/MAX_TOKENS: {}", finishReason);
                }
            } else { /* ... warning ... */ System.err.println("Warning: Candidate finish reason missing."); }


            JsonObject content = candidate.getAsJsonObject("content");
            if (content == null) { /* ... error handling ... */ System.err.println("No content found in candidate."); return null; }

            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.isEmpty()) { /* ... error handling ... */ System.err.println("No parts found in content."); return null; }

            JsonObject part = parts.get(0).getAsJsonObject();
            if (!part.has("text")) { /* ... error handling ... */ System.err.println("No text found in part."); return null; }

            String rawText = part.get("text").getAsString();
            return rawText.trim();

        } catch (JsonSyntaxException | IllegalStateException | NullPointerException e) {
            System.err.println("Error parsing JSON response for price prediction: " + e.getMessage());
            System.err.println("Malformed JSON was: " + jsonResponse);
            // logger.error("Error parsing JSON response for price prediction: {}", jsonResponse, e);
            return null;
        }
    }

    private static double extractPriceFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.err.println("Error extracting price: API response text is null or empty.");
            return -1;
        }

        String cleanedText = text.trim();
        Matcher matcher = PRICE_PATTERN.matcher(cleanedText);

        if (matcher.find()) {
            try {
                // Get the captured numeric part (group 1)
                String priceStr = matcher.group(1);
                // Remove commas before parsing
                priceStr = priceStr.replace(",", "");
                // Attempt to parse as double
                return Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                System.err.println("Error extracting price: Failed to parse extracted numeric string '" + matcher.group(1) + "' after cleaning. Original text: '" + text + "'. Error: " + e.getMessage());
                return -1;
            } catch (IllegalStateException | IndexOutOfBoundsException e){
                 System.err.println("Error extracting price: Regex matching error on text: '" + text + "'. Error: " + e.getMessage());
                 return -1;
            }
        } else {
            // Handle cases where the regex doesn't match at all
            System.err.println("Error extracting price: Could not find a valid numerical price pattern in the response text. Text was: '" + text + "'");
            return -1;
        }
    }
    
}