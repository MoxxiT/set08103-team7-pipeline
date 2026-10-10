package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LanguageReport {

    /**
     * Gets the language statistics report for Chinese, English, Hindi, Spanish, and Arabic,
     * ordered from greatest number of speakers to smallest, including the percentage of the world population.
     */
    public static List<Language> getLanguageStatistics() {
        List<Language> languages = new ArrayList<>();
        String[] targetLanguages = {"Chinese", "English", "Hindi", "Spanish", "Arabic"};

        try {
            Connection con = DatabaseConnection.getConnection();
            if (con == null) {
                System.out.println("Database connection is null.");
                return languages;
            }

            Statement stmt = con.createStatement();

            // 1. Get total world population for percentage calculations
            String worldPopQuery = "SELECT SUM(Population) AS WorldPop FROM country";
            ResultSet worldPopRs = stmt.executeQuery(worldPopQuery);
            long worldPopulation = 0;
            if (worldPopRs.next()) {
                worldPopulation = worldPopRs.getLong("WorldPop");
            }
            worldPopRs.close();

            if (worldPopulation == 0) {
                System.out.println("Could not determine world population.");
                return languages;
            }

            // 2. Query total speakers for each target language
            for (String lang : targetLanguages) {
                String langQuery = "SELECT SUM(c.Population * (cl.Percentage / 100.0)) AS TotalSpeakers "
                        + "FROM countrylanguage cl "
                        + "JOIN country c ON cl.CountryCode = c.Code "
                        + "WHERE cl.Language = '" + lang + "'";

                ResultSet rs = stmt.executeQuery(langQuery);
                if (rs.next()) {
                    long speakers = Math.round(rs.getDouble("TotalSpeakers"));
                    double percentage = (double) speakers / worldPopulation * 100.0;

                    Language langStat = new Language();
                    langStat.setLanguage(lang);
                    langStat.setTotalSpeakers(speakers);
                    langStat.setPercentageOfWorldPopulation(percentage);
                    languages.add(langStat);
                }
                rs.close();
            }

            // 3. Sort from greatest number of speakers to smallest
            languages.sort((l1, l2) -> Long.compare(l2.getTotalSpeakers(), l1.getTotalSpeakers()));

            stmt.close();
        } catch (SQLException e) {
            System.out.println("Database query failed.");
            e.printStackTrace();
        }
        return languages;
    }

    public static void displayLanguageStatistics(List<Language> languages) {
        System.out.printf("%-15s | %-20s | %-25s%n",
                "Language", "Total Speakers", "Percentage of World Pop (%)");
        System.out.println("-".repeat(70));
        for (Language l : languages) {
            System.out.printf("%-15s | %-20d | %-25.2f%%%n",
                    l.getLanguage(), l.getTotalSpeakers(), l.getPercentageOfWorldPopulation());
        }
    }

    public static void main(String[] args) {
        System.out.println("Fetching language statistics report...");
        List<Language> languages = getLanguageStatistics();
        displayLanguageStatistics(languages);
        DatabaseConnection.disconnect();
    }
}