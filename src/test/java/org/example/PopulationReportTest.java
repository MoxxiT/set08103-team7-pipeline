package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PopulationReportTest {

    @Test
    void normalCountrySplitsUrbanAndRural() {
        PopulationComposition c = PopulationReport.buildComposition("Testland", 1000, 400);
        assertEquals(600, c.getRuralPopulation());
        assertEquals(40.0, c.getUrbanPercentage(), 0.001);
        assertEquals(60.0, c.getRuralPercentage(), 0.001);
    }

    @Test
    void percentagesAddUpToOneHundred() {
        PopulationComposition c = PopulationReport.buildComposition("Testland", 1277558000L, 175953614L);
        assertEquals(100.0, c.getUrbanPercentage() + c.getRuralPercentage(), 0.001);
    }

    @Test
    void countryWithNoCitiesIsAllRural() {
        PopulationComposition c = PopulationReport.buildComposition("Nocity", 5000, 0);
        assertEquals(0.0, c.getUrbanPercentage(), 0.001);
        assertEquals(100.0, c.getRuralPercentage(), 0.001);
    }

    @Test
    void zeroPopulationDoesNotDivideByZero() {
        PopulationComposition c = PopulationReport.buildComposition("Antarctica", 0, 0);
        assertEquals(0.0, c.getUrbanPercentage(), 0.001);
        assertEquals(0.0, c.getRuralPercentage(), 0.001);
    }

    @Test
    void cityPopulationAboveCountryPopulationIsCapped() {
        // Real data: Singapore has 3,567,000 people but cities sum to 4,017,733
        PopulationComposition c = PopulationReport.buildComposition("Singapore", 3567000, 4017733);
        assertEquals(100.0, c.getUrbanPercentage(), 0.001);
        assertEquals(0, c.getRuralPopulation());
        assertEquals(0.0, c.getRuralPercentage(), 0.001);
    }
}