
package com.newgen.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private String transactionId;
    private Long fromAccount;
    private Long toAccount;
    private String transactionType;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public Transaction(String transactionId, Long fromAccount,
                       Long toAccount, String transactionType,
                       BigDecimal amount, String status,
                       LocalDateTime createdAt) {

        this.transactionId = transactionId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
