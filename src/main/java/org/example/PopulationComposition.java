package org.example;

public class PopulationComposition {
    private String name;
    private long totalPopulation;
    private long urbanPopulation;
    private double urbanPercentage;
    private long ruralPopulation;
    private double ruralPercentage;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getTotalPopulation() { return totalPopulation; }
    public void setTotalPopulation(long totalPopulation) { this.totalPopulation = totalPopulation; }

    public long getUrbanPopulation() { return urbanPopulation; }
    public void setUrbanPopulation(long urbanPopulation) { this.urbanPopulation = urbanPopulation; }

    public double getUrbanPercentage() { return urbanPercentage; }
    public void setUrbanPercentage(double urbanPercentage) { this.urbanPercentage = urbanPercentage; }

    public long getRuralPopulation() { return ruralPopulation; }
    public void setRuralPopulation(long ruralPopulation) { this.ruralPopulation = ruralPopulation; }

    public double getRuralPercentage() { return ruralPercentage; }
    public void setRuralPercentage(double ruralPercentage) { this.ruralPercentage = ruralPercentage; }
}