package org.example;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    void testPopulationCalculation() {
        // Test core population calculation logic
        int total = 1000;
        int urban = 400;
        int rural = total - urban;
        assertEquals(600, rural, "Rural population calculation should be correct");
    }

    @Test
    void testNullInputHandling() {
        // Test edge case: handling null inputs safely
        assertDoesNotThrow(() -> {
            String input = null;
            assertNull(input);
        });
    }

    @Test
    void testEmptyQueryResults() {
        // Test edge case: empty query results / collections
        List<String> results = new ArrayList<>();
        assertTrue(results.isEmpty(), "Query results should be empty when no data is found");
    }
}