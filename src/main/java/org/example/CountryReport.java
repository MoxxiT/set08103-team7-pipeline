package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Country reports. Every report is ordered from the largest to the smallest population
 * (ties broken alphabetically so the order is always the same).
 *
 * Brief items covered:
 *  - all countries in the world / a continent / a region
 *  - the top N countries in the world / a continent / a region
 */
public class CountryReport {

    // The Capital column holds a city ID, so we join the city table to get the capital's name.
    // LEFT JOIN keeps countries with no capital (e.g. Antarctica); COALESCE shows "N/A" for them.
    private static final String SELECT_COUNTRIES =
            "SELECT c.Code, c.Name, c.Continent, c.Region, c.Population, "
                    + "COALESCE(cap.Name, 'N/A') AS Capital "
                    + "FROM country c "
                    + "LEFT JOIN city cap ON c.Capital = cap.ID ";

    private static final String ORDER_BY_POPULATION = "ORDER BY c.Population DESC, c.Name ASC ";

    /**
     * Gets all the countries in the world organised by largest population to smallest.
     */
    public static List<Country> getWorldCountriesByPopulation() {
        return queryCountries(SELECT_COUNTRIES + ORDER_BY_POPULATION);
    }

    /**
     * Gets all the countries in a continent organised by largest population to smallest.
     * Returns an empty list if the continent is null, blank or unknown.
     */
    public static List<Country> getCountriesByContinent(String continent) {
        if (isBlank(continent)) return new ArrayList<>();
        return queryCountries(SELECT_COUNTRIES + "WHERE c.Continent = ? " + ORDER_BY_POPULATION, continent);
    }

    /**
     * Gets all the countries in a region organised by largest population to smallest.
     * Returns an empty list if the region is null, blank or unknown.
     */
    public static List<Country> getCountriesByRegion(String region) {
        if (isBlank(region)) return new ArrayList<>();
        return queryCountries(SELECT_COUNTRIES + "WHERE c.Region = ? " + ORDER_BY_POPULATION, region);
    }

    /**
     * Gets the top N populated countries in the world.
     * Returns an empty list if n is zero or negative.
     */
    public static List<Country> getTopNCountriesInWorld(int n) {
        if (n <= 0) return new ArrayList<>();
        return queryCountries(SELECT_COUNTRIES + ORDER_BY_POPULATION + "LIMIT ?", n);
    }

    /**
     * Gets the top N populated countries in a continent.
     * Returns an empty list if the continent is null/blank/unknown or n is zero or negative.
     */
    public static List<Country> getTopNCountriesByContinent(String continent, int n) {
        if (isBlank(continent) || n <= 0) return new ArrayList<>();
        return queryCountries(SELECT_COUNTRIES + "WHERE c.Continent = ? " + ORDER_BY_POPULATION + "LIMIT ?",
                continent, n);
    }

    /**
     * Gets the top N populated countries in a region.
     * Returns an empty list if the region is null/blank/unknown or n is zero or negative.
     */
    public static List<Country> getTopNCountriesByRegion(String region, int n) {
        if (isBlank(region) || n <= 0) return new ArrayList<>();
        return queryCountries(SELECT_COUNTRIES + "WHERE c.Region = ? " + ORDER_BY_POPULATION + "LIMIT ?",
                region, n);
    }

    /**
     * Runs a country query with the given parameters and maps each row to a Country.
     * All the reports above share this method so the JDBC code is written only once.
     */
    private static List<Country> queryCountries(String sql, Object... params) {
        List<Country> countries = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();
        if (con == null) {
            System.out.println("Database connection is null.");
            return countries;
        }

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    Country country = new Country();
                    country.setCode(rset.getString("Code"));
                    country.setName(rset.getString("Name"));
                    country.setContinent(rset.getString("Continent"));
                    country.setRegion(rset.getString("Region"));
                    country.setPopulation(rset.getLong("Population"));
                    country.setCapital(rset.getString("Capital"));
                    countries.add(country);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database query failed.");
            e.printStackTrace();
        }
        return countries;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void displayCountries(List<Country> countries) {
        System.out.printf("%-5s | %-35s | %-15s | %-25s | %-12s | %-25s%n",
                "Code", "Name", "Continent", "Region", "Population", "Capital");
        System.out.println("-".repeat(130));
        for (Country c : countries) {
            System.out.printf("%-5s | %-35s | %-15s | %-25s | %-12d | %-25s%n",
                    c.getCode(), c.getName(), c.getContinent(), c.getRegion(), c.getPopulation(), c.getCapital());
        }
    }

    public static void main(String[] args) {
        System.out.println("Fetching countries in the world by largest population to smallest...");
        displayCountries(getWorldCountriesByPopulation());

        System.out.println();
        System.out.println("Fetching the top 5 populated countries in Europe...");
        displayCountries(getTopNCountriesByContinent("Europe", 5));

        DatabaseConnection.disconnect();
    }
}