package org.example;

/**
 * One row of a population report: for a continent, region or country, the total
 * population, how many people live in cities, and how many do not, with percentages.
 */
public class PopulationComposition {

    /** Name of the continent, region or country. */
    private String name;

    /** Total population of the area. */
    private long totalPopulation;

    /** Number of people living in cities. */
    private long urbanPopulation;

    /** People living in cities as a percentage of the total (0 to 100). */
    private double urbanPercentage;

    /** Number of people not living in cities. */
    private long ruralPopulation;

    /** People not living in cities as a percentage of the total (0 to 100). */
    private double ruralPercentage;

    /**
     * Creates an empty population entry; fill it in with the setters.
     */
    public PopulationComposition() {
    }

    /**
     * Gets the name of the area.
     *
     * @return the continent, region or country name
     */
    public String getName() { return name; }

    /**
     * Sets the name of the area.
     *
     * @param name the continent, region or country name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the total population of the area.
     *
     * @return the total population
     */
    public long getTotalPopulation() { return totalPopulation; }

    /**
     * Sets the total population of the area.
     *
     * @param totalPopulation the total population
     */
    public void setTotalPopulation(long totalPopulation) { this.totalPopulation = totalPopulation; }

    /**
     * Gets the number of people living in cities.
     *
     * @return the city population
     */
    public long getUrbanPopulation() { return urbanPopulation; }

    /**
     * Sets the number of people living in cities.
     *
     * @param urbanPopulation the city population
     */
    public void setUrbanPopulation(long urbanPopulation) { this.urbanPopulation = urbanPopulation; }

    /**
     * Gets the percentage of people living in cities.
     *
     * @return the percentage, from 0 to 100
     */
    public double getUrbanPercentage() { return urbanPercentage; }

    /**
     * Sets the percentage of people living in cities.
     *
     * @param urbanPercentage the percentage, from 0 to 100
     */
    public void setUrbanPercentage(double urbanPercentage) { this.urbanPercentage = urbanPercentage; }

    /**
     * Gets the number of people not living in cities.
     *
     * @return the non-city population
     */
    public long getRuralPopulation() { return ruralPopulation; }

    /**
     * Sets the number of people not living in cities.
     *
     * @param ruralPopulation the non-city population
     */
    public void setRuralPopulation(long ruralPopulation) { this.ruralPopulation = ruralPopulation; }

    /**
     * Gets the percentage of people not living in cities.
     *
     * @return the percentage, from 0 to 100
     */
    public double getRuralPercentage() { return ruralPercentage; }

    /**
     * Sets the percentage of people not living in cities.
     *
     * @param ruralPercentage the percentage, from 0 to 100
     */
    public void setRuralPercentage(double ruralPercentage) { this.ruralPercentage = ruralPercentage; }
}
