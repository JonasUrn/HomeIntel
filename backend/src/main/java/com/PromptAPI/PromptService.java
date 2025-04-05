package com.PromptAPI;

import okhttp3.*;
import com.google.gson.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
public class PromptService {
    private static final String API_KEY = "API_KEY"; // Replace with your actual API Key
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
            + API_KEY;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public JsonObject getStructuredResponse(String user_prompt) throws IOException {
        System.out.println("Sending request to Gemini API...");
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        String systemInstruction = "Please extract the following details from the provided real estate description and return them in a structured JSON format."
                + "Make sure to always use the exact field names and provide the values in the appropriate types. If city is present - infer the country. If possible, infer latitude and"
                + "longitude from address and vice verse - it may be approxiamte. If area and price are present - infer price per square meter. If price per square meter is present and are ais present - infer total price."
                + "If total price and price per is present - infer total area. If not possible to do so, use null. If a value is not present or cannot be inferred, use `null` or an empty string."
                + "If a field is optional, omit it if unknown. Please respond in the following JSON format:";

        String prompt = systemInstruction + "Details:" + user_prompt + "; \n" + " The fields to extract are: "
                + "{"
                + "\"Price\": \"number\", "
                + "\"Addres\": \"string\", "
                + "\"City\": \"string\", "
                + "\"Country\": \"string\", "
                + "\"YearBuilt\": \"integer\", "
                + "\"PricePer\": \"number\", "
                + "\"Area\": \"number\", "
                + "\"RoomCount\": \"integer\", "
                + "\"Longitude\": \"number\", "
                + "\"Latitude\": \"number\", "
                + "\"PropertyType\": \"enum [House, Apartment, Condo, Garage, Other]\", "
                + "\"NumOfFloors\": \"integer\", "
                + "\"FloorNr\": \"integer\", "
                + "\"Heating\": \"enum [Electricity, Gas, Wood, Diesel, Other]\", "
                + "\"EnergyClass\": \"enum [A++, A+, A, B, C, D, E, F, G]\", "
                + "\"NearestSchool\": \"string\", "
                + "\"NearestShop\": \"string\", "
                + "\"HasBalcony\": \"boolean\", "
                + "\"Description\": \"string\""
                + "}";

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
            String responseBody = response.body().string();
            JsonObject parsedResponse = parseResponse(responseBody);
            return parsedResponse;
        }
    }

    private JsonObject parseResponse(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray candidates = jsonObject.getAsJsonArray("candidates");
        System.out.println("Parsing response...");

        // Extract the text from the first candidate
        String jsonText = null;
        if (candidates != null && candidates.size() > 0) {
            JsonObject candidate = candidates.get(0).getAsJsonObject();
            JsonObject content = candidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts != null && parts.size() > 0) {
                JsonObject part = parts.get(0).getAsJsonObject();
                jsonText = part.get("text").getAsString();
            }
        }

        if (jsonText != null && jsonText.startsWith("```json")) {
            jsonText = jsonText.replace("```json", "").replace("```", "").trim();

            JsonObject parsedJson = JsonParser.parseString(jsonText).getAsJsonObject();
            return parsedJson;
        }
        return null;
    }
}
