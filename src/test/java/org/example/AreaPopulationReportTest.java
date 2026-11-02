package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests the single-figure population lookups against the real world database.
 * Skipped on a laptop without the database; required in CI (see CityReportQueryTest).
 */
class AreaPopulationReportTest {

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
    void worldPopulationIsTheSumOfAllCountries() {
        assertEquals(6078749450L, AreaPopulationReport.getWorldPopulation());
    }

    @Test
    void worldPopulationEqualsTheSumOfTheContinents() {
        long sum = 0;
        for (String continent : new String[]{"Asia", "Africa", "Europe", "North America",
                "South America", "Oceania", "Antarctica"}) {
            sum += AreaPopulationReport.getContinentPopulation(continent);
        }
        assertEquals(AreaPopulationReport.getWorldPopulation(), sum);
    }

    @Test
    void continentPopulation() {
        assertEquals(730074600L, AreaPopulationReport.getContinentPopulation("Europe"));
    }

    @Test
    void regionPopulation() {
        assertEquals(183247600L, AreaPopulationReport.getRegionPopulation("Western Europe"));
    }

    @Test
    void countryPopulation() {
        assertEquals(126714000L, AreaPopulationReport.getCountryPopulation("Japan"));
    }

    @Test
    void districtPopulationIsTheSumOfItsCities() {
        assertEquals(16716706L, AreaPopulationReport.getDistrictPopulation("California"));
    }

    @Test
    void cityPopulation() {
        assertEquals(7980230L, AreaPopulationReport.getCityPopulation("Tokyo"));
    }

    @Test
    void ambiguousCityNameReturnsTheLargestMatch() {
        // Valencia exists in Spain (739,412), the Philippines (147,924) and Venezuela (794,246)
        assertEquals(794246L, AreaPopulationReport.getCityPopulation("Valencia"));
    }

    @Test
    void cityCanBeNarrowedDownByCountry() {
        assertEquals(739412L, AreaPopulationReport.getCityPopulation("Valencia", "Spain"));
        assertEquals(147924L, AreaPopulationReport.getCityPopulation("Valencia", "Philippines"));
    }

    @Test
    void unknownOrBlankInputGivesZero() {
        assertEquals(0, AreaPopulationReport.getContinentPopulation("Atlantis"));
        assertEquals(0, AreaPopulationReport.getRegionPopulation("Narnia"));
        assertEquals(0, AreaPopulationReport.getCountryPopulation("Nowhere"));
        assertEquals(0, AreaPopulationReport.getDistrictPopulation("Mordor"));
        assertEquals(0, AreaPopulationReport.getCityPopulation("Gotham"));
        assertEquals(0, AreaPopulationReport.getCityPopulation("Valencia", "Atlantis"));
        assertEquals(0, AreaPopulationReport.getContinentPopulation(null));
        assertEquals(0, AreaPopulationReport.getCityPopulation("  "));
        assertEquals(0, AreaPopulationReport.getCityPopulation("Valencia", null));
    }
}