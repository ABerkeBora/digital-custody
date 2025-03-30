package com.digitalcustody;

import java.math.BigDecimal;
import java.util.List;

public class TransactionStatsCalculator {

    public static TransactionStats calculate(List<Transaction> transactions, BigDecimal ethPriceUsd) {
        int totalTx = transactions.size();
        BigDecimal totalEthSent = BigDecimal.ZERO;
        int ethTransfers = 0;
        int contractCalls = 0;
        int swapCount = 0;
        for (Transaction tx : transactions) {
            totalEthSent = totalEthSent.add(tx.getValueEth());

            if (tx.isContractInteraction()) {
                contractCalls++;
                if (tx.getMethodSignature().startsWith("0x38ed1739") ||
                        tx.getMethodSignature().startsWith("0x18cbafe5") ||
                        tx.getMethodSignature().startsWith("0x7ff36ab5")) {
                    swapCount++;
                }
            } else {
                ethTransfers++;
            }
        }
        BigDecimal totalUsdVolume = totalEthSent.multiply(ethPriceUsd);

        return new TransactionStats(totalTx, totalEthSent, totalUsdVolume, ethTransfers, contractCalls, swapCount);
    }

    public static class TransactionStats {
        private final int totalTx;
        private final BigDecimal totalEthSent;
        private final BigDecimal totalUsdVolume;
        private final int ethTransfers;
        private final int contractCalls;
        private final int swapCount;

        public TransactionStats(int totalTx, BigDecimal totalEthSent, BigDecimal totalUsdVolume, int ethTransfers,
                int contractCalls, int swapCount) {
            this.totalTx = totalTx;
            this.totalEthSent = totalEthSent;
            this.totalUsdVolume = totalUsdVolume;
            this.ethTransfers = ethTransfers;
            this.contractCalls = contractCalls;
            this.swapCount = swapCount;
        }

        public int getSwapCount() {
            return swapCount;
        }

        public int getEthTransfers() {
            return ethTransfers;
        }

        public int getContractCalls() {
            return contractCalls;
        }

        public int getTotalTx() {
            return totalTx;
        }

        public BigDecimal getTotalEthSent() {
            return totalEthSent;
        }

        public BigDecimal getTotalUsdVolume() {
            return totalUsdVolume;
        }
    }
}