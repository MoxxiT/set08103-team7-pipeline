package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CapitalCityReport {

    /**
     * Gets all the capital cities in the world organised by largest population to smallest.
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
            // SQL query joining country and city tables on the capital ID
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
    // Capital cities in a continent, sorted by population
    public static List<CapitalCity> getCapitalCitiesByContinent(String continent) {
        return getCapitalCities(continent, true, 0);
    }

    // Capital cities in a region, sorted by population
    public static List<CapitalCity> getCapitalCitiesByRegion(String region) {
        return getCapitalCities(region, false, 0);
    }

    // Top N capital cities in a continent
    public static List<CapitalCity> getTopNCapitalCitiesByContinent(
            String continent, int n) {
        return getCapitalCities(continent, true, n);
    }

    // Top N capital cities in a region
    public static List<CapitalCity> getTopNCapitalCitiesByRegion(
            String region, int n) {
        return getCapitalCities(region, false, n);
    }

    // Shared database query
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
    public static void displayCapitalCities(List<CapitalCity> capitalCities) {
        System.out.printf("%-30s | %-30s | %-12s%n",
                "Capital City Name", "Country", "Population");
        System.out.println("-".repeat(80));
        for (CapitalCity c : capitalCities) {
            System.out.printf("%-30s | %-30s | %-12d%n",
                    c.getName(), c.getCountry(), c.getPopulation());
        }
    }

    public static void main(String[] args) {
        System.out.println("Fetching capital cities in the world by largest population to smallest...");
        List<CapitalCity> capitalCities = getWorldCapitalCitiesByPopulation();
        displayCapitalCities(capitalCities);
        DatabaseConnection.disconnect();
    }
}