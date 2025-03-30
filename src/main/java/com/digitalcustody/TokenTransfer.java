package com.digitalcustody;

import java.math.BigDecimal;

public class TokenTransfer {
    private String from;
    private String to;
    private String symbol;
    private String contractAddress;
    private BigDecimal value;
    private int decimals;

    public TokenTransfer(String from, String to, String symbol, String contractAddress, BigDecimal value,
            int decimals) {
        this.from = from;
        this.to = to;
        this.symbol = symbol;
        this.contractAddress = contractAddress;
        this.value = value;
        this.decimals = decimals;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public BigDecimal getValue() {
        return value;
    }

    public int getDecimals() {
        return decimals;
    }
}
