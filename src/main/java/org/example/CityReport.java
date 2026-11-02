package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * City reports. Every report is ordered from the largest to the smallest population
 * (ties broken alphabetically so the order is always the same).
 *
 * Brief items covered:
 *  - all the cities in the world / a continent / a region / a country / a district
 *  - the top N cities in the world / a continent / a region / a country / a district
 */
public class CityReport {

    // Columns required by the brief: Name, Country, District, Population.
    private static final String SELECT_CITIES =
            "SELECT ci.Name, c.Name AS Country, ci.District, ci.Population "
                    + "FROM city ci "
                    + "JOIN country c ON ci.CountryCode = c.Code ";

    private static final String ORDER_BY_POPULATION = "ORDER BY ci.Population DESC, ci.Name ASC ";

    // ----- all cities -----

    /**
     * Gets all the cities in the world organised by largest population to smallest.
     */
    public static List<City> getWorldCitiesByPopulation() {
        return queryCities(SELECT_CITIES + ORDER_BY_POPULATION);
    }

    /**
     * Gets all the cities in a continent organised by largest population to smallest.
     * Returns an empty list if the continent is null, blank or unknown.
     */
    public static List<City> getCitiesByContinent(String continent) {
        if (isBlank(continent)) return new ArrayList<>();
        return queryCities(SELECT_CITIES + "WHERE c.Continent = ? " + ORDER_BY_POPULATION, continent);
    }

    /**
     * Gets all the cities in a region organised by largest population to smallest.
     * Returns an empty list if the region is null, blank or unknown.
     */
    public static List<City> getCitiesByRegion(String region) {
        if (isBlank(region)) return new ArrayList<>();
        return queryCities(SELECT_CITIES + "WHERE c.Region = ? " + ORDER_BY_POPULATION, region);
    }

    /**
     * Gets all the cities in a country (matched by country name) organised by largest
     * population to smallest. Returns an empty list if the country is null, blank or unknown.
     */
    public static List<City> getCitiesByCountry(String country) {
        if (isBlank(country)) return new ArrayList<>();
        return queryCities(SELECT_CITIES + "WHERE c.Name = ? " + ORDER_BY_POPULATION, country);
    }

    /**
     * Gets all the cities in a district organised by largest population to smallest.
     * Returns an empty list if the district is null, blank or unknown.
     */
    public static List<City> getCitiesByDistrict(String district) {
        if (isBlank(district)) return new ArrayList<>();
        return queryCities(SELECT_CITIES + "WHERE ci.District = ? " + ORDER_BY_POPULATION, district);
    }

    // ----- top N cities -----

    /**
     * Gets the top N populated cities in the world.
     * Returns an empty list if n is zero or negative.
     */
    public static List<City> getTopNCitiesInWorld(int n) {
        if (n <= 0) return new ArrayList<>();
        return queryCities(SELECT_CITIES + ORDER_BY_POPULATION + "LIMIT ?", n);
    }

    /**
     * Gets the top N populated cities in a continent.
     * Returns an empty list if the continent is null/blank/unknown or n is zero or negative.
     */
    public static List<City> getTopNCitiesByContinent(String continent, int n) {
        if (isBlank(continent) || n <= 0) return new ArrayList<>();
        return queryCities(SELECT_CITIES + "WHERE c.Continent = ? " + ORDER_BY_POPULATION + "LIMIT ?",
                continent, n);
    }

    /**
     * Gets the top N populated cities in a region.
     * Returns an empty list if the region is null/blank/unknown or n is zero or negative.
     */
    public static List<City> getTopNCitiesByRegion(String region, int n) {
        if (isBlank(region) || n <= 0) return new ArrayList<>();
        return queryCities(SELECT_CITIES + "WHERE c.Region = ? " + ORDER_BY_POPULATION + "LIMIT ?",
                region, n);
    }

    /**
     * Gets the top N populated cities in a country (matched by country name).
     * Returns an empty list if the country is null/blank/unknown or n is zero or negative.
     */
    public static List<City> getTopNCitiesByCountry(String country, int n) {
        if (isBlank(country) || n <= 0) return new ArrayList<>();
        return queryCities(SELECT_CITIES + "WHERE c.Name = ? " + ORDER_BY_POPULATION + "LIMIT ?",
                country, n);
    }

    /**
     * Gets the top N populated cities in a district.
     * Returns an empty list if the district is null/blank/unknown or n is zero or negative.
     */
    public static List<City> getTopNCitiesByDistrict(String district, int n) {
        if (isBlank(district) || n <= 0) return new ArrayList<>();
        return queryCities(SELECT_CITIES + "WHERE ci.District = ? " + ORDER_BY_POPULATION + "LIMIT ?",
                district, n);
    }

    // ----- shared helpers -----

    /**
     * Runs a city query with the given parameters and maps each row to a City.
     * All the reports above share this method so the JDBC code is written only once.
     */
    private static List<City> queryCities(String sql, Object... params) {
        List<City> cities = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();
        if (con == null) {
            System.out.println("Database connection is null.");
            return cities;
        }

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    City city = new City();
                    city.setName(rset.getString("Name"));
                    city.setCountry(rset.getString("Country"));
                    city.setDistrict(rset.getString("District"));
                    city.setPopulation(rset.getLong("Population"));
                    cities.add(city);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database query failed.");
            e.printStackTrace();
        }
        return cities;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void displayCities(List<City> cities) {
        System.out.printf("%-30s | %-30s | %-20s | %-12s%n",
                "City Name", "Country", "District", "Population");
        System.out.println("-".repeat(100));
        for (City c : cities) {
            System.out.printf("%-30s | %-30s | %-20s | %-12d%n",
                    c.getName(), c.getCountry(), c.getDistrict(), c.getPopulation());
        }
    }

    public static void main(String[] args) {
        System.out.println("Top 5 populated cities in the world...");
        displayCities(getTopNCitiesInWorld(5));

        System.out.println();
        System.out.println("Top 5 populated cities in Japan...");
        displayCities(getTopNCitiesByCountry("Japan", 5));

        DatabaseConnection.disconnect();
    }
}