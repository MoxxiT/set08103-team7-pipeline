package org.example;

import java.util.List;

/**
 * Entry point of the application (the JAR and the Docker container run this class).
 * It runs one example of every report type so a single run shows the whole system working.
 * Each report class also has its own main method if you want to run it on its own.
 */
public class Main {

    private static final int TOP_N = 5;

    public static void main(String[] args) {
        try {
            run();
        } finally {
            DatabaseConnection.disconnect();
        }
    }

    static void run() {
        // ----- Country reports -----
        section("Countries: top " + TOP_N + " in the world");
        CountryReport.displayCountries(CountryReport.getTopNCountriesInWorld(TOP_N));

        section("Countries: top " + TOP_N + " in Europe (continent)");
        CountryReport.displayCountries(CountryReport.getTopNCountriesByContinent("Europe", TOP_N));

        section("Countries: top " + TOP_N + " in Western Europe (region)");
        CountryReport.displayCountries(CountryReport.getTopNCountriesByRegion("Western Europe", TOP_N));

        // ----- City reports -----
        section("Cities: top " + TOP_N + " in the world");
        CityReport.displayCities(CityReport.getTopNCitiesInWorld(TOP_N));

        section("Cities: top " + TOP_N + " in Japan (country)");
        CityReport.displayCities(CityReport.getTopNCitiesByCountry("Japan", TOP_N));

        section("Cities: top " + TOP_N + " in California (district)");
        CityReport.displayCities(CityReport.getTopNCitiesByDistrict("California", TOP_N));

        // ----- Capital city reports -----
        section("Capital cities: top " + TOP_N + " in the world");
        CapitalCityReport.displayCapitalCities(CapitalCityReport.getTopNCapitalCitiesInWorld(TOP_N));

        section("Capital cities: top " + TOP_N + " in Europe (continent)");
        CapitalCityReport.displayCapitalCities(
                CapitalCityReport.getTopNCapitalCitiesByContinent("Europe", TOP_N));

        // ----- Population in cities / not in cities -----
        section("Population living in and out of cities: continents");
        PopulationReport.displayComposition(PopulationReport.getContinentPopulationComposition());

        section("Population living in and out of cities: first " + TOP_N + " regions");
        PopulationReport.displayComposition(firstN(PopulationReport.getRegionPopulationComposition(), TOP_N));

        section("Population living in and out of cities: first " + TOP_N + " countries");
        PopulationReport.displayComposition(firstN(PopulationReport.getCountryPopulationComposition(), TOP_N));

        // ----- Population of an area -----
        section("Population of an area");
        AreaPopulationReport.displayPopulation("World", AreaPopulationReport.getWorldPopulation());
        AreaPopulationReport.displayPopulation("Europe (continent)",
                AreaPopulationReport.getContinentPopulation("Europe"));
        AreaPopulationReport.displayPopulation("Western Europe (region)",
                AreaPopulationReport.getRegionPopulation("Western Europe"));
        AreaPopulationReport.displayPopulation("Japan (country)",
                AreaPopulationReport.getCountryPopulation("Japan"));
        AreaPopulationReport.displayPopulation("California (district)",
                AreaPopulationReport.getDistrictPopulation("California"));
        AreaPopulationReport.displayPopulation("Tokyo (city)",
                AreaPopulationReport.getCityPopulation("Tokyo"));

        // ----- Languages -----
        section("Languages: speakers and share of world population");
        LanguageReport.displayLanguageStatistics(LanguageReport.getLanguageStatistics());
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    private static <T> List<T> firstN(List<T> list, int n) {
        return list.subList(0, Math.min(n, list.size()));
    }
}