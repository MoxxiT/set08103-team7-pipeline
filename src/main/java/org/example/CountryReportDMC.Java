
package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CountryReport {

    public static List<Country> getWorldCountriesByPopulation() {
        return getCountries("", "", 0);
    }

    public static List<Country> getCountriesByContinent(String continent) {
        return getCountries("c.Continent", continent, 0);
    }

    public static List<Country> getCountriesByRegion(String region) {
        return getCountries("c.Region", region, 0);
    }

    public static List<Country> getTopNCountriesInWorld(int n) {
        return getCountries("", "", n);
    }

    public static List<Country> getTopNCountriesByContinent(String continent, int n) {
        return getCountries("c.Continent", continent, n);
    }

    public static List<Country> getTopNCountriesByRegion(String region, int n) {
        return getCountries("c.Region", region, n);
    }

    private static List<Country> getCountries(String filter, String value, int n) {

        List<Country> list = new ArrayList<>();

        String sql = "SELECT c.Code, c.Name, c.Continent, c.Region, "
                + "c.Population, COALESCE(cap.Name, 'N/A') AS Capital "
                + "FROM country c LEFT JOIN city cap ON c.Capital = cap.ID ";

        if (!filter.isEmpty()) {
            sql += "WHERE " + filter + " = ? ";
        }

        sql += "ORDER BY c.Population DESC ";

        if (n > 0) {
            sql += "LIMIT ?";
        }

        try {
            Connection con = DatabaseConnection.getConnection();

            if (con == null) {
                System.out.println("Connection failed.");
                return list;
            }

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

                Country c = new Country();

                c.setCode(rs.getString("Code"));
                c.setName(rs.getString("Name"));
                c.setContinent(rs.getString("Continent"));
                c.setRegion(rs.getString("Region"));
                c.setPopulation(rs.getLong("Population"));
                c.setCapital(rs.getString("Capital"));

                list.add(c);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Database error.");
        }

        return list;
    }

    public static void displayCountries(List<Country> list) {

        for (Country c : list) {
            System.out.println(c.getName() + " | "
                    + c.getContinent() + " | "
                    + c.getRegion() + " | "
                    + c.getPopulation() + " | "
                    + c.getCapital());
        }
    }

    public static void main(String[] args) {

        System.out.println("Countries in the World");
        displayCountries(getWorldCountriesByPopulation());

        System.out.println("\nTop 5 Countries in Europe");
        displayCountries(getTopNCountriesByContinent("Europe", 5));

        System.out.println("\nTop 5 Countries in Asia");
        displayCountries(getTopNCountriesByContinent("Asia", 5));

        DatabaseConnection.disconnect();
    }
}
