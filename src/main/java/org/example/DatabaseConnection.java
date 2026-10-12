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

                // Read configuration from environment variables with sensible local defaults
                String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
                String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
                String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";

                String dbUrl = "jdbc:mysql://" + dbHost + ":3306/world?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

                // Debug print to verify the active connection string
                System.out.println("Attempting connection to URL: " + dbUrl);

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
                con = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}