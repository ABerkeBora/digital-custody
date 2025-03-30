package com.digitalcustody;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class EthereumService {

    private static final String INFURA_URL = System.getenv("INFURA_PROJECT_URL");
    private static final Web3j web3j = Web3j.build(new HttpService(INFURA_URL));
    private static final OkHttpClient client = new OkHttpClient();
    private static final String ETHERSCAN_API_KEY = System.getenv("ETHERSCAN_API_KEY");

    public static BigDecimal getEthBalance(String address) {
        try {
            EthGetBalance balanceResponse = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigInteger wei = balanceResponse.getBalance();
            return new BigDecimal(wei).divide(BigDecimal.TEN.pow(18)); // Convert from wei to ETH
        } catch (Exception e) {
            System.err.println("Failed to fetch ETH balance: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public static List<Transaction> getTransactions(String address) {
        List<Transaction> transactions = new ArrayList<>();

        String url = "https://api.etherscan.io/api"
                + "?module=account"
                + "&action=txlist"
                + "&address=" + address
                + "&startblock=0"
                + "&endblock=99999999"
                + "&sort=asc"
                + "&apikey=" + ETHERSCAN_API_KEY;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Etherscan API call failed.");
                return transactions;
            }

            String json = response.body().string();
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray result = root.getAsJsonArray("result");

            for (JsonElement element : result) {
                JsonObject tx = element.getAsJsonObject();

                String hash = tx.get("hash").getAsString();
                String from = tx.get("from").getAsString();
                String to = tx.get("to").getAsString();
                BigDecimal valueEth = new BigDecimal(tx.get("value").getAsString())
                        .divide(BigDecimal.TEN.pow(18));
                long timeStamp = tx.get("timeStamp").getAsLong();
                LocalDateTime date = Instant.ofEpochSecond(timeStamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                String input = tx.get("input").getAsString();
                boolean isContractInteraction = !input.equals("0x");
                String methodSig = input.length() >= 10 ? input.substring(0, 10) : input;

                transactions.add(new Transaction(hash, from, to, valueEth, date, isContractInteraction, methodSig));
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch transactions: " + e.getMessage());
        }

        return transactions;
    }

    public static int getSwapCount(List<Transaction> transactions) {
        int swapCount = 0;

        for (Transaction tx : transactions) {
            if (tx.isContractInteraction()) {
                String methodSig = tx.getMethodSignature();
                if (methodSig != null && (methodSig.startsWith("0x38ed1739") || // swapExactTokensForTokens
                        methodSig.startsWith("0x18cbafe5") || // swapExactETHForTokens
                        methodSig.startsWith("0x7ff36ab5") // swapExactETHForTokensSupportingFeeOnTransferTokens
                )) {
                    swapCount++;
                }
            }
        }

        return swapCount;
    }

    public static List<TokenTransfer> getTokenTransfers(String address) {
        List<TokenTransfer> transfers = new ArrayList<>();

        String url = "https://api.etherscan.io/api"
                + "?module=account"
                + "&action=tokentx"
                + "&address=" + address
                + "&startblock=0"
                + "&endblock=99999999"
                + "&sort=asc"
                + "&apikey=" + ETHERSCAN_API_KEY;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to fetch token transfers from Etherscan.");
                return transfers;
            }

            String json = response.body().string();
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray result = root.getAsJsonArray("result");

            for (JsonElement element : result) {
                JsonObject tx = element.getAsJsonObject();

                String from = tx.get("from").getAsString();
                String to = tx.get("to").getAsString();
                String symbol = tx.get("tokenSymbol").getAsString();
                String contractAddress = tx.get("contractAddress").getAsString();
                int decimals = tx.get("tokenDecimal").getAsInt();

                BigDecimal rawValue = new BigDecimal(tx.get("value").getAsString());
                BigDecimal value = rawValue.divide(BigDecimal.TEN.pow(decimals));

                transfers.add(new TokenTransfer(from, to, symbol, contractAddress, value, decimals));
            }
        } catch (Exception e) {
            System.err.println("Error while fetching token transfers: " + e.getMessage());
        }

        return transfers;
    }
}