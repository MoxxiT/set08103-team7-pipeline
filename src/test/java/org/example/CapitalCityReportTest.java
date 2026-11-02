
package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CapitalCityReportTest {

    @Test
    void testCapitalCityReportInstantiation() {
        CapitalCityReport report = new CapitalCityReport();
        assertNotNull(report);
    }

    @Test
    void testCapitalCityReportEdgeCases() {
        assertDoesNotThrow(() -> new CapitalCityReport());
    }

    @Test
    void testCapitalCitiesByContinent() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByContinent("Europe");

        assertFalse(capitals.isEmpty());

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(capitals.get(i - 1).getPopulation()
                    >= capitals.get(i).getPopulation());
        }
    }

    @Test
    void testTopNCapitalCitiesByContinent() {
        List<CapitalCity> capitals =
                CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", 5);

        assertEquals(5, capitals.size());

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(capitals.get(i - 1).getPopulation()
                    >= capitals.get(i).getPopulation());
        }
    }

    @Test
    void testCapitalCitiesByRegion() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByRegion("Western Europe");

        assertFalse(capitals.isEmpty());

        for (int i = 1; i < capitals.size(); i++) {
            assertTrue(capitals.get(i - 1).getPopulation()
                    >= capitals.get(i).getPopulation());
        }
    }

    @Test
    void testTopNCapitalCitiesByRegion() {
        List<CapitalCity> capitals =
                CapitalCityReport.getTopNCapitalCitiesByRegion("Western Europe", 3);

        assertEquals(3, capitals.size());
    }

    @Test
    void testNullRegion() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByRegion(null);

        assertNotNull(capitals);
        assertTrue(capitals.isEmpty());
    }

    @Test
    void testBlankContinent() {
        List<CapitalCity> capitals =
                CapitalCityReport.getCapitalCitiesByContinent("   ");

        assertNotNull(capitals);
        assertTrue(capitals.isEmpty());
    }

    @Test
    void testNegativeN() {
        List<CapitalCity> capitals =
                CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", -5);

        assertNotNull(capitals);
        assertTrue(capitals.isEmpty());
    }
}
