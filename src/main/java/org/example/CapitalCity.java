package org.example;

/**
 * One row of a capital city report: the capital's name, the country it is the
 * capital of, and its population.
 */
public class CapitalCity {

    /** Name of the capital city. */
    private String name;

    /** Name of the country this city is the capital of. */
    private String country;

    /** Population of the capital city. */
    private long population;

    /**
     * Creates an empty capital city; fill it in with the setters.
     */
    public CapitalCity() {
    }

    /**
     * Gets the name of the capital city.
     *
     * @return the capital city's name
     */
    public String getName() { return name; }

    /**
     * Sets the name of the capital city.
     *
     * @param name the capital city's name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the name of the country this city is the capital of.
     *
     * @return the country name
     */
    public String getCountry() { return country; }

    /**
     * Sets the name of the country this city is the capital of.
     *
     * @param country the country name
     */
    public void setCountry(String country) { this.country = country; }

    /**
     * Gets the population of the capital city.
     *
     * @return the population
     */
    public long getPopulation() { return population; }

    /**
     * Sets the population of the capital city.
     *
     * @param population the population
     */
    public void setPopulation(long population) { this.population = population; }
}
