package com.newgen.banking.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TransactionDAO {

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
}