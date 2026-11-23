package org.example;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that use Mockito to replace the database with fake objects.
 * No real database is needed, so these tests can check what happens when the
 * database is down or a query fails, which the integration tests cannot do.
 *
 * mockStatic replaces DatabaseConnection.getConnection() only inside each
 * try block, so the other tests still use the real database.
 */
@Tag("unit")
class DatabaseFailureTest {

    @Test
    void capitalReportIsEmptyWhenThereIsNoConnection() {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(null);

            List<CapitalCity> capitals = CapitalCityReport.getWorldCapitalCitiesByPopulation();

            assertNotNull(capitals, "The report must return a list, never null");
            assertTrue(capitals.isEmpty(), "No connection means no rows");
        }
    }

    @Test
    void countryReportIsEmptyWhenThereIsNoConnection() {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(null);

            assertTrue(CountryReport.getWorldCountriesByPopulation().isEmpty());
        }
    }

    @Test
    void populationIsZeroWhenThereIsNoConnection() {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(null);

            assertEquals(0, AreaPopulationReport.getWorldPopulation());
        }
    }

    @Test
    void capitalReportIsEmptyWhenTheQueryThrowsSqlException() throws SQLException {
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenThrow(new SQLException("database went away"));

        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(connection);

            List<CapitalCity> capitals = CapitalCityReport.getWorldCapitalCitiesByPopulation();

            assertTrue(capitals.isEmpty(), "A failed query must give an empty list, not a crash");
        }
    }

    @Test
    void topNReportIsEmptyWhenPreparingTheStatementFails() throws SQLException {
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("bad SQL"));

        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(connection);

            assertTrue(CapitalCityReport.getTopNCapitalCitiesInWorld(5).isEmpty());
        }
    }

    @Test
    void capitalRowsAreMappedFromTheResultSet() throws SQLException {
        Statement statement = mock(Statement.class);
        ResultSet rows = mock(ResultSet.class);
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(rows);
        when(rows.next()).thenReturn(true, false);
        when(rows.getString("Name")).thenReturn("Testville");
        when(rows.getString("Country")).thenReturn("Testland");
        when(rows.getLong("Population")).thenReturn(123456L);

        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(connection);

            List<CapitalCity> capitals = CapitalCityReport.getWorldCapitalCitiesByPopulation();

            assertEquals(1, capitals.size());
            assertEquals("Testville", capitals.get(0).getName());
            assertEquals("Testland", capitals.get(0).getCountry());
            assertEquals(123456L, capitals.get(0).getPopulation());
            // try-with-resources must close both objects, even with fake ones
            verify(rows).close();
            verify(statement).close();
        }
    }

    @Test
    void countryRowsAreMappedFromTheResultSet() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet rows = mock(ResultSet.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rows);
        when(rows.next()).thenReturn(true, false);
        when(rows.getString("Code")).thenReturn("TST");
        when(rows.getString("Name")).thenReturn("Testland");
        when(rows.getString("Continent")).thenReturn("Europe");
        when(rows.getString("Region")).thenReturn("Western Europe");
        when(rows.getLong("Population")).thenReturn(5000L);
        when(rows.getString("Capital")).thenReturn("Testville");

        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(connection);

            List<Country> countries = CountryReport.getWorldCountriesByPopulation();

            assertEquals(1, countries.size());
            assertEquals("TST", countries.get(0).getCode());
            assertEquals("Testland", countries.get(0).getName());
            assertEquals(5000L, countries.get(0).getPopulation());
            assertEquals("Testville", countries.get(0).getCapital());
            verify(rows).close();
            verify(statement).close();
        }
    }
}