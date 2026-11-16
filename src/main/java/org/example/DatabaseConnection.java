package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Holds the single shared connection to the MySQL "world" database.
 *
 * Connection settings are read from environment variables so the same code works
 * locally, in GitHub Actions and in Docker Compose:
 *  - DB_HOST (default "localhost")
 *  - DB_USER (default "root")
 *  - DB_PASSWORD (default empty)
 */
public class DatabaseConnection {

    /** Not used: this is a utility class with only static methods, so it is never instantiated. */
    private DatabaseConnection() {
    }

    /** The shared connection, or null if not connected yet (or after disconnect). */
    private static Connection con = null;

    /**
     * Returns the shared database connection, opening it on first use.
     *
     * @return the open connection, or null if the driver is missing or the
     *         database cannot be reached (the error is printed to the console)
     */
    public static Connection getConnection() {
        if (con == null) {
            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Read configuration from environment variables, falling back to local defaults
                String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
                String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
                String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";

                String dbUrl = "jdbc:mysql://" + dbHost + ":3306/world?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

                // Print the URL being used so connection problems are easy to diagnose
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

    /**
     * Closes the shared connection if it is open. The next call to
     * {@link #getConnection()} will open a new one.
     */
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
