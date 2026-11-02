
package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PopulationReport {

    // Country population report
    public static List<PopulationComposition> getCountryPopulationComposition() {
        List<PopulationComposition> list = new ArrayList<>();

        String query =
                "SELECT c.Name AS CountryName, c.Population AS TotalPop, " +
                        "COALESCE(SUM(ci.Population), 0) AS UrbanPop " +
                        "FROM country c " +
                        "LEFT JOIN city ci ON c.Code = ci.CountryCode " +
                        "GROUP BY c.Code, c.Name, c.Population " +
                        "ORDER BY c.Population DESC";

        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) return list;

            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    list.add(buildComposition(
                            rs.getString("CountryName"),
                            rs.getLong("TotalPop"),
                            rs.getLong("UrbanPop")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Continent population report
    public static List<PopulationComposition> getContinentPopulationComposition() {
        String query =
                "SELECT c.Continent AS Name, " +
                        "SUM(c.Population) AS TotalPop, " +
                        "COALESCE(SUM(city_pop.UrbanPop), 0) AS UrbanPop " +
                        "FROM country c " +
                        "LEFT JOIN (" +
                        " SELECT CountryCode, SUM(Population) AS UrbanPop " +
                        " FROM city GROUP BY CountryCode" +
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
                        " SELECT CountryCode, SUM(Population) AS UrbanPop " +
                        " FROM city GROUP BY CountryCode" +
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

            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    list.add(buildComposition(
                            rs.getString("Name"),
                            rs.getLong("TotalPop"),
                            rs.getLong("UrbanPop")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Builds a PopulationComposition from raw totals.
     * Kept separate from database code so it can be unit tested.
     * Urban percentage is capped at 100 because some countries
     * have more city population than country population in the dataset.
     */
    static PopulationComposition buildComposition(
            String name, long totalPop, long urbanPop) {

        long ruralPop = Math.max(0, totalPop - urbanPop);

        double urbanPct = totalPop > 0
                ? Math.min(100.0, (double) urbanPop / totalPop * 100.0)
                : 0.0;

        double ruralPct = totalPop > 0
                ? (double) ruralPop / totalPop * 100.0
                : 0.0;

        PopulationComposition comp = new PopulationComposition();
        comp.setName(name);
        comp.setTotalPopulation(totalPop);
        comp.setUrbanPopulation(urbanPop);
        comp.setUrbanPercentage(urbanPct);
        comp.setRuralPopulation(ruralPop);
        comp.setRuralPercentage(ruralPct);

        return comp;
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
