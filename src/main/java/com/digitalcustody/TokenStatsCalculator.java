package com.digitalcustody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenStatsCalculator {

    public static BigDecimal calculate(List<TokenTransfer> transfers, String walletAddress) {
        // 1. Sum total sent per token (filtering for transfers sent by the wallet)
        Map<String, BigDecimal> tokenSums = new HashMap<>();
        Map<String, String> contractToSymbol = new HashMap<>();

        for (TokenTransfer transfer : transfers) {
            if (!transfer.getFrom().equalsIgnoreCase(walletAddress))
                continue;

            String contract = transfer.getContractAddress().toLowerCase();
            contractToSymbol.putIfAbsent(contract, transfer.getSymbol());

            tokenSums.put(contract, tokenSums.getOrDefault(contract, BigDecimal.ZERO).add(transfer.getValue()));
        }

        // 2. Fetch live prices
        Map<String, BigDecimal> prices = TokenPriceService.getUsdPricesByContract(contractToSymbol);

        // 3. Multiply totals by price
        BigDecimal totalUsd = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : tokenSums.entrySet()) {
            String contract = entry.getKey();
            BigDecimal totalAmount = entry.getValue();

            BigDecimal price = prices.getOrDefault(contract, BigDecimal.ZERO);
            BigDecimal usdValue = totalAmount.multiply(price);

            totalUsd = totalUsd.add(usdValue);
        }

        return totalUsd;
    }
}
