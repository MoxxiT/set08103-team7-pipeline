package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests the language report against the real world database.
 * In GitHub Actions the MySQL service container provides the database.
 * On a laptop without the database running, these tests are skipped
 * (start it with: docker compose up -d db).
 */
class LanguageReportTest {

    private static final long WORLD_POPULATION = 6078749450L;

    @BeforeAll
    static void requireDatabase() {
        boolean available = DatabaseConnection.getConnection() != null;
        if (!available && System.getenv("CI") != null) {
            fail("The database must be available in CI");
        }
        assumeTrue(available, "No local database running - skipping database tests");
    }

    @AfterAll
    static void closeDatabase() {
        DatabaseConnection.disconnect();
    }

    @Test
    void reportContainsExactlyTheFiveRequiredLanguages() {
        List<Language> languages = LanguageReport.getLanguageStatistics();

        assertEquals(5, languages.size());
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Chinese")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("English")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Hindi")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Spanish")));
        assertTrue(languages.stream().anyMatch(l -> l.getLanguage().equals("Arabic")));
    }

    @Test
    void languagesAreOrderedFromMostToFewestSpeakers() {
        List<Language> languages = LanguageReport.getLanguageStatistics();

        assertEquals("Chinese", languages.get(0).getLanguage());
        assertEquals("Hindi", languages.get(1).getLanguage());
        assertEquals("Spanish", languages.get(2).getLanguage());
        assertEquals("English", languages.get(3).getLanguage());
        assertEquals("Arabic", languages.get(4).getLanguage());

        for (int i = 1; i < languages.size(); i++) {
            assertTrue(languages.get(i - 1).getTotalSpeakers() >= languages.get(i).getTotalSpeakers(),
                    "Languages must be ordered from greatest to fewest speakers");
        }
    }

    @Test
    void chineseSpeakersAndPercentageAreCorrect() {
        Language chinese = LanguageReport.getLanguageStatistics().get(0);

        assertEquals(1191843539L, chinese.getTotalSpeakers());
        assertEquals(19.6, chinese.getPercentageOfWorldPopulation(), 0.05);
    }

    @Test
    void percentagesAreSpeakersAsAShareOfTheWorldPopulation() {
        for (Language l : LanguageReport.getLanguageStatistics()) {
            double expected = (double) l.getTotalSpeakers() / WORLD_POPULATION * 100.0;
            assertEquals(expected, l.getPercentageOfWorldPopulation(), 0.0001,
                    "Wrong percentage for " + l.getLanguage());
            assertTrue(l.getPercentageOfWorldPopulation() > 0
                    && l.getPercentageOfWorldPopulation() < 100);
        }
    }
}