package org.example;

import org.example.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CountryReportTest {

    @Test
    void testCountryReportInstantiation() {
        CountryReport report = new CountryReport();
        assertNotNull(report, "CountryReport should instantiate successfully");
    }

    @Test
    void testCountryReportEdgeCases() {
        // Add test logic following the pattern you saw in PopulationReportTest
        CountryReport report = new CountryReport();
        assertDoesNotThrow(() -> {
            // Verify behavior with empty or normal parameters if applicable
            assertNotNull(report);
        });
    }
}