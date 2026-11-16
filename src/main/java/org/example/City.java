package org.example;

/**
 * One row of a city report: the city's name, country, district and population.
 */
public class City {

    /** Name of the city. */
    private String name;

    /** Name of the country the city is in. */
    private String country;

    /** District (state, province, etc.) the city is in. */
    private String district;

    /** Population of the city. */
    private long population;

    /**
     * Creates an empty city; fill it in with the setters.
     */
    public City() {
    }

    /**
     * Gets the name of the city.
     *
     * @return the city name
     */
    public String getName() { return name; }

    /**
     * Sets the name of the city.
     *
     * @param name the city name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the name of the country the city is in.
     *
     * @return the country name
     */
    public String getCountry() { return country; }

    /**
     * Sets the name of the country the city is in.
     *
     * @param country the country name
     */
    public void setCountry(String country) { this.country = country; }

    /**
     * Gets the district the city is in.
     *
     * @return the district name
     */
    public String getDistrict() { return district; }

    /**
     * Sets the district the city is in.
     *
     * @param district the district name
     */
    public void setDistrict(String district) { this.district = district; }

    /**
     * Gets the population of the city.
     *
     * @return the population
     */
    public long getPopulation() { return population; }

    /**
     * Sets the population of the city.
     *
     * @param population the population
     */
    public void setPopulation(long population) { this.population = population; }
}
