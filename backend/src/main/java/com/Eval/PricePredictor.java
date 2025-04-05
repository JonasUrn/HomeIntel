package com.Eval;

import com.google.gson.*;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;

public class PricePredictor {
    Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("API_HOME_INTEL"); // API key
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    
    public static double getPredictPrice(JsonObject answer) {
        JsonObject inputData = answer.deepCopy();
        try {
            try {
                inputData.remove("Price");
            } catch (InvalidParameterException e) {
                System.err.println("No price found: " + e.getMessage());
            }
            // System.out.println(inputData.toString());
            // Send request to Gemini API
            PricePredictor pricePredictor = new PricePredictor();
            String predictedPriceText = pricePredictor.getPredictedPrice(inputData);

            // Extract predicted price from the text response
            double predictedPrice = extractPriceFromText(predictedPriceText);

            // Print results
            System.out.println("Predicted Price: " + (predictedPrice == -1 ? "Unknown" : predictedPrice));
            return predictedPrice;
        } catch (IOException e) {
            System.err.println("Error reading JSON file or sending request: " + e.getMessage());
        }
        return 0;
    }

    // Method to send the request to the Gemini API and get the response
    private String getPredictedPrice(JsonObject inputData) throws IOException {
        String systemInstruction = "You are a professional real estate analyst. Your task is to predict the fair market price of a house based solely on the provided JSON input."
                +
                " Do not provide explanations or reasoning—only return a single integer value representing the predicted price in USD."
                +
                " Use your knowledge of real estate pricing trends, ZIP-code-related value patterns, and housing characteristics to make an accurate estimate."
                +
                " " +
                "Respond with only the price in integer USD format, no extra characters.";

        String prompt = systemInstruction + "Details: " + inputData.toString() + "; \n";

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(message));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0);
        requestBody.put("generationConfig", generationConfig);

        Request request = new Request.Builder()
                .url(GEMINI_URL)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            // System.out.println("Response received, HTTP Code: " + response.code());
            if (!response.isSuccessful()) {
                System.err.println("Error response: " + response.body().string());
                throw new IOException("Unexpected response " + response);
            }

            // Log the raw response body for debugging
            String responseBody = response.body().string();
            // System.out.println("API Response Body: " + responseBody);

            return parseResponse(responseBody);
        }
    }

    private String parseResponse(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray candidates = jsonObject.getAsJsonArray("candidates");

        // System.out.println("Parsing response...");

        // If candidates array is empty, log and return null
        if (candidates == null || candidates.size() == 0) {
            System.err.println("No candidates found in the API response.");
            return null;
        }

        // Extract the text from the first candidate
        String jsonText = null;
        JsonObject candidate = candidates.get(0).getAsJsonObject();
        JsonObject content = candidate.getAsJsonObject("content");
        JsonArray parts = content.getAsJsonArray("parts");

        if (parts != null && parts.size() > 0) {
            JsonObject part = parts.get(0).getAsJsonObject();
            jsonText = part.get("text").getAsString();
        }

        if (jsonText != null) {
            // The response text contains the predicted price directly (no "Price:" prefix)
            return jsonText.trim(); // Return the raw text of the response
        } else {
            System.err.println("Response text not found.");
            return null;
        }
    }

    // Helper method to extract the price from the response text
    private static double extractPriceFromText(String text) {
        if (text != null && !text.isEmpty()) {
            try {
                // Attempt to parse the numeric value
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException e) {
                System.err.println("Error extracting price from response: " + e.getMessage());
                return -1;
            }
        }
        return -1; // Return -1 if price is not found
    }
}
