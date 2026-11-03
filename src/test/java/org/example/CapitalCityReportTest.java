package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CapitalCityReportTest {

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
    void testWorldCapitalCitiesAreAllReturnedLargestFirst() {
        List<CapitalCity> capitals = CapitalCityReport.getWorldCapitalCitiesByPopulation();

        assertEquals(232, capitals.size());
        assertEquals("Seoul", capitals.get(0).getName());
        assertEquals(9981619, capitals.get(0).getPopulation());
    }

    @Test
    void testCapitalCitiesByContinent() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByContinent("Europe");

        assertFalse(capitals.isEmpty());

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(capitals.get(i - 1).getPopulation()
                    >= capitals.get(i).getPopulation());
        }
    }

    @Test
    void testTopNCapitalCitiesByContinent() {
        List<CapitalCity> capitals =
                CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", 5);

        assertEquals(5, capitals.size());
        assertEquals("Moscow", capitals.get(0).getName());

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(capitals.get(i - 1).getPopulation()
                    >= capitals.get(i).getPopulation());
        }
    }

    @Test
    void testCapitalCitiesByRegion() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByRegion("Western Europe");

        assertFalse(capitals.isEmpty());

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(capitals.get(i - 1).getPopulation()
                    >= capitals.get(i).getPopulation());
        }
    }

    @Test
    void testTopNCapitalCitiesByRegion() {
        List<CapitalCity> capitals =
                CapitalCityReport.getTopNCapitalCitiesByRegion("Western Europe", 3);

        assertEquals(3, capitals.size());
    }

    @Test
    void testNullRegion() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByRegion(null);

        assertNotNull(capitals);
        assertTrue(capitals.isEmpty());
    }

    @Test
    void testBlankContinent() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByContinent("   ");

        assertNotNull(capitals);
        assertTrue(capitals.isEmpty());
    }

    @Test
    void testNegativeN() {
        List<CapitalCity> capitals =
                CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", -5);

        assertNotNull(capitals);
        assertTrue(capitals.isEmpty());
    }
}
