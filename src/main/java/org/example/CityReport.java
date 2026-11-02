
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CityReport {

    private static final String SELECT_CITIES =
            "SELECT ci.Name AS Name, co.Name AS Country, "
            + "ci.District AS District, ci.Population AS Population "
            + "FROM city ci "
            + "JOIN country co ON ci.CountryCode = co.Code ";

    private static final String ORDER_BY_POPULATION =
            "ORDER BY ci.Population DESC, ci.Name ASC, co.Name ASC ";

    // All cities worldwide
    public static List<City> getWorldCitiesByPopulation() {
        return queryCities(SELECT_CITIES + ORDER_BY_POPULATION);
    }

    // All cities by continent
    public static List<City> getCitiesByContinent(String continent) {
        if (isBlank(continent)) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + "WHERE co.Continent = ? "
                + ORDER_BY_POPULATION, continent.trim());
    }

    // All cities by region
    public static List<City> getCitiesByRegion(String region) {
        if (isBlank(region)) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + "WHERE co.Region = ? "
                + ORDER_BY_POPULATION, region.trim());
    }

    // All cities by country
    public static List<City> getCitiesByCountry(String country) {
        if (isBlank(country)) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + "WHERE co.Name = ? "
                + ORDER_BY_POPULATION, country.trim());
    }

    // All cities by district
    public static List<City> getCitiesByDistrict(String district) {
        if (isBlank(district)) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + "WHERE ci.District = ? "
                + ORDER_BY_POPULATION, district.trim());
    }

    // Top N cities worldwide
    public static List<City> getTopNCitiesInWorld(int n) {
        if (n <= 0) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + ORDER_BY_POPULATION + "LIMIT ?", n);
    }

    // Top N cities by continent
    public static List<City> getTopNCitiesByContinent(
            String continent, int n) {
        if (isBlank(continent) || n <= 0) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + "WHERE co.Continent = ? "
                + ORDER_BY_POPULATION + "LIMIT ?",
                continent.trim(), n);
    }

    // Top N cities by region
    public static List<City> getTopNCitiesByRegion(
            String region, int n) {
        if (isBlank(region) || n <= 0) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + "WHERE co.Region = ? "
                + ORDER_BY_POPULATION + "LIMIT ?",
                region.trim(), n);
    }

    // Top N cities by country
    public static List<City> getTopNCitiesByCountry(
            String country, int n) {
        if (isBlank(country) || n <= 0) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + "WHERE co.Name = ? "
                + ORDER_BY_POPULATION + "LIMIT ?",
                country.trim(), n);
    }

    // Top N cities by district
    public static List<City> getTopNCitiesByDistrict(
            String district, int n) {
        if (isBlank(district) || n <= 0) return new ArrayList<>();

        return queryCities(
                SELECT_CITIES + "WHERE ci.District = ? "
                + ORDER_BY_POPULATION + "LIMIT ?",
                district.trim(), n);
    }

    // Shared database query method
    private static List<City> queryCities(
            String sql, Object... parameters) {

        List<City> cities = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();

        if (con == null) {
            System.err.println("Database connection failed.");
            return cities;
        }

        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    City city = new City();

                    city.setName(rs.getString("Name"));
                    city.setCountry(rs.getString("Country"));
                    city.setDistrict(rs.getString("District"));
                    city.setPopulation(rs.getLong("Population"));

                    cities.add(city);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving city reports:");
            e.printStackTrace();
        }

        return cities;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Display results
    public static void displayCities(List<City> cities) {
        System.out.printf("%-30s | %-30s | %-20s | %-12s%n",
                "City Name", "Country", "District", "Population");

        System.out.println("-".repeat(100));

        for (City city : cities) {
            System.out.printf("%-30s | %-30s | %-20s | %-12d%n",
                    city.getName(),
                    city.getCountry(),
                    city.getDistrict(),
                    city.getPopulation());
        }
    }

    public static void main(String[] args) {
        System.out.println("Top 10 Cities in the World");
        displayCities(getTopNCitiesInWorld(10));

        System.out.println("\nTop 10 Cities in Europe");
        displayCities(getTopNCitiesByContinent("Europe", 10));

        DatabaseConnection.disconnect();
    }
}
