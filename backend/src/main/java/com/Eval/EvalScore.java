package com.Eval;

import com.google.gson.*;
import okhttp3.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class EvalScore {

    private static final String JSON_FILE_PATH = "backend\\src\\main\\java\\com\\Eval\\data.json";
    private static final String API_KEY = "ADD_Yourself"; // API key
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            // Read the JSON file
            String jsonContent = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JsonObject inputData = JsonParser.parseString(jsonContent).getAsJsonObject();

            // Send request to Gemini API
            EvalScore evalScore = new EvalScore();
            String scoreText = evalScore.getEvaluationScore(inputData);
            double score = extractScoreFromText(scoreText);


            // Print results
            System.out.println("Eval score: " + (score == -1 ? "Unknown" : score));
        } catch (IOException e) {
            System.err.println("Error reading JSON file or sending request: " + e.getMessage());
        }
    }

    // Method to send the request to the Gemini API and get the response
    public String getEvaluationScore(JsonObject inputData) throws IOException{

        String systemInstruction = 
                        "You are a professional real estate analyst. Your task is to evaluate the overall quality and value of a house on a scale from 1 to 10, based solely on the provided JSON input." +
                        " Do not explain your reasoning—just return a single integer from 1 (very poor) to 10 (excellent)." +
                        " Use your expert knowledge of property appraisal, considering the price, lot size, total area, number of baths and beds, year built, house area, and ZIP code trends." +
                        " Input will be in this format: " +
                        "{" +
                        "\"Price\": number, " +
                        "\"Lotarea\": number, " +
                        "\"Total area\": number, " +
                        "\"Baths\": number, " +
                        "\"Build date\": number, " +
                        "\"House area\": number, " +
                        "\"Beds\": integer, " +
                        "\"Zip\": string" +
                        "}. " +
                        "Respond with only the evaluation score (an integer from 1 to 10), no extra characters.";

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
    private static double extractScoreFromText(String text) {
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
