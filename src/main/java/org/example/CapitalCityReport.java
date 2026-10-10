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