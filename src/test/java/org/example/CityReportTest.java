package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CityReportTest {

    @Test
    void testCityReport() {
        CityReport report = new CityReport();
        assertNotNull(report);
    }

    @Test
    void testInvalidInputs() {
        assertTrue(CityReport.getCitiesByContinent("").isEmpty());
        assertTrue(CityReport.getCitiesByRegion("").isEmpty());
        assertTrue(CityReport.getCitiesByCountry("").isEmpty());
        assertTrue(CityReport.getCitiesByDistrict("").isEmpty());
    }

    @Test
    void testTopN() {
        assertTrue(CityReport.getTopNCitiesInWorld(0).isEmpty());
        assertTrue(CityReport.getTopNCitiesInWorld(-1).isEmpty());
    }
}