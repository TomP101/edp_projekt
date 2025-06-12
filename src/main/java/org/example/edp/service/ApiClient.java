package org.example.edp.service;

import org.example.edp.model.Fact;
import org.json.JSONArray;
import org.json.JSONObject;
import org.example.edp.model.Quote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private static final String FACT_API_URL = "https://uselessfacts.jsph.pl/api/v2/facts/random?language=en";

    public static Fact fetchRandomFact() throws Exception {
        URL url = new URL(FACT_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }

        reader.close();
        conn.disconnect();

        JSONObject json = new JSONObject(responseBuilder.toString());

        return new Fact(
                json.optString("id"),
                json.optString("text"),
                json.optString("source"),
                json.optString("updated_at")
        );
    }
    private static final String QUOTE_API_URL = "https://zenquotes.io/api/random";

    public static Quote fetchRandomQuote() throws Exception {
        URL url = new URL(QUOTE_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }

        reader.close();
        conn.disconnect();

        JSONArray jsonArray = new JSONArray(responseBuilder.toString());
        JSONObject json = jsonArray.getJSONObject(0);

        return new Quote(
                json.optString("q"), // content
                json.optString("a")  // author
        );
    }
}
