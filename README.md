# SET08103 - Team 7: Population Reporting System

[![CI Status](https://github.com/MoxxiT/set08103-team7-pipeline/actions/workflows/ci.yml/badge.svg)](https://github.com/MoxxiT/set08103-team7-pipeline/actions)

**Test Line Coverage:** 75.5% (measured on 23 November 2026, JaCoCo line coverage)

Welcome to the GitHub repository for Team 7 in SET08103 (Software Engineering Methods) at Edinburgh Napier University. This project is a population reporting system designed to retrieve and organize population statistics from an SQL database.

## Team Members & Roles

* **Trisztan** – Project Lead / Infrastructure & DevOps
* **David** – Analysis & Design
* **Dorjee** – Backend & Core Development
* **Adrian** – Testing & Documentation

## Tech Stack

* **Language:** Java 21
* **Build Tool:** Maven
* **Containerization:** Docker (Amazon Corretto)
* **CI/CD:** GitHub Actions

## GitFlow Branching Strategy

We follow a strict GitFlow branching model:

* **`master`** – Holds stable, official releases (e.g., tagged with `v1.0.0`).
* **`develop`** – The main integration branch for ongoing feature development.
* **Feature Branches** – Created from `develop` for individual tasks and merged back via Pull Requests.

## Requirements

* **Java 21** JDK (tested with 21.0.13)
* **Apache Maven** (tested with 3.10.2)
* **Docker Desktop** with Docker Compose (tested with 29.9.3)
* **Port 3306 free** on the host, because the Docker Compose database uses it

## Getting Started & Building Locally

### Prerequisites

* Java 21 Development Kit (JDK) installed
* Apache Maven installed

### Build Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/MoxxiT/set08103-team7-pipeline.git
   cd set08103-team7-pipeline
   ```

2. Start the database and wait until it is healthy:

   ```bash
   docker compose up -d db
   docker compose ps
   ```

3. Build and run all tests:

   ```bash
   mvn clean package
   ```

   Without the database running, the database tests are skipped.

4. Run the application: open `src/main/java/org/example/Main.java` in IntelliJ and run it, or run the whole stack with Docker:

   ```bash
   docker compose up --build --abort-on-container-exit --exit-code-from app
   ```
## Assumptions
- Population of a district = sum of the populations of the cities recorded in that district (the database has no district table).
- City names are not unique (e.g. three cities called Valencia). getCityPopulation(name) returns the largest match; use getCityPopulation(name, country) to be specific.
- Top N reports return an empty list when N is zero or negative.

## Release and Deployment

The release pipeline runs automatically when a version tag
such as `v1.0.0` is pushed to GitHub.

It builds and tests the application, creates a GitHub Release
containing the executable JAR, and publishes a Docker image
to GitHub Container Registry (GHCR).

### Run the published Docker image

The application requires a running MySQL database containing
the `world` database.

```bash
docker run --rm \
  -e DB_HOST=host.docker.internal \
  -e DB_USER=root \
  -e DB_PASSWORD=your_password \
  ghcr.io/moxxit/set08103-team7-pipeline:v1.0.0
```

### Database environment variables

- `DB_HOST` — MySQL server hostname.
- `DB_USER` — MySQL username.
- `DB_PASSWORD` — MySQL password.

Replace `your_password` with your actual database password.
The example uses `host.docker.internal` for MySQL running
on the host computer with Docker Desktop.

### Run locally with Docker Compose

```bash
mvn clean package -DskipTests
docker compose up --build
```

Docker Compose starts MySQL, waits until it is healthy,
and then runs the population reporting application.
