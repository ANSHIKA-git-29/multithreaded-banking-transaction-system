
package com.newgen.banking.simulator;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PerformanceSimulator {

    public static void runTest(int numberOfThreads,
                               int totalTransactions)
            throws InterruptedException {

        TransactionWorker.resetCounters();

        ExecutorService executor =
                Executors.newFixedThreadPool(numberOfThreads);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalTransactions; i++) {
            executor.submit(
                new TransactionWorker(
                    3,
                    new BigDecimal("1.00")
                )
            );
        }

        executor.shutdown();

        boolean completed = executor.awaitTermination(
                30, TimeUnit.MINUTES
        );

        if (!completed) {
            executor.shutdownNow();
            throw new IllegalStateException(
                "Transactions did not finish within 30 minutes."
            );
        }

        long endTime = System.currentTimeMillis();

        double totalSeconds =
                (endTime - startTime) / 1000.0;

        int successful = TransactionWorker.getSuccessCount();
        int failed = TransactionWorker.getFailureCount();

        double tps = totalSeconds > 0
                ? successful / totalSeconds : 0;

        System.out.println("\n--- PERFORMANCE RESULTS ---");
        System.out.println("Threads: " + numberOfThreads);
        System.out.println("Total Transactions: " + totalTransactions);
        System.out.println("Successful: " + successful);
        System.out.println("Failed: " + failed);
        System.out.println("Time: " + totalSeconds + " seconds");
        System.out.println("TPS: " + tps);
    }
}
