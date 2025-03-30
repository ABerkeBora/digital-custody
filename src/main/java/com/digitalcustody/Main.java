package com.digitalcustody;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // String address = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045";
        // Vitalik's address (This one fails with pricing of ERC-20s as he has so many
        // shitcoins failing coingeckos or coinmarketcaps api)

        String address = "0x4dfee6e07c77f52191dff76bc706f2af80db6d48";
        // Some random address with uniswap swaps that I have found randomly on
        // etherscan of uniswap's router.

        // 1. Fetch balance
        BigDecimal ethBalance = EthereumService.getEthBalance(address);
        System.out.println("ETH Balance: " + ethBalance);

        // 2. Fetch transactions
        List<Transaction> transactions = EthereumService.getTransactions(address);
        System.out.println("Fetched " + transactions.size() + " transactions.");

        // 3. Mock ETH/USD price for now (we’ll fetch live next)
        BigDecimal ethPriceUsd = PriceService.getEthPriceInUsd();
        System.out.println("Live ETH Price: $" + ethPriceUsd);

        // 4. Calculate stats
        TransactionStatsCalculator.TransactionStats stats = TransactionStatsCalculator.calculate(transactions,
                ethPriceUsd);

        System.out.println("\n--- Transaction Stats ---");
        System.out.println("Total Transactions: " + stats.getTotalTx());
        System.out.println("Total ETH Sent: " + stats.getTotalEthSent());
        System.out.println("Total USD Volume (approx): $" + stats.getTotalUsdVolume());
        System.out.println("ETH Transfers: " + stats.getEthTransfers());
        System.out.println("Contract Interactions: " + stats.getContractCalls());
        System.out.println("Swap Transactions (Uniswap-style): " + stats.getSwapCount());

        // 5. Fetch token transfers and calculate token USD value
        List<TokenTransfer> tokenTransfers = EthereumService.getTokenTransfers(address);
        System.out.println("Fetched " + tokenTransfers.size() + " ERC-20 token transfers.");

        BigDecimal tokenUsdValue = TokenStatsCalculator.calculate(tokenTransfers, address);
        System.out.println("ERC-20 Token Value (USD): $" + tokenUsdValue);

        // 6. Combined value
        BigDecimal totalUsd = stats.getTotalUsdVolume().add(tokenUsdValue);
        System.out.println("TOTAL USD Value (ETH + Tokens): $" + totalUsd);
    }
}