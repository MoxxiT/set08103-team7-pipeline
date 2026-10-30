package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CapitalCityTest {

    @Test
    void testCapitalCityWithZeroPopulation() {
        CapitalCity city = new CapitalCity();

        city.setName("Test Capital");
        city.setCountry("Test Country");
        city.setPopulation(0);

        assertEquals("Test Capital", city.getName());
        assertEquals("Test Country", city.getCountry());
        assertEquals(0, city.getPopulation());
    }

    @Test
    void testCapitalCityGettersAndSetters() {
        CapitalCity city = new CapitalCity();

        city.setName("London");
        city.setCountry("United Kingdom");
        city.setPopulation(9000000);

        assertEquals("London", city.getName());
        assertEquals("United Kingdom", city.getCountry());
        assertEquals(9000000, city.getPopulation());
    }
}