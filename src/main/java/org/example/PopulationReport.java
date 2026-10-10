package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PopulationReport {

    /**
     * Generates population composition reports showing total population,
     * people living in cities (with percentages), and people not living in cities for each country.
     */
    public static List<PopulationComposition> getCountryPopulationComposition() {
        List<PopulationComposition> list = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) return list;

            Statement stmt = con.createStatement();
            // SQL query calculating total country population vs urban population from cities
            String query = "SELECT c.Name AS CountryName, c.Population AS TotalPop, "
                    + "COALESCE(SUM(ci.Population), 0) AS UrbanPop "
                    + "FROM country c "
                    + "LEFT JOIN city ci ON c.Code = ci.CountryCode "
                    + "GROUP BY c.Code, c.Name, c.Population "
                    + "ORDER BY c.Population DESC";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("CountryName");
                long totalPop = rs.getLong("TotalPop");
                long urbanPop = rs.getLong("UrbanPop");
                long ruralPop = Math.max(0, totalPop - urbanPop);

                double urbanPct = totalPop > 0 ? ((double) urbanPop / totalPop) * 100.0 : 0.0;
                double ruralPct = totalPop > 0 ? ((double) ruralPop / totalPop) * 100.0 : 0.0;

                PopulationComposition comp = new PopulationComposition();
                comp.setName(name);
                comp.setTotalPopulation(totalPop);
                comp.setUrbanPopulation(urbanPop);
                comp.setUrbanPercentage(urbanPct);
                comp.setRuralPopulation(ruralPop);
                comp.setRuralPercentage(ruralPct);
                list.add(comp);
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
                    p.getName(), p.getTotalPopulation(),
                    p.getUrbanPopulation(), p.getUrbanPercentage(),
                    p.getRuralPopulation(), p.getRuralPercentage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Fetching Country Population Composition Report...");
        List<PopulationComposition> report = getCountryPopulationComposition();
        displayComposition(report);
        DatabaseConnection.disconnect();
    }
}
