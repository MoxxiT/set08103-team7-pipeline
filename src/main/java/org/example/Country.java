package org.example;

public class Country {
    private String code;
    private String name;
    private String continent;
    private String region;
    private long population;
    private String capital; // or int/long depending on how you map the ID/Name

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContinent() { return continent; }
    public void setContinent(String continent) { this.continent = continent; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public long getPopulation() { return population; }
    public void setPopulation(long population) { this.population = population; }

    public String getCapital() { return capital; }
    public void setCapital(String capital) { this.capital = capital; }
}