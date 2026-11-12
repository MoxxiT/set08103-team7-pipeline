
package org.example; // Defines the package.

// Imports the database connection class.
import java.sql.Connection;

// Imports prepared SQL statements.
import java.sql.PreparedStatement;

// Imports SQL query results.
import java.sql.ResultSet;

// Imports regular SQL statements.
import java.sql.Statement;

// Imports SQL error handling.
import java.sql.SQLException;

// Imports the ArrayList class.
import java.util.ArrayList;

// Imports the List interface.
import java.util.List;

// Creates the capital city report class.
public class CapitalCityReport {

    /**
     * Gets all capital cities in the world.
     * Sorts them from largest to smallest population.
     */
    public static List<CapitalCity> getWorldCapitalCitiesByPopulation() {

        // Creates an empty list for capital cities.
        List<CapitalCity> capitalCities = new ArrayList<>();

        // Defines the SQL query.
        String strSelect =
                "SELECT city.Name, country.Name AS Country, city.Population "
                        + "FROM country "
                        + "JOIN city ON country.Capital = city.ID "
                        + "ORDER BY city.Population DESC";

        // Starts database error handling.
        try {

            // Gets the shared database connection.
            Connection con = DatabaseConnection.getConnection();

            // Checks whether the connection exists.
            if (con == null) {

                // Prints a connection error.
                System.out.println("Database connection is null.");

                // Returns the empty list.
                return capitalCities;
            }

            // Opens the statement and result set.
            // Both close automatically when finished.
            try (Statement stmt = con.createStatement();
                 ResultSet rset = stmt.executeQuery(strSelect)) {

                // Loops through every result.
                while (rset.next()) {

                    // Creates a new capital city object.
                    CapitalCity capCity = new CapitalCity();

                    // Sets the capital city name.
                    capCity.setName(rset.getString("Name"));

                    // Sets the country name.
                    capCity.setCountry(rset.getString("Country"));

                    // Sets the population.
                    capCity.setPopulation(rset.getLong("Population"));

                    // Adds the capital city to the list.
                    capitalCities.add(capCity);
                }
            }

            // Catches database errors.
        } catch (SQLException e) {

            // Prints an error message.
            System.out.println("Database query failed.");

            // Prints the full error details.
            e.printStackTrace();
        }

        // Returns the capital city list.
        return capitalCities;
    }

    /**
     * Gets capital cities in a continent.
     * Sorts them by population.
     */
    public static List<CapitalCity> getCapitalCitiesByContinent(String continent) {

        // Calls the shared method for continents.
        return getCapitalCities(continent, true, 0);
    }

    /**
     * Gets capital cities in a region.
     * Sorts them by population.
     */
    public static List<CapitalCity> getCapitalCitiesByRegion(String region) {

        // Calls the shared method for regions.
        return getCapitalCities(region, false, 0);
    }

    /**
     * Gets the top N capital cities in the world.
     * Returns an empty list if N is zero or negative.
     */
    public static List<CapitalCity> getTopNCapitalCitiesInWorld(int n) {

        // Creates an empty list.
        List<CapitalCity> capitals = new ArrayList<>();

        // Checks whether N is valid.
        if (n <= 0) {

            // Returns an empty list for invalid N.
            return capitals;
        }

        // Defines the SQL query with a limit parameter.
        String sql = "SELECT city.Name, country.Name AS Country, "
                + "city.Population FROM country "
                + "JOIN city ON country.Capital = city.ID "
                + "ORDER BY city.Population DESC, city.Name ASC "
                + "LIMIT ?";

        // Starts database error handling.
        try {

            // Gets the shared database connection.
            Connection con = DatabaseConnection.getConnection();

            // Checks whether the connection exists.
            if (con == null) {

                // Returns the empty list.
                return capitals;
            }

            // Creates a prepared statement.
            // The statement closes automatically.
            try (PreparedStatement stmt = con.prepareStatement(sql)) {

                // Sets the maximum number of results.
                stmt.setInt(1, n);

                // Executes the query and closes results automatically.
                try (ResultSet rs = stmt.executeQuery()) {

                    // Loops through each result.
                    while (rs.next()) {

                        // Creates a capital city object.
                        CapitalCity city = new CapitalCity();

                        // Sets the capital city name.
                        city.setName(rs.getString("Name"));

                        // Sets the country name.
                        city.setCountry(rs.getString("Country"));

                        // Sets the population.
                        city.setPopulation(rs.getLong("Population"));

                        // Adds the city to the list.
                        capitals.add(city);
                    }
                }
            }

            // Catches database errors.
        } catch (SQLException e) {

            // Prints the database error.
            System.err.println("Failed to fetch top N world capital cities: "
                    + e.getMessage());
        }

        // Returns the list of capital cities.
        return capitals;
    }

