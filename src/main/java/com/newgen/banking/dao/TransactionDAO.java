
package com.newgen.banking.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class TransactionDAO {

    // Check whether transaction ID already exists
    public boolean transactionExists(
            Connection connection, String transactionId)
            throws SQLException {

        String sql = "SELECT 1 FROM BANK_TRANSACTION " +
                     "WHERE transaction_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, transactionId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Save successful transaction
    public void saveTransaction(
            Connection connection,
            String transactionId,
            Long fromAccount,
            Long toAccount,
            String transactionType,
            BigDecimal amount,
            String status) throws SQLException {

        String sql = "INSERT INTO BANK_TRANSACTION " +
                "(transaction_id, from_account, to_account, " +
                "transaction_type, amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, transactionId);

            if (fromAccount == null) {
                ps.setNull(2, Types.BIGINT);
            } else {
                ps.setLong(2, fromAccount);
            }

            if (toAccount == null) {
                ps.setNull(3, Types.BIGINT);
            } else {
                ps.setLong(3, toAccount);
            }

            ps.setString(4, transactionType);
            ps.setBigDecimal(5, amount);
            ps.setString(6, status);

            ps.executeUpdate();
        }
    }

    // Save transaction audit record
    public void saveAudit(
            Connection connection,
            String transactionId,
            String action,
            String details) throws SQLException {

        String sql = "INSERT INTO TRANSACTION_AUDIT " +
                "(transaction_id, action, details) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, transactionId);
            ps.setString(2, action);
            ps.setString(3, details);

            ps.executeUpdate();
        }
    }

    // Save failed transaction record
    // Save failed transaction record
public void saveFailedTransaction(
        Connection connection,
        String transactionId,
        String errorMessage) throws SQLException {

    String sql = "INSERT INTO FAILED_TRANSACTION " +
            "(transaction_id, error_message) VALUES (?, ?)";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setString(1, transactionId);
        ps.setString(2, errorMessage);

        ps.executeUpdate();
    }
}
}
