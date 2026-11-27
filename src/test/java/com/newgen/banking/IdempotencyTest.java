
package com.newgen.banking;
import java.util.UUID;
import com.newgen.banking.config.DBConnection;
import com.newgen.banking.service.BankingService;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class IdempotencyTest {

    @Test
    void shouldPreventDuplicateDeposit() throws Exception {

        long accountId = 4;
        String transactionId = "IDEMPOTENCY-TEST-"+UUID.randomUUID();

        // Reset test account balance
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE ACCOUNT SET balance = 10000 WHERE account_id = ?")) {

            ps.setLong(1, accountId);
            assertEquals(1, ps.executeUpdate());
        }

        BankingService service = new BankingService();

        // First deposit
        assertTrue(service.deposit(
                accountId,
                new BigDecimal("500"),
                transactionId
        ));

        // Duplicate deposit must fail
        assertThrows(
                java.sql.SQLException.class,
                () -> service.deposit(
                        accountId,
                        new BigDecimal("500"),
                        transactionId
                )
        );

        // Verify final balance
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT balance FROM ACCOUNT WHERE account_id = ?")) {

            ps.setLong(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());

                assertEquals(
                        0,
                        rs.getBigDecimal("balance")
                                .compareTo(new BigDecimal("10500"))
                );
            }
        }
    }
}
