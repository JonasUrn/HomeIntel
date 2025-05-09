package com.Eval;

import com.google.gson.*;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PricePredictor {

    private final String apiKey;
    private final String geminiUrl;
    private final OkHttpClient client;
    private final Gson gson;

    // CHOOSE THE NEWEST MODEL YOU WANT TO USE:
    private static final String MODEL_NAME = "gemini-1.5-flash-latest"; // Faster and more cost-effective

    private static final double TEMPERATURE = 0.0;
    private static final int MAX_OUTPUT_TOKENS = 20; // Keep this low if you only expect a number

    private static final Pattern PRICE_PATTERN = Pattern.compile("\\$? *([\\d,]+(?:\\.\\d+)?)");

    public PricePredictor() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("API_HOME_INTEL");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            String errorMsg = "ERROR: API_HOME_INTEL environment variable not set.";
            System.err.println(errorMsg);
            throw new IllegalArgumentException("API Key is missing.");
        }

        // CORRECTED URL for Generative Language API
        this.geminiUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", MODEL_NAME, this.apiKey);

        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    // This constructor is already correctly formatted for Generative Language API if modelName is a valid Gemini model
    public PricePredictor(String apiKey, OkHttpClient client, Gson gson, String modelName) {
        this.apiKey = apiKey;
        // Ensure the modelName passed here is a valid Gemini model like "gemini-1.5-pro-latest"
        this.geminiUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", modelName, this.apiKey);
        this.client = client;
        this.gson = gson;
    }

    public static double getPredictPrice(JsonObject propertyData) {
        if (propertyData == null) {
            System.err.println("Input property data cannot be null.");
            return -1;
        }

        JsonObject inputData = propertyData.deepCopy();
        inputData.remove("Price");
        inputData.remove("price");

        PricePredictor predictor = new PricePredictor();
        try {
            String predictedPriceText = predictor.getPredictedPriceInternal(inputData);
            if (predictedPriceText == null) { // Check if parsing response itself failed
                System.err.println("Failed to get a valid response text from the API.");
                return -1;
            }
            double predictedPrice = extractPriceFromText(predictedPriceText);

            System.out.println("Predicted Price: " + (predictedPrice < 0 ? "Unknown/Invalid" : String.format("%.2f", predictedPrice)));
            return predictedPrice;
        } catch (IOException e) {
            System.err.println("Error communicating with Prediction API: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during price prediction: " + e.getMessage());
             e.printStackTrace(); // Good for debugging
        }
        return -1;
    }

    private String getPredictedPriceInternal(JsonObject inputData) throws IOException {
        String systemInstruction = """
            **CONTEXT:**
            You are an expert real estate market analyst and appraiser AI. Your sole task is to predict the fair market value (price) for a given property based *only* on the provided JSON data.

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
            """;

        String prompt = systemInstruction +
                "\n\n**PROPERTY DETAILS FOR PRICE PREDICTION:**\n" +
                "JSON Input: " + inputData.toString() + "\n\n" +
                "**PREDICTED PRICE (USD Number Only):**";

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", TEMPERATURE);
        generationConfig.put("maxOutputTokens", MAX_OUTPUT_TOKENS);
        // Optional: Add safety settings if needed, though for this task it might not be critical
        // Map<String, String> safetySetting = new HashMap<>();
        // safetySetting.put("category", "HARM_CATEGORY_DANGEROUS_CONTENT"); // Example
        // safetySetting.put("threshold", "BLOCK_NONE"); // Example
        // requestBody.put("safetySettings", Collections.singletonList(safetySetting));


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(message));
        requestBody.put("generationConfig", generationConfig);

        Request request = new Request.Builder()
                .url(this.geminiUrl)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.get("application/json; charset=utf-8")))
                .build();

        // System.out.println("Sending Price Prediction Request Body: " + gson.toJson(requestBody)); // For debugging

        try (Response response = this.client.newCall(request).execute()) {
            String responseBodyString = response.body() != null ? response.body().string() : null;
            // System.out.println("Raw Price Prediction API Response: HTTP " + response.code() + " Body: " + responseBodyString); // For debugging

            if (!response.isSuccessful() || responseBodyString == null) {
                 String errorDetails = "API request failed: HTTP " + response.code() + " - " + response.message();
                 if(responseBodyString != null) errorDetails += "\nResponse body: " + responseBodyString;
                 System.err.println(errorDetails);
                throw new IOException("Unexpected API response code: " + response.code() + ". Body: " + responseBodyString);
            }
            return parseResponse(responseBodyString);
        }
    }

    private String parseResponse(String jsonResponse) {
         try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            if (jsonObject.has("promptFeedback")) {
                JsonObject feedback = jsonObject.getAsJsonObject("promptFeedback");
                if (feedback.has("blockReason")) {
                     String reason = feedback.get("blockReason").getAsString();
                     System.err.println("Warning: Prompt potentially blocked. Reason: " + reason);
                     // If blocked, there might not be candidates.
                     // Depending on the block reason, you might want to return null or throw an exception.
                     // For now, we'll proceed to check for candidates, but it's likely to fail if blocked.
                }
            }

            // Crucial: If the API returns an error object instead of candidates (e.g. for invalid API key or model)
            if (jsonObject.has("error")) {
                JsonObject errorObj = jsonObject.getAsJsonObject("error");
                String errorMessage = errorObj.has("message") ? errorObj.get("message").getAsString() : "Unknown API error";
                System.err.println("API Error: " + errorMessage);
                System.err.println("Full error response: " + jsonResponse);
                return null; // Or throw specific exception
            }

            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                System.err.println("No candidates found in API response.");
                System.err.println("Full API response: " + jsonResponse); // Log the full response for debugging
                return null;
            }

            JsonObject candidate = candidates.get(0).getAsJsonObject();

            if (candidate.has("finishReason")) {
                String finishReason = candidate.get("finishReason").getAsString();
                if (!"STOP".equalsIgnoreCase(finishReason) && !"MAX_TOKENS".equalsIgnoreCase(finishReason)) {
                     System.err.println("Warning: Candidate finish reason was: " + finishReason + ". This might indicate an issue.");
                }
            } else {
                 System.err.println("Warning: Candidate finish reason missing.");
            }

            // If finishReason is "SAFETY" or other problematic reasons, content might be missing.
            if (!candidate.has("content")) {
                System.err.println("No content found in candidate. Finish reason might be relevant (e.g., SAFETY).");
                System.err.println("Full API response: " + jsonResponse);
                return null;
            }
            JsonObject content = candidate.getAsJsonObject("content");

            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.isEmpty()) {
                System.err.println("No parts found in content.");
                return null;
            }

            JsonObject part = parts.get(0).getAsJsonObject();
            if (!part.has("text")) {
                System.err.println("No text found in part.");
                return null;
            }

            String rawText = part.get("text").getAsString();
            return rawText.trim();

        } catch (JsonSyntaxException | IllegalStateException | NullPointerException e) {
            System.err.println("Error parsing JSON response for price prediction: " + e.getMessage());
            System.err.println("Malformed JSON was: " + jsonResponse);
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
                String priceStr = matcher.group(1).replace(",", "");
                return Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                System.err.println("Error extracting price: Failed to parse extracted numeric string '" + matcher.group(1) + "' after cleaning. Original text: '" + text + "'. Error: " + e.getMessage());
                return -1;
            } catch (IllegalStateException | IndexOutOfBoundsException e){
                 System.err.println("Error extracting price: Regex matching error on text: '" + text + "'. Error: " + e.getMessage());
                 return -1;
            }
        } else {
            System.err.println("Error extracting price: Could not find a valid numerical price pattern in the response text. Text was: '" + text + "'");
            return -1;
        }
    }
}