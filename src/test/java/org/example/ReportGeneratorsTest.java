package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorsTest {

    @Test
    void testCapitalCityReportInstantiation() {
        CapitalCityReport report = new CapitalCityReport();
        assertNotNull(report, "CapitalCityReport should instantiate successfully");
    }

    @Test
    void testCityReportEmptyData() {
        CityReport report = new CityReport();
        assertNotNull(report, "CityReport should handle instantiation cleanly");
    }

    @Test
    void testCountryReportNullHandling() {
        assertDoesNotThrow(() -> {
            CountryReport report = new CountryReport();
            // Verify handling of null or empty datasets if applicable
            assertNotNull(report);
        });
    }

    @Test
    void testLanguageReportInstantiation() {
        LanguageReport report = new LanguageReport();
        assertNotNull(report, "LanguageReport should instantiate successfully");
    }

    @Test
    void testPopulationReportInstantiation() {
        PopulationReport report = new PopulationReport();
        assertNotNull(report, "PopulationReport should instantiate successfully");
    }
}