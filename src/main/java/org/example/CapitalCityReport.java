package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Capital city reports. Every report is ordered from the largest to the smallest population.
 *
 * Brief items covered:
 *  - all the capital cities in the world / a continent / a region
 *  - the top N capital cities in the world / a continent / a region
 */
public class CapitalCityReport {

    /** Not used: this is a utility class with only static methods, so it is never instantiated. */
    private CapitalCityReport() {
    }

    /**
     * Gets all the capital cities in the world organised by largest population to smallest.
     *
     * @return the capital cities, or an empty list if the database cannot be queried
     */
    public static List<CapitalCity> getWorldCapitalCitiesByPopulation() {
        List<CapitalCity> capitalCities = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) {
                System.out.println("Database connection is null.");
                return capitalCities;
            }

            Statement stmt = con.createStatement();
            // country.Capital holds a city ID, so join the city table to get the capital's details
            String strSelect = "SELECT city.Name, country.Name AS Country, city.Population "
                    + "FROM country "
                    + "JOIN city ON country.Capital = city.ID "
                    + "ORDER BY city.Population DESC";

            ResultSet rset = stmt.executeQuery(strSelect);

            while (rset.next()) {
                CapitalCity capCity = new CapitalCity();
                capCity.setName(rset.getString("Name"));
                capCity.setCountry(rset.getString("Country"));
                capCity.setPopulation(rset.getLong("Population"));
                capitalCities.add(capCity);
            }
        } catch (SQLException e) {
            System.out.println("Database query failed.");
            e.printStackTrace();
        }
        return capitalCities;
    }

    /**
     * Gets all the capital cities in a continent organised by largest population to smallest.
     *
     * @param continent the continent name, e.g. "Europe"
     * @return the capital cities, or an empty list if the continent is null, blank or unknown
     */
    public static List<CapitalCity> getCapitalCitiesByContinent(String continent) {
        return getCapitalCities(continent, true, 0);
    }

    /**
     * Gets all the capital cities in a region organised by largest population to smallest.
     *
     * @param region the region name, e.g. "Western Europe"
     * @return the capital cities, or an empty list if the region is null, blank or unknown
     */
    public static List<CapitalCity> getCapitalCitiesByRegion(String region) {
        return getCapitalCities(region, false, 0);
    }

    /**
     * Gets the top N most populated capital cities in the world.
     *
     * @param n how many capital cities to return
     * @return up to n capital cities, or an empty list if n is zero or negative
     */
    public static List<CapitalCity> getTopNCapitalCitiesInWorld(int n) {
        List<CapitalCity> capitals = new ArrayList<>();
        if (n <= 0) {
            return capitals;
        }

        String sql = "SELECT city.Name, country.Name AS Country, "
                + "city.Population FROM country "
                + "JOIN city ON country.Capital = city.ID "
                + "ORDER BY city.Population DESC, city.Name ASC "
                + "LIMIT ?";

        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) {
                return capitals;
            }

            try (java.sql.PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, n);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        CapitalCity city = new CapitalCity();
                        city.setName(rs.getString("Name"));
                        city.setCountry(rs.getString("Country"));
                        city.setPopulation(rs.getLong("Population"));
                        capitals.add(city);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch top N world capital cities: "
                    + e.getMessage());
        }

        return capitals;
    }

    /**
     * Gets the top N most populated capital cities in a continent.
     *
     * @param continent the continent name, e.g. "Europe"
     * @param n         how many capital cities to return
     * @return up to n capital cities, or an empty list if the continent is
     *         null/blank/unknown or n is zero or negative
     */
    public static List<CapitalCity> getTopNCapitalCitiesByContinent(
            String continent, int n) {
        if (n <= 0) {
            return new ArrayList<>();
        }
        return getCapitalCities(continent, true, n);
    }

    /**
     * Gets the top N most populated capital cities in a region.
     *
     * @param region the region name, e.g. "Western Europe"
     * @param n      how many capital cities to return
     * @return up to n capital cities, or an empty list if the region is
     *         null/blank/unknown or n is zero or negative
     */
    public static List<CapitalCity> getTopNCapitalCitiesByRegion(
            String region, int n) {
        if (n <= 0) {
            return new ArrayList<>();
        }
        return getCapitalCities(region, false, n);
    }

    /**
     * Shared query behind the continent and region reports.
     *
     * @param location    the continent or region name to filter on
     * @param isContinent true to filter by continent, false to filter by region
     * @param limit       the maximum number of rows to return, or 0 for no limit
     * @return the matching capital cities ordered by population, largest first
     */
    private static List<CapitalCity> getCapitalCities(
            String location, boolean isContinent, int limit) {

        List<CapitalCity> capitals = new ArrayList<>();

        if (location == null || location.isBlank() || limit < 0) {
            return capitals;
        }

        String column = isContinent ? "Continent" : "Region";

        String sql = "SELECT city.Name, country.Name AS Country, "
                + "city.Population FROM country "
                + "JOIN city ON country.Capital = city.ID "
                + "WHERE country." + column + " = ? "
                + "ORDER BY city.Population DESC";

        if (limit > 0) {
            sql += " LIMIT ?";
        }

        try {
            Connection con = DatabaseConnection.getConnection();

            if (con == null) {
                return capitals;
            }

            try (java.sql.PreparedStatement stmt =
                         con.prepareStatement(sql)) {

                stmt.setString(1, location);

                if (limit > 0) {
                    stmt.setInt(2, limit);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        CapitalCity city = new CapitalCity();

                        city.setName(rs.getString("Name"));
                        city.setCountry(rs.getString("Country"));
                        city.setPopulation(rs.getLong("Population"));

                        capitals.add(city);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch capital cities: "
                    + e.getMessage());
        }

        return capitals;
    }

    /**
     * Prints a list of capital cities as a table.
     *
     * @param capitalCities the capital cities to print
     */
    public static void displayCapitalCities(List<CapitalCity> capitalCities) {
        System.out.printf("%-30s | %-30s | %-12s%n",
                "Capital City Name", "Country", "Population");
        System.out.println("-".repeat(80));
        for (CapitalCity c : capitalCities) {
            System.out.printf("%-30s | %-30s | %-12d%n",
                    c.getName(), c.getCountry(), c.getPopulation());
        }
    }

    /**
     * Runs example capital city reports and prints the results.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("Fetching capital cities in the world by largest population to smallest...");
        List<CapitalCity> capitalCities = getWorldCapitalCitiesByPopulation();
        displayCapitalCities(capitalCities);

        System.out.println("\nTop 5 capital cities in the world...");
        displayCapitalCities(getTopNCapitalCitiesInWorld(5));
        DatabaseConnection.disconnect();
    }
}