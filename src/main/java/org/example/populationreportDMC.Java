
package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PopulationReport {

    public static List<PopulationComposition> getCountryPopulationComposition() {

        String sql = "SELECT c.Name AS Name, c.Population AS Total, "
                + "COALESCE(SUM(ci.Population), 0) AS Urban "
                + "FROM country c LEFT JOIN city ci ON c.Code = ci.CountryCode "
                + "GROUP BY c.Code, c.Name, c.Population";

        return getData(sql);
    }

    public static List<PopulationComposition> getContinentPopulationComposition() {

        String sql = "SELECT c.Continent AS Name, SUM(c.Population) AS Total, "
                + "SUM(COALESCE(x.Urban, 0)) AS Urban "
                + "FROM country c LEFT JOIN "
                + "(SELECT CountryCode, SUM(Population) AS Urban "
                + "FROM city GROUP BY CountryCode) x "
                + "ON c.Code = x.CountryCode GROUP BY c.Continent";

        return getData(sql);
    }

    public static List<PopulationComposition> getRegionPopulationComposition() {

        String sql = "SELECT c.Region AS Name, SUM(c.Population) AS Total, "
                + "SUM(COALESCE(x.Urban, 0)) AS Urban "
                + "FROM country c LEFT JOIN "
                + "(SELECT CountryCode, SUM(Population) AS Urban "
                + "FROM city GROUP BY CountryCode) x "
                + "ON c.Code = x.CountryCode GROUP BY c.Region";

        return getData(sql);
    }

    public static List<PopulationComposition> getData(String sql) {

        List<PopulationComposition> list = new ArrayList<>();

        try {
            Connection con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
                     ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                long total = rs.getLong("Total");
                long urban = rs.getLong("Urban");
                long rural = total - urban;

                if (rural < 0) {
                    rural = 0;
                }

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

    public static void display(List<PopulationComposition> list) {

        for (PopulationComposition p : list) {
            System.out.println(
                    p.getName() + " | " +
                    p.getTotalPopulation() + " | " +
                    p.getUrbanPopulation() + " | " +
                    p.getRuralPopulation() + " | " +
                    p.getUrbanPercentage() + "% | " +
                    p.getRuralPercentage() + "%"
            );
        }
    }

    public static void main(String[] args) {

        System.out.println("Country Population");
        display(getCountryPopulationComposition());

        System.out.println("\nContinent Population");
        display(getContinentPopulationComposition());

        System.out.println("\nRegion Population");
        display(getRegionPopulationComposition());

        DatabaseConnection.disconnect();
    }
}
