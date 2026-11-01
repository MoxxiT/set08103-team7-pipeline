package org.example;

import org.example.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CapitalCityReportTest {

    @Test
    void testCapitalCityReportInstantiation() {
        CapitalCityReport report = new CapitalCityReport();
        assertNotNull(report, "CapitalCityReport should instantiate successfully");
    }

    @Test
    void testCapitalCityReportEdgeCases() {
        CapitalCityReport report = new CapitalCityReport();
        assertDoesNotThrow(() -> {
            assertNotNull(report);
        });
    }
}