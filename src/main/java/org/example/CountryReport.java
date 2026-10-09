package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CountryReport {

    /**
     * Gets all the countries in the world organised by largest population to smallest.
     */
    public static List<Country> getWorldCountriesByPopulation() {
        List<Country> countries = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) {
                System.out.println("Database connection is null.");
                return countries;
            }

            Statement stmt = con.createStatement();
            // SQL query matching the required columns and ordering (largest to smallest)
            String strSelect = "SELECT Code, Name, Continent, Region, Population, Capital "
                    + "FROM country "
                    + "ORDER BY Population DESC";

            ResultSet rset = stmt.executeQuery(strSelect);

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
        } catch (SQLException e) {
            System.out.println("Database query failed.");
            e.printStackTrace();
        }
        return countries;
    }

    public static void displayCountries(List<Country> countries) {
        System.out.printf("%-5s | %-35s | %-15s | %-25s | %-12s | %-10s%n",
                "Code", "Name", "Continent", "Region", "Population", "Capital");
        System.out.println("-".repeat(115));
        for (Country c : countries) {
            System.out.printf("%-5s | %-35s | %-15s | %-25s | %-12d | %-10s%n",
                    c.getCode(), c.getName(), c.getContinent(), c.getRegion(), c.getPopulation(), c.getCapital());
        }
    }

    public static void main(String[] args) {
        System.out.println("Fetching countries in the world by largest population to smallest...");
        List<Country> countries = getWorldCountriesByPopulation();
        displayCountries(countries);
        DatabaseConnection.disconnect();
    }
}