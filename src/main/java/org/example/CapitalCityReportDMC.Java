
package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CapitalCityReport {

    public static List<CapitalCity> getWorldCapitalCitiesByPopulation() {
        return getCapitals("", "", 0);
    }

    public static List<CapitalCity> getCapitalCitiesByContinent(String continent) {
        return getCapitals("country.Continent", continent, 0);
    }

    public static List<CapitalCity> getCapitalCitiesByRegion(String region) {
        return getCapitals("country.Region", region, 0);
    }

    public static List<CapitalCity> getTopNCapitalCitiesInWorld(int n) {
        return getCapitals("", "", n);
    }

    public static List<CapitalCity> getTopNCapitalCitiesByContinent(String continent, int n) {
        return getCapitals("country.Continent", continent, n);
    }

    public static List<CapitalCity> getTopNCapitalCitiesByRegion(String region, int n) {
        return getCapitals("country.Region", region, n);
    }

    private static List<CapitalCity> getCapitals(String filter, String value, int n) {

        List<CapitalCity> list = new ArrayList<>();

        String sql = "SELECT city.Name, country.Name AS Country, city.Population "
                + "FROM country JOIN city ON country.Capital = city.ID ";

        if (!filter.isEmpty()) {
            sql += "WHERE " + filter + " = ? ";
        }

        sql += "ORDER BY city.Population DESC ";

        if (n > 0) {
            sql += "LIMIT ?";
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            if (!filter.isEmpty()) {
                stmt.setString(1, value);
            }

            if (n > 0) {
                if (filter.isEmpty()) {
                    stmt.setInt(1, n);
                } else {
                    stmt.setInt(2, n);
                }
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CapitalCity c = new CapitalCity();

                c.setName(rs.getString("Name"));
                c.setCountry(rs.getString("Country"));
                c.setPopulation(rs.getLong("Population"));

                list.add(c);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Database error.");
        }

        return list;
    }

    public static void displayCapitalCities(List<CapitalCity> list) {

        for (CapitalCity c : list) {
            System.out.println(c.getName() + " | "
                    + c.getCountry() + " | "
                    + c.getPopulation());
        }
    }

    public static void main(String[] args) {

        System.out.println("Capital Cities in the World");
        displayCapitalCities(getWorldCapitalCitiesByPopulation());

        System.out.println("\nCapital Cities in Europe");
        displayCapitalCities(getCapitalCitiesByContinent("Europe"));

        System.out.println("\nTop 5 Capital Cities in the World");
        displayCapitalCities(getTopNCapitalCitiesInWorld(5));

        System.out.println("\nTop 5 Capital Cities in Europe");
        displayCapitalCities(getTopNCapitalCitiesByContinent("Europe", 5));

        DatabaseConnection.Disconnect();
    }
}
