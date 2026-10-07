package org.example.model;

public class PopulationReport {
    private String name;
    private long totalPopulation;
    private long cityPopulation;
    private long nonCityPopulation;

    public PopulationReport(String name,
                            long totalPopulation,
                            long cityPopulation,
                            long nonCityPopulation)
    {
        this.name = name;
        this.totalPopulation = totalPopulation;
        this.cityPopulation = cityPopulation;
        this.nonCityPopulation = nonCityPopulation;
    }

    public long getTotalPopulation() {
        return totalPopulation;
    }

    public String getName() {
        return name;
    }
    public long getCityPopulation() {
        return cityPopulation;
    }
    public long getNonCityPopulation() {
        return nonCityPopulation;
    }
}
