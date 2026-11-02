
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AreaPopulationReport {

    // World population
    public static Map<String, Long> getWorldPopulation() {

        String sql =
                "SELECT 'World' AS Name, "
                + "SUM(Population) AS Population "
                + "FROM country";

        return getData(sql);
    }

    // Population by continent
    public static Map<String, Long> getPopulationByContinent() {

        String sql =
                "SELECT Continent AS Name, "
                + "SUM(Population) AS Population "
                + "FROM country "
                + "GROUP BY Continent "
                + "ORDER BY Continent";

        return getData(sql);
    }

    // Population by region
    public static Map<String, Long> getPopulationByRegion() {

        String sql =
                "SELECT Region AS Name, "
                + "SUM(Population) AS Population "
                + "FROM country "
                + "GROUP BY Region "
                + "ORDER BY Region";

        return getData(sql);
    }

    // Population by country
    public static Map<String, Long> getPopulationByCountry() {

        String sql =
                "SELECT Name, Population "
                + "FROM country "
                + "ORDER BY Name";

        return getData(sql);
    }

    // Population by district
    public static Map<String, Long> getPopulationByDistrict() {

        String sql =
                "SELECT CONCAT(CountryCode, ' - ', District) AS Name, "
                + "SUM(Population) AS Population "
                + "FROM city "
                + "GROUP BY CountryCode, District "
                + "ORDER BY CountryCode, District";

        return getData(sql);
    }

    // Population by city
    public static Map<String, Long> getPopulationByCity() {

        String sql =
                "SELECT CONCAT(Name, ' (', ID, ')') AS Name, "
                + "Population "
                + "FROM city "
                + "ORDER BY Name";

        return getData(sql);
    }

    // Shared database query method
    private static Map<String, Long> getData(String sql) {

        Map<String, Long> data = new LinkedHashMap<>();

        Connection con = DatabaseConnection.getConnection();

        if (con == null) {
            System.err.println("Database connection failed.");
            return data;
        }

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                data.put(
                        rs.getString("Name"),
                        rs.getLong("Population")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving population data:");
            e.printStackTrace();
        }

        return data;
    }

    // Display population results
    public static void displayPopulation(Map<String, Long> data) {

        for (Map.Entry<String, Long> entry : data.entrySet()) {

            System.out.println(
                    entry.getKey() + " | " + entry.getValue()
            );
        }
    }

    public static void main(String[] args) {

        System.out.println("World Population");
        displayPopulation(getWorldPopulation());

        System.out.println("\nPopulation by Continent");
        displayPopulation(getPopulationByContinent());

        System.out.println("\nPopulation by Region");
        displayPopulation(getPopulationByRegion());

        System.out.println("\nPopulation by Country");
        displayPopulation(getPopulationByCountry());

        System.out.println("\nPopulation by District");
        displayPopulation(getPopulationByDistrict());

        System.out.println("\nPopulation by City");
        displayPopulation(getPopulationByCity());

        DatabaseConnection.disconnect();
    }
}
