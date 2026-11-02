package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Single-figure population lookups.
 *
 * Brief items covered: the population of the world, a continent, a region,
 * a country, a district and a city.
 *
 * Assumptions (also documented in the README):
 *  - World, continent, region and country populations come from country.Population.
 *  - The database has no district table, so a district's population is the sum of the
 *    populations of the cities recorded in that district.
 *  - A city's population comes from city.Population. City names are not unique in the
 *    data (e.g. three cities are called Valencia), so getCityPopulation(name) returns the
 *    most populated match; use getCityPopulation(name, country) to be specific.
 *  - Every lookup returns 0 for null, blank or unknown input.
 */
public class AreaPopulationReport {

    /** The population of the world. */
    public static long getWorldPopulation() {
        return queryPopulation("SELECT SUM(Population) FROM country");
    }

    /** The population of a continent. */
    public static long getContinentPopulation(String continent) {
        if (isBlank(continent)) return 0;
        return queryPopulation("SELECT SUM(Population) FROM country WHERE Continent = ?", continent);
    }

    /** The population of a region. */
    public static long getRegionPopulation(String region) {
        if (isBlank(region)) return 0;
        return queryPopulation("SELECT SUM(Population) FROM country WHERE Region = ?", region);
    }

    /** The population of a country (matched by country name). */
    public static long getCountryPopulation(String country) {
        if (isBlank(country)) return 0;
        return queryPopulation("SELECT SUM(Population) FROM country WHERE Name = ?", country);
    }

    /** The population of a district: the sum of the cities recorded in it. */
    public static long getDistrictPopulation(String district) {
        if (isBlank(district)) return 0;
        return queryPopulation("SELECT SUM(Population) FROM city WHERE District = ?", district);
    }

    /** The population of a city. If several cities share the name, the largest one is returned. */
    public static long getCityPopulation(String city) {
        if (isBlank(city)) return 0;
        return queryPopulation("SELECT MAX(Population) FROM city WHERE Name = ?", city);
    }

    /** The population of a city in a specific country (use this when the name is ambiguous). */
    public static long getCityPopulation(String city, String country) {
        if (isBlank(city) || isBlank(country)) return 0;
        return queryPopulation("SELECT MAX(ci.Population) FROM city ci "
                + "JOIN country c ON ci.CountryCode = c.Code "
                + "WHERE ci.Name = ? AND c.Name = ?", city, country);
    }

    /**
     * Runs a query that returns a single number. SUM and MAX return NULL when nothing
     * matches, which getLong turns into 0, so unknown areas give 0.
     */
    private static long queryPopulation(String sql, Object... params) {
        Connection con = DatabaseConnection.getConnection();
        if (con == null) {
            System.out.println("Database connection is null.");
            return 0;
        }

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rset = stmt.executeQuery()) {
                return rset.next() ? rset.getLong(1) : 0;
            }
        } catch (SQLException e) {
            System.out.println("Database query failed.");
            e.printStackTrace();
            return 0;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void displayPopulation(String area, long population) {
        System.out.printf("%-25s | %,15d%n", area, population);
    }

    public static void main(String[] args) {
        System.out.printf("%-25s | %15s%n", "Area", "Population");
        System.out.println("-".repeat(43));
        displayPopulation("World", getWorldPopulation());
        displayPopulation("Europe (continent)", getContinentPopulation("Europe"));
        displayPopulation("Western Europe (region)", getRegionPopulation("Western Europe"));
        displayPopulation("Japan (country)", getCountryPopulation("Japan"));
        displayPopulation("California (district)", getDistrictPopulation("California"));
        displayPopulation("Tokyo (city)", getCityPopulation("Tokyo"));
        DatabaseConnection.disconnect();
    }
}