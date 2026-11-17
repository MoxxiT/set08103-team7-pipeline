
package org.example;

// Import database connection.
import java.sql.Connection;

// Import safe SQL statements.
import java.sql.PreparedStatement;

// Import SQL query results.
import java.sql.ResultSet;

// Import SQL error handling.
import java.sql.SQLException;

// Import the ArrayList collection.
import java.util.ArrayList;

// Import the List interface.
import java.util.List;

/**
 * LanguageReport generates statistics for five major languages.
 * It calculates speakers and their percentage of world population.
 * PreparedStatement prevents unsafe SQL concatenation.
 * Try-with-resources automatically closes database resources.
 */
public class LanguageReport {

    /**
     * Gets language statistics from the database.
     * Results are sorted by the highest number of speakers.
     */
    public static List<Language> getLanguageStatistics() {

        // Create a list to store results.
        List<Language> languages = new ArrayList<>();

        // Define the languages to report.
        String[] targetLanguages = {
                "Chinese", "English", "Hindi", "Spanish", "Arabic"
        };

        // Handle database errors.
        try {

            // Get the existing database connection.
            Connection con = DatabaseConnection.getConnection();

            // Check whether the connection exists.
            if (con == null) {

                // Display an error message.
                System.out.println("Database connection is null.");

                // Return an empty list.
                return languages;
            }

            // Define the world population query.
            String worldPopQuery =
                    "SELECT SUM(Population) AS WorldPop FROM country";

            // Store the world population.
            long worldPopulation = 0;

            // Prepare the query and automatically close resources.
            try (PreparedStatement stmt =
                         con.prepareStatement(worldPopQuery);
                 ResultSet rs = stmt.executeQuery()) {

                // Check whether a result exists.
                if (rs.next()) {

                    // Read the world population.
                    worldPopulation = rs.getLong("WorldPop");
                }
            }

            // Check for missing population data.
            if (worldPopulation == 0) {

                // Display an error message.
                System.out.println("Could not determine world population.");

                // Return the empty list.
                return languages;
            }

            // Define the language statistics query.
            String langQuery =
                    "SELECT SUM(c.Population * (cl.Percentage / 100.0)) AS TotalSpeakers "
                            + "FROM countrylanguage cl "
                            + "JOIN country c ON cl.CountryCode = c.Code "
                            + "WHERE cl.Language = ?";

            // Prepare the SQL query safely.
            try (PreparedStatement stmt =
                         con.prepareStatement(langQuery)) {

                // Process each target language.
                for (String lang : targetLanguages) {

                    // Insert the language as a safe parameter.
                    stmt.setString(1, lang);

                    // Execute the query and close results automatically.
                    try (ResultSet rs = stmt.executeQuery()) {

                        // Check whether data exists.
                        if (rs.next()) {

                            // Calculate the total number of speakers.
                            long speakers = Math.round(
                                    rs.getDouble("TotalSpeakers")
                            );

                            // Calculate the percentage of world population.
                            double percentage =
                                    (double) speakers / worldPopulation * 100.0;

                            // Create a new language object.
                            Language langStat = new Language();

                            // Store the language name.
                            langStat.setLanguage(lang);

                            // Store the total speakers.
                            langStat.setTotalSpeakers(speakers);

                            // Store the population percentage.
                            langStat.setPercentageOfWorldPopulation(percentage);

                            // Add the language to the results.
                            languages.add(langStat);
                        }
                    }
                }
            }

            // Sort languages by speakers, highest first.
            languages.sort((l1, l2) ->
                    Long.compare(
                            l2.getTotalSpeakers(),
                            l1.getTotalSpeakers()
                    )
            );

            // Handle SQL errors.
        } catch (SQLException e) {

            // Display a database error message.
            System.out.println("Database query failed.");

            // Print the error details.
            e.printStackTrace();
        }

        // Return the completed language report.
        return languages;
    }

    /**
     * Displays the language statistics in a table.
     */
    public static void displayLanguageStatistics(List<Language> languages) {

        // Print the table headings.
        System.out.printf("%-15s | %-20s | %-25s%n",
                "Language",
                "Total Speakers",
                "Percentage of World Pop (%)");

        // Print a separator line.
        System.out.println("-".repeat(70));

        // Loop through each language.
        for (Language l : languages) {

            // Print the language statistics.
            System.out.printf("%-15s | %-20d | %-25.2f%%%n",
                    l.getLanguage(),
                    l.getTotalSpeakers(),
                    l.getPercentageOfWorldPopulation());
        }
    }

    /**
     * Runs the language report.
     */
    public static void main(String[] args) {

        // Display the starting message.
        System.out.println("Fetching language statistics report...");

        // Retrieve language statistics.
        List<Language> languages = getLanguageStatistics();

        // Display the results.
        displayLanguageStatistics(languages);

        // Close the database connection.
        DatabaseConnection.disconnect();
    }
}
