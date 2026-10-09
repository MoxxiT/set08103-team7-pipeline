package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CityReport {

    /**
     * Gets all the cities in the world organised by largest population to smallest.
     */
    public static List<City> getWorldCitiesByPopulation() {
        List<City> cities = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) {
                System.out.println("Database connection is null.");
                return cities;
            }

            Statement stmt = con.createStatement();
            // SQL query joining city and country tables to get the country name
            String strSelect = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                    + "FROM city "
                    + "JOIN country ON city.CountryCode = country.Code "
                    + "ORDER BY city.Population DESC";

            ResultSet rset = stmt.executeQuery(strSelect);

            while (rset.next()) {
                City city = new City();
                city.setName(rset.getString("Name"));
                city.setCountry(rset.getString("Country"));
                city.setDistrict(rset.getString("District"));
                city.setPopulation(rset.getLong("Population"));
                cities.add(city);
            }
        } catch (SQLException e) {
            System.out.println("Database query failed.");
            e.printStackTrace();
        }
        return cities;
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
        System.out.println("Fetching cities in the world by largest population to smallest...");
        List<City> cities = getWorldCitiesByPopulation();
        displayCities(cities);
        DatabaseConnection.disconnect();
    }
}