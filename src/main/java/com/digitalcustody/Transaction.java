package com.digitalcustody;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String hash;
    private String from;
    private String to;
    private BigDecimal valueEth;
    private LocalDateTime timestamp;
    private boolean isContractInteraction;
    private String methodSignature;

    public Transaction(String hash, String from, String to, BigDecimal valueEth, LocalDateTime timestamp,
            boolean isContractInteraction, String methodSignature) {
        this.hash = hash;
        this.from = from;
        this.to = to;
        this.valueEth = valueEth;
        this.timestamp = timestamp;
        this.isContractInteraction = isContractInteraction;
        this.methodSignature = methodSignature;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public boolean isContractInteraction() {
        return isContractInteraction;
    }

    public String getHash() {
        return hash;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigDecimal getValueEth() {
        return valueEth;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}