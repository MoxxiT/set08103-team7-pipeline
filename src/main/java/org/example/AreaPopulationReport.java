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

    /** Not used: this is a utility class with only static methods, so it is never instantiated. */
    private AreaPopulationReport() {
    }

    /**
     * Gets the population of the world.
     *
     * @return the world population
     */
    public static long getWorldPopulation() {
        return queryPopulation("SELECT SUM(Population) FROM country");
    }

    /**
     * Gets the population of a continent.
     *
     * @param continent the continent name, e.g. "Europe"
     * @return the population, or 0 if the continent is null, blank or unknown
     */
    public static long getContinentPopulation(String continent) {
        if (isBlank(continent)) return 0;
        return queryPopulation("SELECT SUM(Population) FROM country WHERE Continent = ?", continent);
    }

    /**
     * Gets the population of a region.
     *
     * @param region the region name, e.g. "Western Europe"
     * @return the population, or 0 if the region is null, blank or unknown
     */
    public static long getRegionPopulation(String region) {
        if (isBlank(region)) return 0;
        return queryPopulation("SELECT SUM(Population) FROM country WHERE Region = ?", region);
    }

    /**
     * Gets the population of a country (matched by country name).
     *
     * @param country the country name, e.g. "Japan"
     * @return the population, or 0 if the country is null, blank or unknown
     */
    public static long getCountryPopulation(String country) {
        if (isBlank(country)) return 0;
        return queryPopulation("SELECT SUM(Population) FROM country WHERE Name = ?", country);
    }

    /**
     * Gets the population of a district: the sum of the cities recorded in it.
     *
     * @param district the district name, e.g. "California"
     * @return the population, or 0 if the district is null, blank or unknown
     */
    public static long getDistrictPopulation(String district) {
        if (isBlank(district)) return 0;
        return queryPopulation("SELECT SUM(Population) FROM city WHERE District = ?", district);
    }

    /**
     * Gets the population of a city. If several cities share the name, the
     * largest one is returned.
     *
     * @param city the city name, e.g. "Tokyo"
     * @return the population, or 0 if the city is null, blank or unknown
     */
    public static long getCityPopulation(String city) {
        if (isBlank(city)) return 0;
        return queryPopulation("SELECT MAX(Population) FROM city WHERE Name = ?", city);
    }

    /**
     * Gets the population of a city in a specific country. Use this when the
     * city name is shared by cities in different countries.
     *
     * @param city    the city name, e.g. "Valencia"
     * @param country the country name, e.g. "Spain"
     * @return the population, or 0 if either argument is null, blank or unknown
     */
    public static long getCityPopulation(String city, String country) {
        if (isBlank(city) || isBlank(country)) return 0;
        return queryPopulation("SELECT MAX(ci.Population) FROM city ci "
                + "JOIN country c ON ci.CountryCode = c.Code "
                + "WHERE ci.Name = ? AND c.Name = ?", city, country);
    }

    /**
     * Runs a query that returns a single number. SUM and MAX return NULL when nothing
     * matches, which getLong turns into 0, so unknown areas give 0.
     *
     * @param sql    the SQL query, using ? for each parameter
     * @param params the values for the ? placeholders, in order
     * @return the number returned by the query, or 0 on no match or error
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

    /**
     * Checks whether a string is null, empty or only whitespace.
     *
     * @param value the string to check
     * @return true if the value is null or blank
     */
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Prints one area and its population as a row of the population table.
     *
     * @param area       the label to show for the area
     * @param population the population to show
     */
    public static void displayPopulation(String area, long population) {
        System.out.printf("%-25s | %,15d%n", area, population);
    }

    /**
     * Runs an example of each population lookup and prints the results.
     *
     * @param args not used
     */
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