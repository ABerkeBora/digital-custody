package com.digitalcustody;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.math.BigDecimal;

public class PriceService {

    private static final OkHttpClient client = new OkHttpClient();

    public static BigDecimal getEthPriceInUsd() {
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to fetch ETH price from Coingecko");
                return BigDecimal.ZERO;
            }

            String json = response.body().string();
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            BigDecimal price = obj.getAsJsonObject("ethereum").get("usd").getAsBigDecimal();

            return price;
        } catch (Exception e) {
            System.err.println("Error fetching ETH price: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}