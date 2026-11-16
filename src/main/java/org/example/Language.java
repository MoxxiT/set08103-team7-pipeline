package org.example;

/**
 * One row of the language report: a language, how many people speak it, and
 * what percentage of the world population that is.
 */
public class Language {

    /** Name of the language. */
    private String language;

    /** Estimated number of people who speak the language. */
    private long totalSpeakers;

    /** Speakers as a percentage of the world population (0 to 100). */
    private double percentageOfWorldPopulation;

    /**
     * Creates an empty language entry; fill it in with the setters.
     */
    public Language() {
    }

    /**
     * Gets the name of the language.
     *
     * @return the language name
     */
    public String getLanguage() { return language; }

    /**
     * Sets the name of the language.
     *
     * @param language the language name
     */
    public void setLanguage(String language) { this.language = language; }

    /**
     * Gets the estimated number of speakers.
     *
     * @return the number of speakers
     */
    public long getTotalSpeakers() { return totalSpeakers; }

    /**
     * Sets the estimated number of speakers.
     *
     * @param totalSpeakers the number of speakers
     */
    public void setTotalSpeakers(long totalSpeakers) { this.totalSpeakers = totalSpeakers; }

    /**
     * Gets the speakers as a percentage of the world population.
     *
     * @return the percentage, from 0 to 100
     */
    public double getPercentageOfWorldPopulation() { return percentageOfWorldPopulation; }

    /**
     * Sets the speakers as a percentage of the world population.
     *
     * @param percentageOfWorldPopulation the percentage, from 0 to 100
     */
    public void setPercentageOfWorldPopulation(double percentageOfWorldPopulation) {
        this.percentageOfWorldPopulation = percentageOfWorldPopulation;
    }
}
