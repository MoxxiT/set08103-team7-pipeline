package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests the top N capital city reports against the real world database.
 * In GitHub Actions the MySQL service container provides the database.
 * On a laptop without the database running, these tests are skipped
 * (start it with: docker compose up -d db).
 */
class CapitalCityReportQueryTest {

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
    void topNInWorldReturnsExactlyNLargestFirst() {
        List<CapitalCity> capitals = CapitalCityReport.getTopNCapitalCitiesInWorld(5);

        assertEquals(5, capitals.size());
        assertEquals("Seoul", capitals.get(0).getName());
        assertEquals("South Korea", capitals.get(0).getCountry());
        assertEquals(9981619, capitals.get(0).getPopulation());
        assertEquals("Jakarta", capitals.get(1).getName());
        assertEquals("Moscow", capitals.get(3).getName());
        assertEquals("Tokyo", capitals.get(4).getName());
    }

    @Test
    void topNInWorldIsSortedLargestToSmallest() {
        List<CapitalCity> capitals = CapitalCityReport.getTopNCapitalCitiesInWorld(20);

        assertEquals(20, capitals.size());
        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(capitals.get(i - 1).getPopulation() >= capitals.get(i).getPopulation(),
                    "Capital cities must be ordered from largest to smallest population");
        }
    }

    @Test
    void topNInWorldIsConsistentWithTheFullWorldReport() {
        List<CapitalCity> all = CapitalCityReport.getWorldCapitalCitiesByPopulation();
        List<CapitalCity> top = CapitalCityReport.getTopNCapitalCitiesInWorld(3);

        assertEquals(3, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(all.get(i).getPopulation(), top.get(i).getPopulation());
        }
    }

    @Test
    void topNLargerThanTheNumberOfCapitalsReturnsEveryCapital() {
        List<CapitalCity> capitals = CapitalCityReport.getTopNCapitalCitiesInWorld(100000);
        assertEquals(232, capitals.size());
    }

    @Test
    void topNInWorldWithZeroOrNegativeIsEmpty() {
        assertTrue(CapitalCityReport.getTopNCapitalCitiesInWorld(0).isEmpty());
        assertTrue(CapitalCityReport.getTopNCapitalCitiesInWorld(-1).isEmpty());
    }

    @Test
    void topNByContinentAndRegionWithZeroIsEmptyNotUnlimited() {
        // Zero used to mean "no limit" and returned every capital.
        assertTrue(CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", 0).isEmpty());
        assertTrue(CapitalCityReport.getTopNCapitalCitiesByRegion("Western Europe", 0).isEmpty());
        assertTrue(CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", -2).isEmpty());
    }

    @Test
    void topNByContinentStillReturnsTheLargestCapitals() {
        List<CapitalCity> capitals = CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", 3);

        assertEquals(3, capitals.size());
        assertEquals("Moscow", capitals.get(0).getName());
        assertEquals("London", capitals.get(1).getName());
        assertEquals("Berlin", capitals.get(2).getName());
    }
}