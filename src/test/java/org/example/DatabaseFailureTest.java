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
 *
 * Key terms used in this class:
 *  - mock:       a fake object that records calls and returns values we choose.
 *  - when(...).thenReturn(...): tells a mock what to return for a call.
 *  - when(...).thenThrow(...):  tells a mock to throw an exception instead.
 *  - mockStatic: fakes a STATIC method (DatabaseConnection.getConnection()),
 *                which a normal mock cannot do.
 *  - verify(...): checks that a method on a mock was really called.
 *
 * Each test follows three steps: ARRANGE (set up the fakes), ACT (call the
 * report method) and ASSERT (check the result).
 *
 * Tagged "unit" so CI runs it in the "Unit tests (no database)" step.
 */
@Tag("unit")
class DatabaseFailureTest {

    // ------------------------------------------------------------------
    // Group 1: the database connection is missing (getConnection() = null)
    // ------------------------------------------------------------------

    /**
     * Failure case: no database connection.
     * Expected: the capital city report returns an empty list, not null and
     * not an exception.
     */
    @Test
    void capitalReportIsEmptyWhenThereIsNoConnection() {
        // ARRANGE: replace the static method DatabaseConnection.getConnection().
        // The try-with-resources block removes the fake again at the closing
        // brace, so other tests are not affected.
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            // Make getConnection() return null, like a database that is down.
            db.when(DatabaseConnection::getConnection).thenReturn(null);

            // ACT: call the real report method. It asks for a connection and
            // gets null from our fake.
            List<CapitalCity> capitals = CapitalCityReport.getWorldCapitalCitiesByPopulation();

            // ASSERT: a safe empty list, never null.
            assertNotNull(capitals, "The report must return a list, never null");
            assertTrue(capitals.isEmpty(), "No connection means no rows");
        }
    }

    /**
     * Same failure case for the country report: it must also return an empty
     * list when there is no connection.
     */
    @Test
    void countryReportIsEmptyWhenThereIsNoConnection() {
        // ARRANGE: fake getConnection() so that it returns null.
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(null);

            // ACT and ASSERT: the report returns an empty list.
            assertTrue(CountryReport.getWorldCountriesByPopulation().isEmpty());
        }
    }

    /**
     * Same failure case for a single-number report: with no connection the
     * world population is 0, not an exception.
     */
    @Test
    void populationIsZeroWhenThereIsNoConnection() {
        // ARRANGE: fake getConnection() so that it returns null.
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(null);

            // ACT and ASSERT: 0 is the agreed "no data" value for these methods.
            assertEquals(0, AreaPopulationReport.getWorldPopulation());
        }
    }

    // ------------------------------------------------------------------
    // Group 2: the connection exists, but the query fails (SQLException)
    // ------------------------------------------------------------------

    /**
     * Failure case: the database goes away while the report runs.
     * Expected: the report catches the SQLException and returns an empty list.
     * (The stack trace in the test output is this exception, thrown on purpose.)
     */
    @Test
    void capitalReportIsEmptyWhenTheQueryThrowsSqlException() throws SQLException {
        // ARRANGE: a fake Connection. When the report asks it to create a
        // statement, it throws an SQLException, like a lost connection.
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenThrow(new SQLException("database went away"));

        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            // getConnection() now returns our fake connection, not null.
            db.when(DatabaseConnection::getConnection).thenReturn(connection);

            // ACT: the report runs and hits the exception inside its try block.
            List<CapitalCity> capitals = CapitalCityReport.getWorldCapitalCitiesByPopulation();

            // ASSERT: the report's catch block turned the error into an empty list.
            assertTrue(capitals.isEmpty(), "A failed query must give an empty list, not a crash");
        }
    }

    /**
     * Failure case: the SQL cannot be prepared (for example, a syntax error).
     * This report uses a PreparedStatement because it has a LIMIT ? parameter,
     * so we fake prepareStatement(...) instead of createStatement().
     */
    @Test
    void topNReportIsEmptyWhenPreparingTheStatementFails() throws SQLException {
        // ARRANGE: prepareStatement(...) throws for any SQL text (anyString()).
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("bad SQL"));

        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(connection);

            // ACT and ASSERT: top 5 capitals fails safely with an empty list.
            assertTrue(CapitalCityReport.getTopNCapitalCitiesInWorld(5).isEmpty());
        }
    }

    // ------------------------------------------------------------------
    // Group 3: success path with fake data (checks how rows are mapped)
    // ------------------------------------------------------------------

    /**
     * Success case with fake data: one fake row goes in, and the report must
     * turn it into one CapitalCity object with the right values.
     * This tests the mapping code (ResultSet columns to object fields)
     * without any real database.
     */
    @Test
    void capitalRowsAreMappedFromTheResultSet() throws SQLException {
        // ARRANGE: build the chain of fakes that the report code walks through:
        // Connection -> Statement -> ResultSet.
        Statement statement = mock(Statement.class);
        ResultSet rows = mock(ResultSet.class);
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(rows);

        // next() returns true once (one row) and then false (no more rows).
        when(rows.next()).thenReturn(true, false);

        // The values of that one fake row, by column name.
        when(rows.getString("Name")).thenReturn("Testville");
        when(rows.getString("Country")).thenReturn("Testland");
        when(rows.getLong("Population")).thenReturn(123456L);

        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(connection);

            // ACT: the real report code reads our fake row.
            List<CapitalCity> capitals = CapitalCityReport.getWorldCapitalCitiesByPopulation();

            // ASSERT: one object, with the values we put into the fake row.
            assertEquals(1, capitals.size());
            assertEquals("Testville", capitals.get(0).getName());
            assertEquals("Testland", capitals.get(0).getCountry());
            assertEquals(123456L, capitals.get(0).getPopulation());

            // ASSERT: try-with-resources must close both objects, even with
            // fake ones. verify(...) checks that close() was really called.
            verify(rows).close();
            verify(statement).close();
        }
    }

    /**
     * Same idea for the country report. It uses a PreparedStatement, so the
     * fake chain is Connection -> PreparedStatement -> ResultSet, and the
     * country mapping reads six columns.
     */
    @Test
    void countryRowsAreMappedFromTheResultSet() throws SQLException {
        // ARRANGE: the fake chain for a PreparedStatement.
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet rows = mock(ResultSet.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rows);

        // One fake row, then the end of the results.
        when(rows.next()).thenReturn(true, false);

        // The six columns that the country report reads.
        when(rows.getString("Code")).thenReturn("TST");
        when(rows.getString("Name")).thenReturn("Testland");
        when(rows.getString("Continent")).thenReturn("Europe");
        when(rows.getString("Region")).thenReturn("Western Europe");
        when(rows.getLong("Population")).thenReturn(5000L);
        when(rows.getString("Capital")).thenReturn("Testville");

        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(connection);

            // ACT: the real report code reads our fake row.
            List<Country> countries = CountryReport.getWorldCountriesByPopulation();

            // ASSERT: the values are mapped to the right fields.
            assertEquals(1, countries.size());
            assertEquals("TST", countries.get(0).getCode());
            assertEquals("Testland", countries.get(0).getName());
            assertEquals(5000L, countries.get(0).getPopulation());
            assertEquals("Testville", countries.get(0).getCapital());

            // ASSERT: the resources were closed.
            verify(rows).close();
            verify(statement).close();
        }
    }
}