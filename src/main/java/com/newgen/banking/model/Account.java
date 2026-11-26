
package com.newgen.banking.model;

import java.math.BigDecimal;

public class Account {

    private long accountId;
    private long customerId;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;

    public Account(long accountId, long customerId,
                   String accountNumber, BigDecimal balance,
                   String accountType) {

        this.accountId = accountId;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
