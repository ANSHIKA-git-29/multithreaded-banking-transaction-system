
package com.newgen.banking;

import com.newgen.banking.config.DBConnection;
import com.newgen.banking.simulator.PerformanceSimulator;

public class Main {

    public static void main(String[] args)
            throws InterruptedException {

        try {
            int totalTransactions = 50000;

            int[] threadCounts = {1,5,10, 20};

            for (int threads : threadCounts) {

                System.out.println("\nTesting with "
                        + threads + " threads...");

                PerformanceSimulator.runTest(
                        threads,
                        totalTransactions
                );
            }

        } finally {
            DBConnection.closeDataSource();
        }
    }
}