    /**
     * Gets the top N capital cities in a continent.
     * Returns an empty list for invalid N.
     */
    public static List<CapitalCity> getTopNCapitalCitiesByContinent(
            String continent, int n) {

        // Checks whether N is valid.
        if (n <= 0) {

            // Returns an empty list.
            return new ArrayList<>();
        }

        // Calls the shared method with the continent.
        return getCapitalCities(continent, true, n);
    }

    /**
     * Gets the top N capital cities in a region.
     * Returns an empty list for invalid N.
     */
    public static List<CapitalCity> getTopNCapitalCitiesByRegion(
            String region, int n) {

        // Checks whether N is valid.
        if (n <= 0) {

            // Returns an empty list.
            return new ArrayList<>();
        }

        // Calls the shared method with the region.
        return getCapitalCities(region, false, n);
    }

    /**
     * Shared method for continent and region reports.
     * Uses prepared statements for safe SQL queries.
     */
    private static List<CapitalCity> getCapitalCities(
            String location, boolean isContinent, int limit) {

        // Creates an empty list.
        List<CapitalCity> capitals = new ArrayList<>();

        // Checks for invalid location or limit.
        if (location == null || location.isBlank() || limit < 0) {

            // Returns an empty list for invalid input.
            return capitals;
        }

        // Chooses the continent or region column.
        String column = isContinent ? "Continent" : "Region";

        // Creates the SQL query.
        String sql = "SELECT city.Name, country.Name AS Country, "
                + "city.Population FROM country "
                + "JOIN city ON country.Capital = city.ID "
                + "WHERE country." + column + " = ? "
                + "ORDER BY city.Population DESC";

        // Checks whether a result limit is required.
        if (limit > 0) {

            // Adds a parameter for the result limit.
            sql += " LIMIT ?";
        }

        // Starts database error handling.
        try {

            // Gets the shared database connection.
            Connection con = DatabaseConnection.getConnection();

            // Checks whether the connection exists.
            if (con == null) {

                // Returns the empty list.
                return capitals;
            }

            // Creates a prepared SQL statement.
            try (PreparedStatement stmt = con.prepareStatement(sql)) {

                // Sets the continent or region value.
                stmt.setString(1, location);

                // Checks whether a limit is required.
                if (limit > 0) {

                    // Sets the maximum number of results.
                    stmt.setInt(2, limit);
                }

                // Executes the query and closes the results automatically.
                try (ResultSet rs = stmt.executeQuery()) {

                    // Loops through each result.
                    while (rs.next()) {

                        // Creates a capital city object.
                        CapitalCity city = new CapitalCity();

                        // Sets the capital city name.
                        city.setName(rs.getString("Name"));

                        // Sets the country name.
                        city.setCountry(rs.getString("Country"));

                        // Sets the population.
                        city.setPopulation(rs.getLong("Population"));

                        // Adds the city to the list.
                        capitals.add(city);
                    }
                }
            }

            // Catches database errors.
        } catch (SQLException e) {

            // Prints the error message.
            System.err.println("Failed to fetch capital cities: "
                    + e.getMessage());
        }

        // Returns the capital city list.
        return capitals;
    }

    /**
     * Displays capital cities in a formatted table.
     */
    public static void displayCapitalCities(List<CapitalCity> capitalCities) {

        // Prints the table headings.
        System.out.printf("%-30s | %-30s | %-12s%n",
                "Capital City Name", "Country", "Population");

        // Prints the table separator.
        System.out.println("-".repeat(80));

        // Loops through each capital city.
        for (CapitalCity c : capitalCities) {

            // Prints the city, country and population.
            System.out.printf("%-30s | %-30s | %-12d%n",
                    c.getName(), c.getCountry(), c.getPopulation());
        }
    }

    /**
     * Runs the capital city reports.
     */
    public static void main(String[] args) {

        // Prints the first report heading.
        System.out.println(
                "Fetching capital cities in the world by largest population to smallest...");

        // Gets all world capital cities.
        List<CapitalCity> capitalCities = getWorldCapitalCitiesByPopulation();

        // Displays all world capital cities.
        displayCapitalCities(capitalCities);

        // Prints the second report heading.
        System.out.println("\nTop 5 capital cities in the world...");

        // Gets and displays the top five capital cities.
        displayCapitalCities(getTopNCapitalCitiesInWorld(5));

        // Disconnects from the database.
        DatabaseConnection.disconnect();
    }
}
