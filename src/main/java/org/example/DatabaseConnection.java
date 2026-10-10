package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection con = null;

    public static Connection getConnection() {
        if (con == null) {
            try {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Connect to the database (update URL, username, and password as provided by your module setup)
                // Note: If using Docker locally, the host might be 'localhost' or a specific service name.
                String dbUrl = "jdbc:mysql://localhost:3306/world?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                String dbUser = "root";
                String dbPassword = "examplepassword"; // Replace with actual credentials

                con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                System.out.println("Successfully connected to the database!");
            } catch (ClassNotFoundException e) {
                System.out.println("MySQL JDBC Driver not found.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Connection failed. Check output console.");
                e.printStackTrace();
            }
        }
        return con;
    }

    public static void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}