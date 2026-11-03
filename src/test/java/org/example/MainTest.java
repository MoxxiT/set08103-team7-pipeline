package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Smoke test: the application entry point runs every report against the real database.
 * Skipped locally when the database is not running; fails in CI if it is missing.
 */
class MainTest {

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
    void runPrintsEveryReportSectionWithRealData() {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            Main.run();
        } finally {
            System.setOut(original);
        }
        String output = captured.toString();

        assertTrue(output.contains("=== Countries: top 5 in the world ==="));
        assertTrue(output.contains("=== Cities: top 5 in the world ==="));
        assertTrue(output.contains("=== Capital cities: top 5 in the world ==="));
        assertTrue(output.contains("=== Population living in and out of cities: continents ==="));
        assertTrue(output.contains("=== Population of an area ==="));
        assertTrue(output.contains("=== Languages: speakers and share of world population ==="));

        // Real values from the world database
        assertTrue(output.contains("Mumbai (Bombay)"));
        assertTrue(output.contains("Seoul"));
        assertTrue(output.contains("6,078,749,450"));
        assertTrue(output.contains("Chinese"));
    }
}