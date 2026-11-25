
package com.newgen.banking;

import com.newgen.banking.config.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            System.out.println("MySQL Connected Successfully!");
            System.out.println("Database: " +
                    connection.getCatalog());

        } catch (SQLException e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }
    }
}
