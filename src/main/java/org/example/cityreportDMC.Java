package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CityReport {

    public static List<City> getWorldCitiesByPopulation() {
        List<City> cities = new ArrayList<>();
        String query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                     + "FROM city JOIN country ON city.CountryCode = country.Code "
                     + "ORDER BY city.Population DESC";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                City city = new City();
                city.setName(rs.getString("Name"));
                city.setCountry(rs.getString("Country"));
                city.setDistrict(rs.getString("District"));
                city.setPopulation(rs.getLong("Population"));
                cities.add(city);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public static List<City> getCitiesByContinent(String continent) {
        List<City> cities = new ArrayList<>();
        String query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                     + "FROM city JOIN country ON city.CountryCode = country.Code "
                     + "WHERE country.Continent = ? "
                     + "ORDER BY city.Population DESC";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, continent);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                City city = new City();
                city.setName(rs.getString("Name"));
                city.setCountry(rs.getString("Country"));
                city.setDistrict(rs.getString("District"));
                city.setPopulation(rs.getLong("Population"));
                cities.add(city);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public static List<City> getCitiesByRegion(String region) {
        List<City> cities = new ArrayList<>();
        String query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                     + "FROM city JOIN country ON city.CountryCode = country.Code "
                     + "WHERE country.Region = ? "
                     + "ORDER BY city.Population DESC";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, region);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                City city = new City();
                city.setName(rs.getString("Name"));
                city.setCountry(rs.getString("Country"));
                city.setDistrict(rs.getString("District"));
                city.setPopulation(rs.getLong("Population"));
                cities.add(city);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }
    public static List<City> getCitiesByCountry(String country) {
        List<City> cities = new ArrayList<>();
        String query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                     + "FROM city JOIN country ON city.CountryCode = country.Code "
                     + "WHERE country.Name = ? "
                     + "ORDER BY city.Population DESC";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, country);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                City city = new City();
                city.setName(rs.getString("Name"));
                city.setCountry(rs.getString("Country"));
                city.setDistrict(rs.getString("District"));
                city.setPopulation(rs.getLong("Population"));
                cities.add(city);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public static List<City> getTopNCitiesInWorld(int n) {
        List<City> cities = new ArrayList<>();
        if (n <= 0) return cities;

        String query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                     + "FROM city JOIN country ON city.CountryCode = country.Code "
                     + "ORDER BY city.Population DESC LIMIT ?";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, n);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                City city = new City();
                city.setName(rs.getString("Name"));
                city.setCountry(rs.getString("Country"));
                city.setDistrict(rs.getString("District"));
                city.setPopulation(rs.getLong("Population"));
                cities.add(city);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public static List<City> getTopNCitiesByContinent(String continent, int n) {
        List<City> cities = new ArrayList<>();
        if (n <= 0) return cities;

        String query = "SELECT city.Name, country.Name AS Country, city.District, city.Population "
                     + "FROM city JOIN country ON city.CountryCode = country.Code "
                     + "WHERE country.Continent = ? "
                     + "ORDER BY city.Population DESC LIMIT ?";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, continent);
            stmt.setInt(2, n);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                City city = new City();
                city.setName(rs.getString("Name"));
                city.setCountry(rs.getString("Country"));
                city.setDistrict(rs.getString("District"));
                city.setPopulation(rs.getLong("Population"));
                cities.add(city);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

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

        DatabaseConnection.Disconnect();
    }
}