package org.example;

/**
 * One row of a country report: code, name, continent, region, population and
 * the name of the capital city.
 */
public class Country {

    /** Three-letter country code (e.g. "GBR"). */
    private String code;

    /** Name of the country. */
    private String name;

    /** Continent the country is in. */
    private String continent;

    /** Region the country is in. */
    private String region;

    /** Population of the country. */
    private long population;

    /**
     * Name of the capital city. The database stores the capital as a city ID;
     * the report query joins the city table to turn it into a name, and uses
     * "N/A" for countries with no capital.
     */
    private String capital;

    /**
     * Creates an empty country; fill it in with the setters.
     */
    public Country() {
    }

    /**
     * Gets the three-letter country code.
     *
     * @return the country code
     */
    public String getCode() { return code; }

    /**
     * Sets the three-letter country code.
     *
     * @param code the country code
     */
    public void setCode(String code) { this.code = code; }

    /**
     * Gets the name of the country.
     *
     * @return the country name
     */
    public String getName() { return name; }

    /**
     * Sets the name of the country.
     *
     * @param name the country name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the continent the country is in.
     *
     * @return the continent name
     */
    public String getContinent() { return continent; }

    /**
     * Sets the continent the country is in.
     *
     * @param continent the continent name
     */
    public void setContinent(String continent) { this.continent = continent; }

    /**
     * Gets the region the country is in.
     *
     * @return the region name
     */
    public String getRegion() { return region; }

    /**
     * Sets the region the country is in.
     *
     * @param region the region name
     */
    public void setRegion(String region) { this.region = region; }

    /**
     * Gets the population of the country.
     *
     * @return the population
     */
    public long getPopulation() { return population; }

    /**
     * Sets the population of the country.
     *
     * @param population the population
     */
    public void setPopulation(long population) { this.population = population; }

    /**
     * Gets the name of the capital city.
     *
     * @return the capital city's name, or "N/A" if the country has none
     */
    public String getCapital() { return capital; }

    /**
     * Sets the name of the capital city.
     *
     * @param capital the capital city's name
     */
    public void setCapital(String capital) { this.capital = capital; }
}
