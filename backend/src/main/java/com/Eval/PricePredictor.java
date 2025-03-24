package com.Eval;

import com.google.gson.*;
import okhttp3.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PricePredictor {

    private static final String JSON_FILE_PATH = "backend\\src\\main\\java\\com\\Eval\\data.json"; // Path to your input JSON file
    private static final String API_KEY = "FILL_IT"; // API key
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
            + API_KEY;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            // Read the JSON file
            String jsonContent = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JsonObject inputData = JsonParser.parseString(jsonContent).getAsJsonObject();

            // Send request to Gemini API
            PricePredictor pricePredictor = new PricePredictor();
            String predictedPriceText = pricePredictor.getPredictedPrice(inputData);

            // Extract predicted price from the text response
            double predictedPrice = extractPriceFromText(predictedPriceText);

            // Print results
            System.out.println("Predicted Price: " + (predictedPrice == -1 ? "Unknown" : predictedPrice));
        } catch (IOException e) {
            System.err.println("Error reading JSON file or sending request: " + e.getMessage());
        }
    }

    // Method to send the request to the Gemini API and get the response
    public String getPredictedPrice(JsonObject inputData) throws IOException {
        String systemInstruction = "Predict the price for a house based on the following information provided in the JSON input. Give only result, no reasoning!"
                + " Use the provided values to predict the price: "
                + "{"
                + "\"Lotarea\": \"number\", "
                + "\"Total area\": \"number\", "
                + "\"Baths\": \"number\", "
                + "\"Build date\": \"number\", "
                + "\"House area\": \"number\", "
                + "\"Beds\": \"integer\", "
                + "\"Zip\": \"string\""
                + "}";

        String prompt = systemInstruction + "Details: " + inputData.toString() + "; \n";

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(message));

        Request request = new Request.Builder()
                .url(GEMINI_URL)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response received, HTTP Code: " + response.code());
            if (!response.isSuccessful()) {
                System.err.println("Error response: " + response.body().string());
                throw new IOException("Unexpected response " + response);
            }

            // Log the raw response body for debugging
            String responseBody = response.body().string();
            System.out.println("API Response Body: " + responseBody);

            return parseResponse(responseBody);
        }
    }

    private String parseResponse(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray candidates = jsonObject.getAsJsonArray("candidates");

        System.out.println("Parsing response...");

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
