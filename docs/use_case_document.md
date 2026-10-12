# SET08103 - Team 7: Population Reporting System Use Cases

This document defines the system use cases and architectural actors for the Population Reporting System, supporting Code Review 2 requirements.

## System Use Case Diagram

![Use Case Diagram](use-case-diagram.png)

---

### **Use Case 1: Generate Country Population Reports**
* **Use Case ID:** UC-01
* **Actor:** Organisation User
* **Description:** The user requests population reports for countries across the world, continents, or regions, ordered by largest population to smallest, or filtered by top $N$ populated countries.
* **Preconditions:** 
  1. The MySQL database (`world`) is running and accessible.
  2. The application establishes a successful database connection via `DatabaseConnection`.
* **Trigger:** The user executes `CountryReport.main()` or invokes world/continent/region report methods.
* **Main Flow of Events:**
  1. The user selects the country report criteria (e.g., world, continent, region, or top $N$).
  2. The system initiates connection via `DatabaseConnection.getConnection()`.
  3. The system executes the SQL query joining or filtering the `country` table and orders results by `Population DESC`.
  4. The system maps the `ResultSet` into `Country` domain model instances and stores them in a list.
  5. The system formats and prints the tabular output including Code, Name, Continent, Region, Population, and Capital.
  6. The system closes the connection via `DatabaseConnection.disconnect()`.
* **Alternative Flows:**
  * *3a. Connection failure:* The system catches `SQLException`, logs the error safely, and returns an empty list without crashing.

---

### **Use Case 2: Generate City Population Reports**
* **Use Case ID:** UC-02
* **Actor:** Organisation User
* **Description:** The user requests city population reports organized by world, continent, region, country, or district, ordered from largest population to smallest, or limited to top $N$ cities.
* **Preconditions:** 
  1. Database connection is active.
* **Trigger:** The user executes `CityReport.main()`.
* **Main Flow of Events:**
  1. The user selects city reporting parameters.
  2. The system connects to the database and executes the query joining `city` and `country` tables.
  3. The system maps the records to `City` domain objects (Name, Country, District, Population).
  4. The system displays the formatted table to the console output.
  5. The system disconnects from the database.

---

### **Use Case 3: Generate Capital City Reports**
* **Use Case ID:** UC-03
* **Actor:** Organisation User
* **Description:** The user requests capital city population reports for the world, a continent, or a region, ordered from largest population to smallest.
* **Preconditions:** 
  1. Database connection is active.
* **Trigger:** The user executes `CapitalCityReport.main()`.
* **Main Flow of Events:**
  1. The system executes an SQL join between `country` and `city` on the capital ID.
  2. The system maps results into `CapitalCity` domain objects (Name, Country, Population).
  3. The system sorts and displays the capital cities in tabular format.
  4. The system disconnects.

---

### **Use Case 4: Generate Population Composition Reports**
* **Use Case ID:** UC-04
* **Actor:** Organisation User
* **Description:** The user requests population composition reports for continents, regions, or countries, detailing total population, urban population (with percentage), and rural population (with percentage).
* **Preconditions:** 
  1. Database connection is active.
* **Trigger:** The user executes `PopulationReport.main()`.
* **Main Flow of Events:**
  1. The system queries total population and calculates urban population by summing city populations via a `LEFT JOIN`.
  2. The system calculates rural population and respective percentages.
  3. The system maps data into `PopulationComposition` objects and displays the formatted breakdown.
  4. The system disconnects.

---

### **Use Case 5: Generate Language Statistics Reports**
* **Use Case ID:** UC-05
* **Actor:** Organisation User
* **Description:** The user requests speaker statistics and world population percentages for major languages (Chinese, English, Hindi, Spanish, Arabic), ordered from greatest speakers to smallest.
* **Preconditions:** 
  1. Database connection is active.
* **Trigger:** The user executes `LanguageReport.main()`.
* **Main Flow of Events:**
  1. The system calculates total world population from the `country` table.
  2. The system queries total speakers for each target language using country populations and language percentages.
  3. The system sorts results in descending order by speaker count and maps them to `Language` objects.
  4. The system displays the language statistics table and disconnects.