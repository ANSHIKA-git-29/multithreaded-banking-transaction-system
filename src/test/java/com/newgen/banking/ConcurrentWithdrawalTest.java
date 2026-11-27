
package com.newgen.banking;

import com.newgen.banking.service.BankingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentWithdrawalTest {

    @Test
    void shouldPreventNegativeBalance() throws Exception {

        long accountId = 4; // Performance account

        // Reset balance for this test
        try (Connection connection =
                     com.newgen.banking.config.DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE ACCOUNT SET balance = 10000 WHERE account_id = ?")) {

            ps.setLong(1, accountId);
            assertEquals(1, ps.executeUpdate());
        }

        BankingService bankingService = new BankingService();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startSignal = new CountDownLatch(1);

        Callable<Boolean> withdrawal1 = () -> {
            startSignal.await();
            try {
                return bankingService.withdraw(
                        accountId, new BigDecimal("7000"));
            } catch (Exception e) {
                return false;
            }
        };

        Callable<Boolean> withdrawal2 = () -> {
            startSignal.await();
            try {
                return bankingService.withdraw(
                        accountId, new BigDecimal("5000"));
            } catch (Exception e) {
                return false;
            }
        };
        try{

        Future<Boolean> result1 = executor.submit(withdrawal1);
        Future<Boolean> result2 = executor.submit(withdrawal2);

        startSignal.countDown();

        boolean success1 = result1.get();
        boolean success2 = result2.get();

        executor.shutdown();

        assertNotEquals(success1, success2,
                "Exactly one withdrawal should succeed");

        try (Connection connection =
                     com.newgen.banking.config.DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT balance FROM ACCOUNT WHERE account_id = ?")) {

            ps.setLong(1, accountId);

            try (var rs = ps.executeQuery()) {
                assertTrue(rs.next());

                BigDecimal finalBalance = rs.getBigDecimal("balance");

                assertTrue(finalBalance.compareTo(BigDecimal.ZERO) >= 0);
                assertTrue(
                        finalBalance.compareTo(new BigDecimal("3000")) == 0
                        || finalBalance.compareTo(new BigDecimal("5000")) == 0
                );
            }
        }

} finally {
    executor.shutdownNow();
}
    }
}
