package com.Eval; // Assuming same package

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

public class PricePredictorLT {

    private final String apiKey;
    private final String geminiUrl;
    private final OkHttpClient client;
    private final Gson gson;

    private static final String MODEL_NAME = "gemini-1.5-flash";
    private static final double TEMPERATURE = 0.0;
    private static final int MAX_OUTPUT_TOKENS = 20;

    private static final Pattern PRICE_PATTERN_LT = Pattern.compile("\\s*(\\d+(?:[.,]\\d+)?)\\s*[EeUuRr]*");


    public PricePredictorLT() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("API_HOME_INTEL"); // Ensure this key is correct
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            String errorMsg = "ERROR: API_HOME_INTEL environment variable not set.";
            System.err.println(errorMsg);
            // logger.error(errorMsg);
            throw new IllegalArgumentException("API Key is missing.");
        }
        this.geminiUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", MODEL_NAME, this.apiKey);

        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    // Constructor for dependency injection
    public PricePredictorLT(String apiKey, OkHttpClient client, Gson gson, String modelName) {
        this.apiKey = apiKey;
        this.geminiUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", modelName, this.apiKey);
        this.client = client;
        this.gson = gson;
    }

    // Static helper method - tuned for LT
    public static double getPredictPriceLT(JsonObject propertyData) { // Renamed method slightly for clarity
        if (propertyData == null) {
            System.err.println("Input property data cannot be null.");
            return -1;
        }

        JsonObject inputData = propertyData.deepCopy();

        // Remove Lithuanian Price field ("kaina") if it exists
        inputData.remove("kaina");
        inputData.remove("Kaina"); // Check capitalized version too

        PricePredictorLT predictor = new PricePredictorLT();
        try {
            String predictedPriceText = predictor.getPredictedPriceInternal(inputData);
            double predictedPrice = extractPriceFromTextLT(predictedPriceText); // Use LT extraction

            // System.out.println("Raw API Response Text for Price (LT): " + predictedPriceText); // Debugging
            System.out.println("Predicted Price (LT): " + (predictedPrice < 0 ? "Unknown/Invalid" : String.format("%.2f Eur", predictedPrice))); // Format output with Eur
            return predictedPrice;
        } catch (IOException e) {
            System.err.println("Error communicating with LT Prediction API: " + e.getMessage());
            // logger.error("Error communicating with LT Prediction API", e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during LT price prediction: " + e.getMessage());
            // logger.error("An unexpected error occurred during LT price prediction", e);
        }
        return -1;
    }

    // Internal instance method - prompt adapted for LT
    private String getPredictedPriceInternal(JsonObject inputData) throws IOException {

        // --- *** REFINED PROMPT FOR LITHUANIAN PRICE PREDICTION *** ---
        String systemInstruction = """
            **KONTEKSTAS:**
            Esi ekspertas dirbtinis intelektas, specializuojantis Lietuvos nekilnojamojo turto rinkos analizėje ir vertinime. Tavo vienintelė užduotis yra numatyti tikėtiną rinkos kainą (Eurais) pateiktam nekilnojamojo turto objektui, remiantis **tik** pateiktais JSON duomenimis. Dauguma duomenų bus lietuvių kalba.

            **ĮVESTIES DUOMENYS:**
            JSON objektas su NT objekto informacija. Kainai įtaką darantys laukai paprastai yra (bet neapsiriboja):
            *   Vieta: `adresas` arba `vieta`, `miestas`, `savivaldybe`, `mikrorajonas` (LABAI SVARBU - naudok mikrorajoną/miestą vertinimui), `gatve`. Pašto kodai (`zip_code`) Lietuvoje mažiau svarbūs kainai nei mikrorajonas.
            *   Dydis: `plotas` (kvadratiniais metrais, m²)
            *   Struktūra: `kambariu_skaicius` (arba `kambsk`), `aukstas`, `aukstu_skaicius` (visame name)
            *   Amžius ir Būklė: `statybos_metai` (arba `metai`), `irengimas` (pvz., "pilna apdaila", "dalinė apdaila", "reikalingas remontas", "naujai įrengtas")
            *   Tipas: `namo_tipas` (pvz., "mūrinis", "blokinis", "karkasinis", "monolitinis"), `paskirtis` (pvz., "gyvenamoji", "komercinė")
            *   Specifiniai Ypatumai: `sklypo_plotas` (arais, a), `sildymas` (pvz., "centrinis", "autonominis dujinis", "geoterminis", "elektra"), `balkonas`, `garazas`, `rusys`, `energetine_klase`.

            **UŽDUOTIS:**
            1.  Kruopščiai išanalizuok pateiktus JSON duomenis. Atkreipk dėmesį į lietuviškus terminus.
            2.  Pasinaudok savo plačiomis žiniomis apie **Lietuvos NT rinkos tendencijas**, regioninius kainų skirtumus, **miestų ir mikrorajonų** vertes bei tipišką NT charakteristikų (dydžio, būklės, amžiaus, tipo, ypatumų, šildymo tipo) įtaką kainai.
            3.  Numatyk tikėtiną šio NT objekto rinkos kainą **Eurais**.

            **IŠVESTIES FORMATAS:**
            Grąžink TIK vieną **skaitinę vertę**, reiškiančią numatytą kainą.
            *   JOKIŲ Euro simbolių (€).
            *   JOKIŲ kablelių ar taškų tūkstančiams skirti (nebent tai dešimtainis skyriklis).
            *   JOKIO aiškinamojo teksto.
            *   JOKIŲ žodžių kaip "Eur" ar "Eurai".
            *   Tik skaičius (sveikasis skaičius pageidautinas, dešimtainės dalys leidžiamos jei būtina).

            **PAVYZDYS 1:**
            Įvesties JSON: {"vieta": "Vilnius, Žirmūnai, Žirmūnų g.", "plotas": 65, "kambariu_skaicius": 3, "aukstas": 4, "aukstu_skaicius": 5, "statybos_metai": 1975, "irengimas": "suremontuotas", "namo_tipas": "blokinis", "sildymas": "centrinis", "balkonas": true}
            Išvestis: 135000

            **PAVYZDYS 2:**
            Įvesties JSON: {"vieta": "Kaunas, Centras, Kęstučio g.", "plotas": 90, "kambariu_skaicius": 4, "aukstas": 2, "aukstu_skaicius": 3, "statybos_metai": 1935, "irengimas": "reikalingas remontas", "namo_tipas": "mūrinis", "sildymas": "autonominis dujinis", "sklypo_plotas": 0}
            Išvestis: 160000

            **PAVYZDYS 3:**
            Įvesties JSON: {"vieta": "Klaipėda, Paupiai, Jaunystės g.", "plotas": 120, "kambariu_skaicius": 5, "aukstas": 1, "aukstu_skaicius": 2, "statybos_metai": 2018, "irengimas": "pilna apdaila", "namo_tipas": "karkasinis", "sildymas": "geoterminis", "sklypo_plotas": 6, "energetine_klase": "A+"}
            Išvestis: 295000
            """; // End Text Block

        String prompt = systemInstruction +
                "\n\n**NT OBJEKTO DUOMENYS KAINOS PROGNOZAVIMUI:**\n" + 
                "JSON Input: " + inputData.toString() + "\n\n" +
                "**PROGNOZUOJAMA KAINA (Tik skaičius Eurais):**"; 

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

        // System.out.println("Sending LT Price Prediction Request Body: " + gson.toJson(requestBody));

        try (Response response = this.client.newCall(request).execute()) {
            String responseBodyString = response.body() != null ? response.body().string() : null;

            // System.out.println("Raw LT Price Prediction API Response: HTTP " + response.code() + " Body: " + responseBodyString);

            if (!response.isSuccessful() || responseBodyString == null) {
                 String errorDetails = "LT API request failed: HTTP " + response.code() + " - " + response.message();
                 if(responseBodyString != null) errorDetails += "\nResponse body: " + responseBodyString;
                 System.err.println(errorDetails);
                 // logger.error(errorDetails);
                throw new IOException("Unexpected API response code: " + response.code());
            }

            return parseResponse(responseBodyString);
        }
    }

    private String parseResponse(String jsonResponse) {
         try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            if (jsonObject.has("promptFeedback") && jsonObject.getAsJsonObject("promptFeedback").has("blockReason")) {
                 System.err.println("Warning: Prompt potentially blocked. Reason: " + jsonObject.getAsJsonObject("promptFeedback").get("blockReason").getAsString());
            }
            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) { System.err.println("No candidates found."); return null; }
            JsonObject candidate = candidates.get(0).getAsJsonObject();
            if (candidate.has("finishReason")) {
                String finishReason = candidate.get("finishReason").getAsString();
                if (!"STOP".equalsIgnoreCase(finishReason) && !"MAX_TOKENS".equalsIgnoreCase(finishReason)) {
                     System.err.println("Warning: Candidate finish reason was: " + finishReason);
                }
            } else { System.err.println("Warning: Candidate finish reason missing."); }
            JsonObject content = candidate.getAsJsonObject("content");
            if (content == null) { System.err.println("No content found in candidate."); return null; }
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.isEmpty()) { System.err.println("No parts found in content."); return null; }
            JsonObject part = parts.get(0).getAsJsonObject();
            if (!part.has("text")) { System.err.println("No text found in part."); return null; }
            return part.get("text").getAsString().trim();
        } catch (JsonSyntaxException | IllegalStateException | NullPointerException e) {
            System.err.println("Error parsing JSON response for LT price prediction: " + e.getMessage());
            System.err.println("Malformed JSON was: " + jsonResponse);
            return null;
        }
    }

    private static double extractPriceFromTextLT(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.err.println("Error extracting LT price: API response text is null or empty.");
            return -1;
        }

        String cleanedText = text.trim();
        Matcher matcher = PRICE_PATTERN_LT.matcher(cleanedText);

        if (matcher.matches()) { // Use matches() to ensure the *entire* string fits the pattern (more strict)
            try {
                // Get the captured numeric part (group 1)
                String priceStr = matcher.group(1);

                // IMPORTANT: Standardize decimal separator to '.' for Double.parseDouble
                priceStr = priceStr.replace(',', '.');
                // Remove any potential whitespace used as thousands separators (though prompt discourages it)
                priceStr = priceStr.replaceAll("\\s+", "");

                // Attempt to parse as double
                return Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                System.err.println("Error extracting LT price: Failed to parse extracted numeric string '" + matcher.group(1) + "' after cleaning. Original text: '" + text + "'. Error: " + e.getMessage());
                return -1;
            } catch (IllegalStateException | IndexOutOfBoundsException e){
                 System.err.println("Error extracting LT price: Regex matching error on text: '" + text + "'. Error: " + e.getMessage());
                 return -1;
            }
        } else {
            // Handle cases where the regex doesn't match the entire cleaned string
             System.err.println("Error extracting LT price: Response text '" + text + "' does not strictly match the expected numerical price pattern.");

             Matcher fallbackMatcher = Pattern.compile("(\\d+(?:[.,]\\d+)?)").matcher(cleanedText);
             if (fallbackMatcher.find()) {
                 System.err.println("Attempting fallback extraction...");
                 try {
                    String priceStr = fallbackMatcher.group(1).replace(',', '.').replaceAll("\\s+", "");
                    return Double.parseDouble(priceStr);
                 } catch (Exception e) {
                     System.err.println("Fallback extraction failed: " + e.getMessage());
                 }
             }
             // If both strict and fallback fail
            return -1;
        }
    }
}