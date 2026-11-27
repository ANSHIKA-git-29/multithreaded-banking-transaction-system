
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

    // Deposit with automatically generated transaction ID
    public boolean deposit(long accountId, BigDecimal amount)
            throws SQLException {

        return deposit(accountId, amount, UUID.randomUUID().toString());
    }

    // Deposit with idempotency protection
    public boolean deposit(long accountId, BigDecimal amount,
                           String transactionId) throws SQLException {

        validateAmount(amount);
        validateTransactionId(transactionId);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                checkDuplicateTransaction(connection, transactionId);

                boolean success =
                        accountDAO.deposit(connection, accountId, amount);

                if (!success) {
                    throw new SQLException("Account not found.");
                }

                transactionDAO.saveTransaction(
                        connection, transactionId, null, accountId,
                        "DEPOSIT", amount, "SUCCESS"
                );

                transactionDAO.saveAudit(
                        connection, transactionId,
                        "DEPOSIT",
                        "Deposit of " + amount +
                                " to account " + accountId
                );

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                rollbackAndLogFailure(
                        connection, transactionId, e
                );
                throw e;
            }
        }
    }

    // Withdraw with automatically generated transaction ID
    public boolean withdraw(long accountId, BigDecimal amount)
            throws SQLException {

        return withdraw(accountId, amount, UUID.randomUUID().toString());
    }

    // Withdraw with idempotency protection
    public boolean withdraw(long accountId, BigDecimal amount,
                            String transactionId) throws SQLException {

        validateAmount(amount);
        validateTransactionId(transactionId);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                checkDuplicateTransaction(connection, transactionId);

                boolean success =
                        accountDAO.withdraw(connection, accountId, amount);

                if (!success) {
                    throw new SQLException(
                            "Account not found or insufficient balance."
                    );
                }

                transactionDAO.saveTransaction(
                        connection, transactionId, accountId, null,
                        "WITHDRAW", amount, "SUCCESS"
                );

                transactionDAO.saveAudit(
                        connection, transactionId,
                        "WITHDRAW",
                        "Withdrawal of " + amount +
                                " from account " + accountId
                );

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                rollbackAndLogFailure(
                        connection, transactionId, e
                );
                throw e;
            }
        }
    }

    // Transfer with automatically generated transaction ID
    public boolean transfer(long fromAccountId,
                            long toAccountId,
                            BigDecimal amount) throws SQLException {

        return transfer(
                fromAccountId,
                toAccountId,
                amount,
                UUID.randomUUID().toString()
        );
    }

    // Transfer with idempotency protection
    public boolean transfer(long fromAccountId,
                            long toAccountId,
                            BigDecimal amount,
                            String transactionId) throws SQLException {

        validateAmount(amount);
        validateTransactionId(transactionId);

        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException(
                    "Source and destination accounts must be different."
            );
        }

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                checkDuplicateTransaction(connection, transactionId);

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
                        connection, transactionId,
                        fromAccountId, toAccountId,
                        "TRANSFER", amount, "SUCCESS"
                );

                transactionDAO.saveAudit(
                        connection, transactionId,
                        "TRANSFER",
                        "Transferred " + amount +
                                " from account " + fromAccountId +
                                " to account " + toAccountId
                );

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                rollbackAndLogFailure(
                        connection, transactionId, e
                );
                throw e;
            }
        }
    }

    // Roll back the original transaction and record its failure separately
    private void rollbackAndLogFailure(
            Connection connection,
            String transactionId,
            Exception originalException) {

        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }

        // Use a separate connection so failure logging
        // is not affected by the original rollback.
        try (Connection failureConnection =
                     DBConnection.getConnection()) {

            transactionDAO.saveFailedTransaction(
                    failureConnection,
                    transactionId,
                    originalException.getMessage()
            );

        } catch (SQLException loggingException) {
            originalException.addSuppressed(loggingException);
        }
    }

    // Check for an existing transaction ID
    private void checkDuplicateTransaction(
            Connection connection, String transactionId)
            throws SQLException {

        if (transactionDAO.transactionExists(
                connection, transactionId)) {

            throw new SQLException(
                    "Duplicate transaction ID: " + transactionId
            );
        }
    }

    // Validate transaction amount
    private void validateAmount(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Transaction amount must be greater than zero."
            );
        }
    }

    // Validate transaction ID
    private void validateTransactionId(String transactionId) {

        if (transactionId == null || transactionId.isBlank()) {

            throw new IllegalArgumentException(
                    "Transaction ID cannot be empty."
            );
        }
    }

    // Lock an account during transfer
    private BigDecimal lockAccount(
            Connection connection, long accountId)
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
