package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests the city reports against the real world database.
 * In GitHub Actions the MySQL service container provides the database.
 * On a laptop without the database running, these tests are skipped
 * (start it with: docker compose up -d db).
 */
class CityReportQueryTest {

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

    private static void assertSortedByPopulationDescending(List<City> cities) {
        for (int i = 1; i < cities.size(); i++) {
            assertTrue(cities.get(i - 1).getPopulation() >= cities.get(i).getPopulation(),
                    "Cities must be ordered from largest to smallest population");
        }
    }

    // ----- all cities -----

    @Test
    void worldReportContainsEveryCityLargestFirst() {
        List<City> cities = CityReport.getWorldCitiesByPopulation();
        assertEquals(4079, cities.size());
        assertEquals("Mumbai (Bombay)", cities.get(0).getName());
        assertEquals(10500000, cities.get(0).getPopulation());
        assertSortedByPopulationDescending(cities);
    }

    @Test
    void cityRowsHaveEveryRequiredColumn() {
        City top = CityReport.getWorldCitiesByPopulation().get(0);
        assertEquals("India", top.getCountry());
        assertEquals("Maharashtra", top.getDistrict());
    }

    @Test
    void continentReportOnlyContainsThatContinentLargestFirst() {
        List<City> cities = CityReport.getCitiesByContinent("Europe");
        assertEquals(841, cities.size());
        assertEquals("Moscow", cities.get(0).getName());
        assertSortedByPopulationDescending(cities);
    }

    @Test
    void regionReportOnlyContainsThatRegionLargestFirst() {
        List<City> cities = CityReport.getCitiesByRegion("Western Europe");
        assertEquals(186, cities.size());
        assertSortedByPopulationDescending(cities);
    }

    @Test
    void countryReportOnlyContainsThatCountryLargestFirst() {
        List<City> cities = CityReport.getCitiesByCountry("Japan");
        assertEquals(248, cities.size());
        assertTrue(cities.stream().allMatch(c -> c.getCountry().equals("Japan")));
        assertEquals("Tokyo", cities.get(0).getName());
        assertSortedByPopulationDescending(cities);
    }

    @Test
    void districtReportOnlyContainsThatDistrictLargestFirst() {
        List<City> cities = CityReport.getCitiesByDistrict("California");
        assertEquals(68, cities.size());
        assertTrue(cities.stream().allMatch(c -> c.getDistrict().equals("California")));
        assertEquals("Los Angeles", cities.get(0).getName());
        assertSortedByPopulationDescending(cities);
    }

    @Test
    void filteredReportsWithBadInputAreEmpty() {
        assertTrue(CityReport.getCitiesByContinent("Atlantis").isEmpty());
        assertTrue(CityReport.getCitiesByContinent(null).isEmpty());
        assertTrue(CityReport.getCitiesByRegion("Narnia").isEmpty());
        assertTrue(CityReport.getCitiesByRegion("   ").isEmpty());
        assertTrue(CityReport.getCitiesByCountry("Nowhere").isEmpty());
        assertTrue(CityReport.getCitiesByCountry("").isEmpty());
        assertTrue(CityReport.getCitiesByDistrict("Mordor").isEmpty());
        assertTrue(CityReport.getCitiesByDistrict(null).isEmpty());
    }

    // ----- top N -----

    @Test
    void topNInWorldReturnsExactlyNLargestFirst() {
        List<City> cities = CityReport.getTopNCitiesInWorld(5);
        assertEquals(5, cities.size());
        assertEquals("Mumbai (Bombay)", cities.get(0).getName());
        assertSortedByPopulationDescending(cities);
    }

    @Test
    void topNByContinentReturnsTheLargestInThatContinent() {
        List<City> cities = CityReport.getTopNCitiesByContinent("Europe", 3);
        assertEquals(3, cities.size());
        assertEquals("Moscow", cities.get(0).getName());
        assertEquals("London", cities.get(1).getName());
        assertEquals("St Petersburg", cities.get(2).getName());
    }

    @Test
    void topNByRegionReturnsTheLargestInThatRegion() {
        List<City> cities = CityReport.getTopNCitiesByRegion("Western Europe", 2);
        assertEquals(2, cities.size());
        assertSortedByPopulationDescending(cities);
    }

    @Test
    void topNByCountryReturnsTheLargestInThatCountry() {
        List<City> cities = CityReport.getTopNCitiesByCountry("Japan", 2);
        assertEquals(2, cities.size());
        assertEquals("Tokyo", cities.get(0).getName());
        assertEquals("Jokohama [Yokohama]", cities.get(1).getName());
    }

    @Test
    void topNByDistrictReturnsTheLargestInThatDistrict() {
        List<City> cities = CityReport.getTopNCitiesByDistrict("California", 2);
        assertEquals(2, cities.size());
        assertEquals("Los Angeles", cities.get(0).getName());
        assertEquals("San Diego", cities.get(1).getName());
    }

    @Test
    void topNWithZeroOrNegativeIsEmpty() {
        assertTrue(CityReport.getTopNCitiesInWorld(0).isEmpty());
        assertTrue(CityReport.getTopNCitiesInWorld(-3).isEmpty());
        assertTrue(CityReport.getTopNCitiesByContinent("Europe", 0).isEmpty());
        assertTrue(CityReport.getTopNCitiesByRegion("Western Europe", -1).isEmpty());
        assertTrue(CityReport.getTopNCitiesByCountry("Japan", 0).isEmpty());
        assertTrue(CityReport.getTopNCitiesByDistrict("California", -5).isEmpty());
    }

    @Test
    void topNLargerThanAvailableReturnsEverything() {
        assertEquals(68, CityReport.getTopNCitiesByDistrict("California", 1000).size());
        assertEquals(4079, CityReport.getTopNCitiesInWorld(100000).size());
    }

    @Test
    void topNWithBadAreaIsEmpty() {
        assertTrue(CityReport.getTopNCitiesByContinent("Atlantis", 5).isEmpty());
        assertTrue(CityReport.getTopNCitiesByRegion(null, 5).isEmpty());
        assertTrue(CityReport.getTopNCitiesByCountry("Nowhere", 5).isEmpty());
        assertTrue(CityReport.getTopNCitiesByDistrict("", 5).isEmpty());
    }
}