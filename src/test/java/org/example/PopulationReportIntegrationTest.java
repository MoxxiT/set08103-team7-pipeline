
package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PopulationReportIntegrationTest {

    private void checkReports(List<PopulationComposition> reports) {
        assertFalse(reports.isEmpty(), "Report should not be empty");

        assertTrue(
                reports.stream().anyMatch(report ->
                        report.getTotalPopulation() > 0),
                "At least one report must have a positive population"
        );

        for (PopulationComposition report : reports) {
            assertTrue(report.getTotalPopulation() >= 0,
                    "Total population cannot be negative");

            assertTrue(report.getUrbanPopulation() >= 0,
                    "Urban population cannot be negative");

            assertTrue(report.getRuralPopulation() >= 0,
                    "Rural population cannot be negative");

            assertTrue(report.getUrbanPercentage() >= 0
                            && report.getUrbanPercentage() <= 100,
                    "Urban percentage must be between 0 and 100");

            assertTrue(report.getRuralPercentage() >= 0
                            && report.getRuralPercentage() <= 100,
                    "Rural percentage must be between 0 and 100");

            if (report.getTotalPopulation() > 0) {
                assertEquals(
                        100.0,
                        report.getUrbanPercentage()
                                + report.getRuralPercentage(),
                        0.01,
                        "Urban and rural percentages should total 100%"
                );
            } else {
                assertEquals(0.0, report.getUrbanPercentage(), 0.01);
                assertEquals(0.0, report.getRuralPercentage(), 0.01);
            }
        }
    }

    @Test
    void testCountryPopulationComposition() {
        checkReports(PopulationReport.getCountryPopulationComposition());
    }

    @Test
    void testContinentPopulationComposition() {
        checkReports(PopulationReport.getContinentPopulationComposition());
    }

    @Test
    void testRegionPopulationComposition() {
        checkReports(PopulationReport.getRegionPopulationComposition());
    }
}
