package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PopulationReportTest {

    @Test
    void testCountryPopulationComposition() {
        List<PopulationComposition> reports =
                PopulationReport.getCountryPopulationComposition();

        assertFalse(reports.isEmpty());

        for (PopulationComposition report : reports) {
            assertTrue(report.getTotalPopulation() >= 0);
            assertTrue(report.getUrbanPopulation() >= 0);
            assertTrue(report.getRuralPopulation() >= 0);
            assertTrue(report.getUrbanPercentage() >= 0);
            assertTrue(report.getRuralPercentage() >= 0);
        }
    }
    @Test
    void testContinentPopulationComposition() {
        List<PopulationComposition> reports =
                PopulationReport.getContinentPopulationComposition();

        assertFalse(reports.isEmpty());

        for (PopulationComposition report : reports) {
            assertTrue(report.getTotalPopulation() >= 0);
            assertTrue(report.getUrbanPopulation() >= 0);
            assertTrue(report.getRuralPopulation() >= 0);
        }
    }
    @Test
    void testRegionPopulationComposition() {
        List<PopulationComposition> reports =
                PopulationReport.getRegionPopulationComposition();

        assertFalse(reports.isEmpty());

        for (PopulationComposition report : reports) {
            assertTrue(report.getTotalPopulation() >= 0);
            assertTrue(report.getUrbanPopulation() >= 0);
            assertTrue(report.getRuralPopulation() >= 0);
        }
    }
}