package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests the country reports against the real world database.
 * In GitHub Actions the MySQL service container provides the database.
 * On a laptop without the database running, these tests are skipped
 * (start it with: docker compose up -d db).
 */
class CountryReportQueryTest {

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

    private static void assertSortedByPopulationDescending(List<Country> countries) {
        for (int i = 1; i < countries.size(); i++) {
            assertTrue(countries.get(i - 1).getPopulation() >= countries.get(i).getPopulation(),
                    "Countries must be ordered from largest to smallest population");
        }
    }

    // ----- world -----

    @Test
    void worldReportContainsEveryCountryLargestFirst() {
        List<Country> countries = CountryReport.getWorldCountriesByPopulation();
        assertEquals(239, countries.size());
        assertEquals("China", countries.get(0).getName());
        assertSortedByPopulationDescending(countries);
    }

    @Test
    void capitalIsShownAsNameNotId() {
        List<Country> countries = CountryReport.getWorldCountriesByPopulation();
        Country china = countries.stream().filter(c -> c.getCode().equals("CHN")).findFirst().orElseThrow();
        assertEquals("Peking", china.getCapital());
    }

    @Test
    void countryWithNoCapitalShowsNotAvailable() {
        List<Country> countries = CountryReport.getWorldCountriesByPopulation();
        Country antarctica = countries.stream().filter(c -> c.getCode().equals("ATA")).findFirst().orElseThrow();
        assertEquals("N/A", antarctica.getCapital());
    }

    // ----- continent -----

    @Test
    void continentReportOnlyContainsThatContinentLargestFirst() {
        List<Country> countries = CountryReport.getCountriesByContinent("Europe");
        assertFalse(countries.isEmpty());
        assertTrue(countries.stream().allMatch(c -> c.getContinent().equals("Europe")));
        assertEquals("Russian Federation", countries.get(0).getName());
        assertSortedByPopulationDescending(countries);
    }

    @Test
    void continentReportWithBadInputIsEmpty() {
        assertTrue(CountryReport.getCountriesByContinent("Atlantis").isEmpty());
        assertTrue(CountryReport.getCountriesByContinent("").isEmpty());
        assertTrue(CountryReport.getCountriesByContinent("   ").isEmpty());
        assertTrue(CountryReport.getCountriesByContinent(null).isEmpty());
    }

    // ----- region -----

    @Test
    void regionReportOnlyContainsThatRegionLargestFirst() {
        List<Country> countries = CountryReport.getCountriesByRegion("Western Europe");
        assertFalse(countries.isEmpty());
        assertTrue(countries.stream().allMatch(c -> c.getRegion().equals("Western Europe")));
        assertEquals("Germany", countries.get(0).getName());
        assertSortedByPopulationDescending(countries);
    }

    @Test
    void regionReportWithBadInputIsEmpty() {
        assertTrue(CountryReport.getCountriesByRegion("Narnia").isEmpty());
        assertTrue(CountryReport.getCountriesByRegion(null).isEmpty());
    }

    // ----- top N -----

    @Test
    void topNInWorldReturnsExactlyNLargestFirst() {
        List<Country> countries = CountryReport.getTopNCountriesInWorld(5);
        assertEquals(5, countries.size());
        assertEquals("China", countries.get(0).getName());
        assertSortedByPopulationDescending(countries);
    }

    @Test
    void topNWithZeroOrNegativeIsEmpty() {
        assertTrue(CountryReport.getTopNCountriesInWorld(0).isEmpty());
        assertTrue(CountryReport.getTopNCountriesInWorld(-3).isEmpty());
        assertTrue(CountryReport.getTopNCountriesByContinent("Europe", 0).isEmpty());
        assertTrue(CountryReport.getTopNCountriesByRegion("Western Europe", -1).isEmpty());
    }

    @Test
    void topNLargerThanAvailableReturnsEverything() {
        assertEquals(239, CountryReport.getTopNCountriesInWorld(1000).size());
    }

    @Test
    void topNByContinentReturnsTheLargestInThatContinent() {
        List<Country> countries = CountryReport.getTopNCountriesByContinent("Europe", 3);
        assertEquals(3, countries.size());
        assertEquals("Russian Federation", countries.get(0).getName());
        assertEquals("Germany", countries.get(1).getName());
        assertEquals("United Kingdom", countries.get(2).getName());
    }

    @Test
    void topNByRegionReturnsTheLargestInThatRegion() {
        List<Country> countries = CountryReport.getTopNCountriesByRegion("Western Europe", 2);
        assertEquals(2, countries.size());
        assertEquals("Germany", countries.get(0).getName());
        assertEquals("France", countries.get(1).getName());
    }

    @Test
    void topNWithBadAreaIsEmpty() {
        assertTrue(CountryReport.getTopNCountriesByContinent("Atlantis", 5).isEmpty());
        assertTrue(CountryReport.getTopNCountriesByRegion(null, 5).isEmpty());
    }
}