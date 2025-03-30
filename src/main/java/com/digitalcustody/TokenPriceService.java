package com.digitalcustody;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TokenPriceService {

    private static final OkHttpClient client = new OkHttpClient();

    public static Map<String, BigDecimal> getUsdPricesByContract(Map<String, String> contractToSymbolMap) {
        Map<String, BigDecimal> prices = new HashMap<>();
        if (contractToSymbolMap.isEmpty())
            return prices;

        String apiKey = System.getenv("COINMARKETCAP_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            return prices;
        }

        // CoinMarketCap requires symbols (e.g., USDC, DAI)
        String symbols = contractToSymbolMap.values().stream()
                .distinct()
                .collect(Collectors.joining(","));

        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest"
                + "?symbol=" + symbols
                + "&convert=USD";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-CMC_PRO_API_KEY", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return prices;
            }

            String json = response.body().string();
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data");

            for (Map.Entry<String, String> entry : contractToSymbolMap.entrySet()) {
                String contract = entry.getKey();
                String symbol = entry.getValue();
                if (data.has(symbol)) {
                    BigDecimal price = data.getAsJsonObject(symbol)
                            .getAsJsonObject("quote")
                            .getAsJsonObject("USD")
                            .get("price").getAsBigDecimal();
                    prices.put(contract, price);
                }
            }

        } catch (Exception e) {

        }

        return prices;
    }
}
