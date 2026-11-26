
package com.newgen.banking.service;

import com.newgen.banking.config.DBConnection;
import com.newgen.banking.dao.AccountDAO;
import com.newgen.banking.dao.TransactionDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class BankingService {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // Deposit money
    public boolean deposit(long accountId, BigDecimal amount)
            throws SQLException {

        validateAmount(amount);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                boolean success =
                        accountDAO.deposit(connection, accountId, amount);

                if (!success) {
                    throw new SQLException("Account not found.");
                }

                transactionDAO.saveTransaction(
                        connection,
                        UUID.randomUUID().toString(),
                        null,
                        accountId,
                        "DEPOSIT",
                        amount,
                        "SUCCESS"
                );

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // Withdraw money
    public boolean withdraw(long accountId, BigDecimal amount)
            throws SQLException {

        validateAmount(amount);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                boolean success =
                        accountDAO.withdraw(connection, accountId, amount);

                if (!success) {
                    throw new SQLException(
                            "Account not found or insufficient balance."
                    );
                }

                transactionDAO.saveTransaction(
                        connection,
                        UUID.randomUUID().toString(),
                        accountId,
                        null,
                        "WITHDRAW",
                        amount,
                        "SUCCESS"
                );

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // Transfer money between two accounts
    public boolean transfer(long fromAccountId,
                            long toAccountId,
                            BigDecimal amount) throws SQLException {

        validateAmount(amount);

        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException(
                    "Source and destination accounts must be different."
            );
        }

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Lock accounts in ascending ID order
                long firstId = Math.min(fromAccountId, toAccountId);
                long secondId = Math.max(fromAccountId, toAccountId);

                BigDecimal firstBalance =
                        lockAccount(connection, firstId);

                BigDecimal secondBalance =
                        lockAccount(connection, secondId);

                if (firstBalance == null || secondBalance == null) {
                    throw new SQLException("Account not found.");
                }

                BigDecimal sourceBalance =
                        fromAccountId == firstId
                                ? firstBalance : secondBalance;

                if (sourceBalance.compareTo(amount) < 0) {
                    throw new SQLException("Insufficient balance.");
                }

                boolean withdrawn = accountDAO.withdraw(
                        connection, fromAccountId, amount
                );

                if (!withdrawn) {
                    throw new SQLException("Withdrawal failed.");
                }

                boolean deposited = accountDAO.deposit(
                        connection, toAccountId, amount
                );

                if (!deposited) {
                    throw new SQLException("Deposit failed.");
                }

                transactionDAO.saveTransaction(
                        connection,
                        UUID.randomUUID().toString(),
                        fromAccountId,
                        toAccountId,
                        "TRANSFER",
                        amount,
                        "SUCCESS"
                );

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // Validate transaction amount
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be greater than zero."
            );
        }
    }

    // Lock an account during transfer
    private BigDecimal lockAccount(Connection connection, long accountId)
            throws SQLException {

        String sql = "SELECT balance FROM ACCOUNT " +
                     "WHERE account_id = ? FOR UPDATE";

        try (java.sql.PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setLong(1, accountId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }
        }

        return null;
    }
}
