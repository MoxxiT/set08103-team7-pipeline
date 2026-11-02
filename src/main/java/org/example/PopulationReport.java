
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PopulationReport {

    public static List<PopulationComposition> getCountryPopulationComposition() {

        String sql =
                "SELECT c.Name AS Name, c.Population AS Total, "
                + "COALESCE(city_pop.Urban, 0) AS Urban "
                + "FROM country c "
                + "LEFT JOIN ("
                + "SELECT CountryCode, SUM(Population) AS Urban "
                + "FROM city GROUP BY CountryCode"
                + ") city_pop ON c.Code = city_pop.CountryCode "
                + "ORDER BY c.Population DESC";

        return getComposition(sql);
    }

    public static List<PopulationComposition> getContinentPopulationComposition() {

        String sql =
                "SELECT c.Continent AS Name, "
                + "SUM(c.Population) AS Total, "
                + "SUM(COALESCE(city_pop.Urban, 0)) AS Urban "
                + "FROM country c "
                + "LEFT JOIN ("
                + "SELECT CountryCode, SUM(Population) AS Urban "
                + "FROM city GROUP BY CountryCode"
                + ") city_pop ON c.Code = city_pop.CountryCode "
                + "GROUP BY c.Continent "
                + "ORDER BY Total DESC";

        return getComposition(sql);
    }

    public static List<PopulationComposition> getRegionPopulationComposition() {

        String sql =
                "SELECT c.Region AS Name, "
                + "SUM(c.Population) AS Total, "
                + "SUM(COALESCE(city_pop.Urban, 0)) AS Urban "
                + "FROM country c "
                + "LEFT JOIN ("
                + "SELECT CountryCode, SUM(Population) AS Urban "
                + "FROM city GROUP BY CountryCode"
                + ") city_pop ON c.Code = city_pop.CountryCode "
                + "GROUP BY c.Region "
                + "ORDER BY Total DESC";

        return getComposition(sql);
    }
    private static List<PopulationComposition> getComposition(String sql) {

        List<PopulationComposition> list = new ArrayList<>();

        Connection con = DatabaseConnection.getConnection();

        if (con == null) {
            System.err.println("Database connection failed.");
            return list;
        }

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                list.add(buildComposition(
                        rs.getString("Name"),
                        rs.getLong("Total"),
                        rs.getLong("Urban")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving population composition:");
            e.printStackTrace();
        }

        return list;
    }

    public static PopulationComposition buildComposition(
            String name, long total, long urban) {

        // Prevent negative population values
        if (total < 0) {
            total = 0;
        }

        if (urban < 0) {
            urban = 0;
        }

        if (urban > total) {
            urban = total;
        }

        long rural = total - urban;

        double urbanPercentage = 0;
        double ruralPercentage = 0;

        if (total > 0) {
            urbanPercentage = (urban * 100.0) / total;
            ruralPercentage = (rural * 100.0) / total;
        }

        PopulationComposition composition = new PopulationComposition();

        composition.setName(name);
        composition.setTotalPopulation(total);
        composition.setUrbanPopulation(urban);
        composition.setRuralPopulation(rural);
        composition.setUrbanPercentage(urbanPercentage);
        composition.setRuralPercentage(ruralPercentage);

        return composition;
    }

    public static void displayComposition(
            List<PopulationComposition> list) {

        System.out.printf(
                "%-35s | %-15s | %-15s | %-15s | %-10s | %-10s%n",
                "Name", "Total", "Urban", "Rural",
                "Urban %", "Rural %"
        );

        System.out.println("-".repeat(115));

        for (PopulationComposition p : list) {

            System.out.printf(
                    "%-35s | %-15d | %-15d | %-15d | %-9.2f%% | %-9.2f%%%n",
                    p.getName(),
                    p.getTotalPopulation(),
                    p.getUrbanPopulation(),
                    p.getRuralPopulation(),
                    p.getUrbanPercentage(),
                    p.getRuralPercentage()
            );
        }
    }

    public static void main(String[] args) {

        System.out.println("Population Composition by Country");
        displayComposition(getCountryPopulationComposition());

        System.out.println("\nPopulation Composition by Continent");
        displayComposition(getContinentPopulationComposition());

        System.out.println("\nPopulation Composition by Region");
        displayComposition(getRegionPopulationComposition());

        DatabaseConnection.disconnect();
    }
}