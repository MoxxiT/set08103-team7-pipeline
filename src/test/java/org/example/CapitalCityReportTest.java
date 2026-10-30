package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CapitalCityReportTest {

    @Test
    void testCapitalCitiesByContinent() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByContinent("Europe");

        assertFalse(capitals.isEmpty(),
                "Europe should contain capital cities");

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(
                    capitals.get(i - 1).getPopulation()
                            >= capitals.get(i).getPopulation(),
                    "Capital cities should be sorted by population"
            );
        }
    }

    @Test
    void testTopNCapitalCitiesByContinent() {

        List<CapitalCity> capitals =
                CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", 5);

        assertEquals(5, capitals.size());

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(
                    capitals.get(i - 1).getPopulation()
                            >= capitals.get(i).getPopulation()
            );
        }

    }
    @Test
    void testCapitalCitiesByRegion() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByRegion("Western Europe");

        assertFalse(capitals.isEmpty());

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(
                    capitals.get(i - 1).getPopulation()
                            >= capitals.get(i).getPopulation()
            );
        }
    }
    @Test
    void testTopNCapitalCitiesByRegion() {
        List<CapitalCity> capitals =
                CapitalCityReport.getTopNCapitalCitiesByRegion("Western Europe", 3);

        assertEquals(3, capitals.size());
    }
}