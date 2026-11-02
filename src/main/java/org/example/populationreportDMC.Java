
package org.example;

import java.sql.*;
import java.util.*;

public class PopulationReport {

    public static List<PopulationComposition> getCountryPopulationComposition() {

        String sql = "SELECT c.Name AS Name, c.Population AS Total, "
                + "COALESCE(SUM(ci.Population), 0) AS Urban "
                + "FROM country c LEFT JOIN city ci ON c.Code = ci.CountryCode "
                + "GROUP BY c.Code, c.Name, c.Population";

        return getComposition(sql, true);
    }

    public static List<PopulationComposition> getContinentPopulationComposition() {

        String sql = "SELECT c.Continent AS Name, SUM(c.Population) AS Total, "
                + "SUM(COALESCE(x.Urban, 0)) AS Urban "
                + "FROM country c LEFT JOIN "
                + "(SELECT CountryCode, SUM(Population) AS Urban FROM city GROUP BY CountryCode) x "
                + "ON c.Code = x.CountryCode GROUP BY c.Continent";

        return getComposition(sql, false);
    }

    public static List<PopulationComposition> getRegionPopulationComposition() {

        String sql = "SELECT c.Region AS Name, SUM(c.Population) AS Total, "
                + "SUM(COALESCE(x.Urban, 0)) AS Urban "
                + "FROM country c LEFT JOIN "
                + "(SELECT CountryCode, SUM(Population) AS Urban FROM city GROUP BY CountryCode) x "
                + "ON c.Code = x.CountryCode GROUP BY c.Region";

        return getComposition(sql, false);
    }

    private static List<PopulationComposition> getComposition(String sql, boolean country) {

        List<PopulationComposition> list = new ArrayList<>();

        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) return list;

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                long total = rs.getLong("Total");
                long urban = rs.getLong("Urban");
                long rural = total - urban;

                if (rural < 0) rural = 0;

                double urbanPercent = 0;
                double ruralPercent = 0;

                if (total > 0) {
                    urbanPercent = urban * 100.0 / total;
                    ruralPercent = rural * 100.0 / total;
                }

                PopulationComposition p = new PopulationComposition();

                p.setName(rs.getString("Name"));
                p.setTotalPopulation(total);
                p.setUrbanPopulation(urban);
                p.setRuralPopulation(rural);
                p.setUrbanPercentage(urbanPercent);
                p.setRuralPercentage(ruralPercent);

                list.add(p);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Database error.");
        }

        return list;
    }
    public static void displayComposition(List<PopulationComposition> list) {

        for (PopulationComposition p : list) {
            System.out.println(p.getName() + " | "
                    + p.getTotalPopulation() + " | "
                    + p.getUrbanPopulation() + " | "
                    + p.getRuralPopulation() + " | "
                    + p.getUrbanPercentage() + "% | "
                    + p.getRuralPercentage() + "%");
        }
    }

    public static Map<String, Long> getWorldPopulation() {
        return getData("SELECT 'World' AS Name, SUM(Population) AS Population FROM country");
    }

    public static Map<String, Long> getPopulationByContinent() {
        return getData("SELECT Continent AS Name, SUM(Population) AS Population FROM country GROUP BY Continent");
    }

    public static Map<String, Long> getPopulationByRegion() {
        return getData("SELECT Region AS Name, SUM(Population) AS Population FROM country GROUP BY Region");
    }

    public static Map<String, Long> getPopulationByCountry() {
        return getData("SELECT Name, Population FROM country");
    }

    public static Map<String, Long> getPopulationByDistrict() {
        return getData("SELECT CONCAT(CountryCode, ' - ', District) AS Name, "
                + "SUM(Population) AS Population FROM city GROUP BY CountryCode, District");
    }

    public static Map<String, Long> getPopulationByCity() {
        return getData("SELECT CONCAT(Name, ' (', ID, ')') AS Name, Population FROM city");
    }

    private static Map<String, Long> getData(String sql) {

        Map<String, Long> data = new LinkedHashMap<>();

        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) return data;

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                data.put(rs.getString("Name"), rs.getLong("Population"));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Database error.");
        }

        return data;
    }

    public static void displayPopulation(Map<String, Long> data) {

        for (String name : data.keySet()) {
            System.out.println(name + " | " + data.get(name));
        }
    }

    public static void main(String[] args) {

        System.out.println("Population by Country");
        displayComposition(getCountryPopulationComposition());

        System.out.println("\nPopulation by Continent");
        displayComposition(getContinentPopulationComposition());

        System.out.println("\nPopulation by Region");
        displayComposition(getRegionPopulationComposition());

        System.out.println("\nWorld Population");
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

        DatabaseConnection.Disconnect();
    }
}
