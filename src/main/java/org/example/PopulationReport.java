
package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PopulationReport {

    // Country population report
    public static List<PopulationComposition> getCountryPopulationComposition() {
        List<PopulationComposition> list = new ArrayList<>();

        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) return list;

            Statement stmt = con.createStatement();

            String query =
                    "SELECT c.Name AS CountryName, c.Population AS TotalPop, " +
                            "COALESCE(SUM(ci.Population), 0) AS UrbanPop " +
                            "FROM country c " +
                            "LEFT JOIN city ci ON c.Code = ci.CountryCode " +
                            "GROUP BY c.Code, c.Name, c.Population " +
                            "ORDER BY c.Population DESC";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                long total = rs.getLong("TotalPop");
                long urban = rs.getLong("UrbanPop");
                long rural = Math.max(0, total - urban);

                PopulationComposition p = new PopulationComposition();
                p.setName(rs.getString("CountryName"));
                p.setTotalPopulation(total);
                p.setUrbanPopulation(urban);
                p.setRuralPopulation(rural);
                p.setUrbanPercentage(total > 0 ? urban * 100.0 / total : 0);
                p.setRuralPercentage(total > 0 ? rural * 100.0 / total : 0);

                list.add(p);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Continent population report
    public static List<PopulationComposition> getContinentPopulationComposition() {
        List<PopulationComposition> list = new ArrayList<>();

        String query =
                "SELECT c.Continent AS Name, " +
                        "SUM(c.Population) AS TotalPop, " +
                        "COALESCE(SUM(city_pop.UrbanPop), 0) AS UrbanPop " +
                        "FROM country c " +
                        "LEFT JOIN (" +
                        "  SELECT CountryCode, SUM(Population) AS UrbanPop " +
                        "  FROM city GROUP BY CountryCode" +
                        ") city_pop ON c.Code = city_pop.CountryCode " +
                        "GROUP BY c.Continent " +
                        "ORDER BY TotalPop DESC";

        return getPopulationComposition(query);
    }

    // Region population report
    public static List<PopulationComposition> getRegionPopulationComposition() {
        String query =
                "SELECT c.Region AS Name, " +
                        "SUM(c.Population) AS TotalPop, " +
                        "COALESCE(SUM(city_pop.UrbanPop), 0) AS UrbanPop " +
                        "FROM country c " +
                        "LEFT JOIN (" +
                        "  SELECT CountryCode, SUM(Population) AS UrbanPop " +
                        "  FROM city GROUP BY CountryCode" +
                        ") city_pop ON c.Code = city_pop.CountryCode " +
                        "GROUP BY c.Region " +
                        "ORDER BY TotalPop DESC";

        return getPopulationComposition(query);
    }

    // Shared helper for continent and region reports
    private static List<PopulationComposition> getPopulationComposition(String query) {
        List<PopulationComposition> list = new ArrayList<>();

        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) return list;

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                long total = rs.getLong("TotalPop");
                long urban = rs.getLong("UrbanPop");
                long rural = Math.max(0, total - urban);

                PopulationComposition p = new PopulationComposition();
                p.setName(rs.getString("Name"));
                p.setTotalPopulation(total);
                p.setUrbanPopulation(urban);
                p.setRuralPopulation(rural);
                p.setUrbanPercentage(total > 0 ? urban * 100.0 / total : 0);
                p.setRuralPercentage(total > 0 ? rural * 100.0 / total : 0);

                list.add(p);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void displayComposition(List<PopulationComposition> list) {
        System.out.printf("%-35s | %-15s | %-18s | %-18s%n",
                "Country Name", "Total Pop", "Living in Cities", "Not in Cities");

        System.out.println("-".repeat(95));

        for (PopulationComposition p : list) {
            System.out.printf("%-35s | %-15d | %-10d (%5.1f%%) | %-10d (%5.1f%%)%n",
                    p.getName(),
                    p.getTotalPopulation(),
                    p.getUrbanPopulation(),
                    p.getUrbanPercentage(),
                    p.getRuralPopulation(),
                    p.getRuralPercentage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Fetching Country Population Composition Report...");

        List<PopulationComposition> report =
                getCountryPopulationComposition();

        displayComposition(report);
        DatabaseConnection.disconnect();
    }
}
