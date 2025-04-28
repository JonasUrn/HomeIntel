package com.Scraper;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tomcat.util.json.JSONParser;

import com.google.gson.*;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ZillowAPI {
    private String baseURL = "https://api.apify.com/v2/";
    private String houseId;
    private String key;

    public ZillowAPI(String zpid){
        this.houseId = zpid;
        Dotenv dotenv = Dotenv.load();
        this.key = dotenv.get("API_ZILLOW");
    }

    public JsonObject requestData(){
        JsonObject finalAnswer = new JsonObject();

        String requestUrl = String.format("%s/whybit~zillow-detail-scraper-task/runs?token=%s&zpid=%s", this.baseURL, this.key, this.houseId);

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
        .url(requestUrl)
        .get()
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", "Bearer " + this.key)
        .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.printf("Is successfull: %s\n\nText: \n%s", response.isSuccessful(), responseBody);

            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            if (responseJson.has("data") && responseJson.getAsJsonObject("data").has("items")) {
                // Pavyzdžiui, paimkite pirmą elementą iš items
                JsonObject item = responseJson.getAsJsonObject("data").getAsJsonArray("items").get(0).getAsJsonObject();
                System.out.println("Item data: " + item);
            } else {
                System.out.println("No items found in the response.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
                
        return finalAnswer;
    }

    public Dictionary<String, String> getStructuredData(){
        JsonObject responseData = requestData();
        System.out.printf("Returning: %s", responseData);
        Dictionary<String, String> responseDict = parseResponse(responseData);
        return responseDict;
    }
    public Dictionary<String, String> parseResponse(JsonObject obj){
        Dictionary<String, String> responseDict = new Hashtable<>();
        Set<Entry<String, JsonElement>> entries = obj.entrySet();

        for(Entry<String, JsonElement> element : entries){
            responseDict.put(element.getKey(), element.getValue().toString());
        }
        return responseDict;
    }
}
