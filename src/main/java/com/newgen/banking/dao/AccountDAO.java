
package com.newgen.banking.dao;

import com.newgen.banking.model.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

public class AccountDAO {

    // Fetch account details
    public Account getAccountById(Connection connection, long accountId)
            throws SQLException {

        String sql = "SELECT * FROM ACCOUNT WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Account(
                        rs.getLong("account_id"),
                        rs.getLong("customer_id"),
                        rs.getString("account_number"),
                        rs.getBigDecimal("balance"),
                        rs.getString("account_type")
                    );
                }
            }
        }

        return null;
    }

    // Deposit money
    public boolean deposit(Connection connection, long accountId,
                           BigDecimal amount) throws SQLException {

        String sql = "UPDATE ACCOUNT SET balance = balance + ? " +
                     "WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setLong(2, accountId);

            return ps.executeUpdate() == 1;
        }
    }

    // Withdraw money safely
    public boolean withdraw(Connection connection, long accountId,
                             BigDecimal amount) throws SQLException {

        String sql = "UPDATE ACCOUNT SET balance = balance - ? " +
                     "WHERE account_id = ? AND balance >= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setLong(2, accountId);
            ps.setBigDecimal(3, amount);

            return ps.executeUpdate() == 1;
        }
    }
}
