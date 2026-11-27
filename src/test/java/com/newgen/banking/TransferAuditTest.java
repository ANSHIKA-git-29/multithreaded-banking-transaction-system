
package com.newgen.banking;

import com.newgen.banking.config.DBConnection;
import com.newgen.banking.service.BankingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransferAuditTest {

    @Test
    void transferShouldCreateAuditRecord() throws Exception {

        long fromAccount = -1;
        long toAccount = -1;
        String transactionId = "TRANSFER-AUDIT-" + UUID.randomUUID();

        try {
            // Create two dedicated test accounts
            try (Connection con = DBConnection.getConnection()) {
                fromAccount = createAccount(con, "AS" + UUID.randomUUID().toString().substring(0, 8));
toAccount = createAccount(con, "AD" + UUID.randomUUID().toString().substring(0, 8));
            }

            // Perform transfer
            BankingService service = new BankingService();

            assertTrue(service.transfer(
                    fromAccount,
                    toAccount,
                    new BigDecimal("100.00"),
                    transactionId
            ));

            // Verify transaction and audit records
            try (Connection con = DBConnection.getConnection()) {

                String sql = """
                    SELECT COUNT(*)
                    FROM TRANSACTION_AUDIT
                    WHERE transaction_id = ?
                    AND action = 'TRANSFER'
                    """;

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, transactionId);

                    try (ResultSet rs = ps.executeQuery()) {
                        assertTrue(rs.next());
                        assertEquals(1, rs.getInt(1),
                                "Transfer audit record should exist");
                    }
                }
            }

        } finally {
            // Remove only the dedicated test accounts
            try (Connection con = DBConnection.getConnection()) {
                if (fromAccount != -1) {
                    deleteAccount(con, fromAccount);
                }
                if (toAccount != -1) {
                    deleteAccount(con, toAccount);
                }
            }
        }
    }

    private long createAccount(Connection con, String accountNumber)
            throws SQLException {

        String sql = """
            INSERT INTO ACCOUNT
            (customer_id, account_number, balance, account_type)
            VALUES (1, ?, 1000.00, 'SAVINGS')
            """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, accountNumber);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        throw new SQLException("Could not create test account");
    }

    private void deleteAccount(Connection con, long accountId)
            throws SQLException {

        try (PreparedStatement ps =
                     con.prepareStatement(
                             "DELETE FROM ACCOUNT WHERE account_id = ?")) {
            ps.setLong(1, accountId);
            ps.executeUpdate();
        }
    }
}

