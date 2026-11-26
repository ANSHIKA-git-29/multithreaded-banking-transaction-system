
package com.newgen.banking.simulator;

import com.newgen.banking.service.BankingService;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionWorker implements Runnable {

    private static final AtomicInteger successCount =
            new AtomicInteger(0);

    private static final AtomicInteger failureCount =
            new AtomicInteger(0);

    private final BankingService bankingService;
    private final long accountId;
    private final BigDecimal amount;

    public TransactionWorker(long accountId, BigDecimal amount) {
        this.bankingService = new BankingService();
        this.accountId = accountId;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            bankingService.deposit(accountId, amount);
            successCount.incrementAndGet();

        } catch (Exception e) {
    failureCount.incrementAndGet();

    System.err.println(
        "Transaction failed for account "
        + accountId + ": " + e.getMessage()
    );
}
    }

    public static int getSuccessCount() {
        return successCount.get();
    }

    public static int getFailureCount() {
        return failureCount.get();
    }

    public static void resetCounters() {
        successCount.set(0);
        failureCount.set(0);
    }
}
