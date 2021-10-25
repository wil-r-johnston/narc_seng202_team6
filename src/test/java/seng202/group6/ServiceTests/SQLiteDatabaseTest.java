package seng202.group6.ServiceTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import seng202.group6.Models.Crime;
import seng202.group6.Services.SQLiteDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SQLiteDatabaseTest {
    public static String tableName;

    @BeforeAll
    public static void setUp() throws SQLException {
        String jdbcUrl = "jdbc:sqlite:test.db";
        tableName = "test";
        SQLiteDatabase.connectToDatabase(jdbcUrl);
    }

    @AfterEach
    public void clearDatabase() throws SQLException {
        SQLiteDatabase.endTransaction();

        String sql = "DELETE FROM " + tableName + ";";    //Clear all rows from table
        Statement statement = SQLiteDatabase.getConnection().createStatement();
        statement.executeUpdate(sql);

        SQLiteDatabase.endTransaction();
    }

    @Test
    public void connectToDatabaseTest() {
        assertNotNull(SQLiteDatabase.getConnection());
    }

    @Test
    public void createTableTest() throws SQLException {
        ResultSet tableNames;
        SQLiteDatabase.createTable(tableName);

        String sql = "SELECT name FROM sqlite_master WHERE type ='table' AND name = '" + tableName + "';";
        ResultSet result = SQLiteDatabase.executeQuery(sql);
        SQLiteDatabase.endTransaction();
        assertEquals(result.getString(1), tableName);
        result.close();
    }

    @Test
    public void insertIntoTableTest() throws SQLException {
        Crime testCrime1 = new Crime("JE266628", LocalDateTime.parse("2021-06-15T09:30"),
                "080XX S DREXEL AVE", "820", "THEFT",
                "$500 AND UNDER", false, false, 631, 8, "6",
                "STREET", 41.748486365, -87.602675062);
        Crime testCrime2 = new Crime("JE266416", LocalDateTime.parse("2021-06-15T12:15"),
                "115XX S YALE AVE", "4387", "OTHER OFFENSE",
                "VIOLATE ORDER OF PROTECTION", false, true, 522, 34, "26",
                "RESIDENCE - PORCH / HALLWAY", 41.684663397, -87.628870501);
        ResultSet result;
        int numCrimes = 0;

        SQLiteDatabase.insertIntoTable(tableName, testCrime1);
        SQLiteDatabase.insertIntoTable(tableName, testCrime2);
        result = SQLiteDatabase.selectAllFromTable(tableName);

        while(result.next()) {
            numCrimes++;
        }
        result.close();
        SQLiteDatabase.endTransaction();

        assertEquals(2, numCrimes);
    }

    @Test
    public void insertDuplicateCrimeTest() throws SQLException {
        Crime testCrime1 = new Crime("JE277728", LocalDateTime.parse("2021-06-15T09:30"),
                "080XX S DREXEL AVE", "820", "THEFT",
                "$500 AND UNDER", false, false, 631, 8, "6",
                "STREET", 41.748486365, -87.602675062);
        Crime testCrime2 = new Crime("JE277728", LocalDateTime.parse("2021-06-15T09:30"),
                "080XX S DREXEL AVE", "820", "THEFT",
                "$500 AND UNDER", false, false, 631, 8, "6",
                "STREET", 41.748486365, -87.602675062);
        SQLiteDatabase.insertIntoTable(tableName, testCrime1);
        assertThrows(SQLException.class, ()-> {
            SQLiteDatabase.insertIntoTable(tableName, testCrime2);
        });
    }

    @Test
    public void updateCrimeTest() throws SQLException {
        Crime testCrime1 = new Crime("JE266628", LocalDateTime.parse("2021-06-15T09:30"),
                "080XX S DREXEL AVE", "820", "THEFT",
                "$500 AND UNDER", false, false, 631, 8, "6",
                "STREET", 41.748486365, -87.602675062);
        Crime testCrime2 = new Crime("JE266628", LocalDateTime.parse("2021-06-15T09:30"),
                "080XX S DREXEL AVE", "820", "HOMICIDE",
                "FIRST DEGREE MURDER", false, false, 631, 8, "6",
                "STREET", 41.748486365, -87.602675062);
        SQLiteDatabase.insertIntoTable(tableName, testCrime1);
        SQLiteDatabase.updateInTable(tableName, testCrime2);
        SQLiteDatabase.endTransaction();
        ResultSet result = SQLiteDatabase.selectAllFromTable(tableName);
        assertEquals(testCrime2, SQLiteDatabase.convertResultSet(result).get(0));
        result.close();
    }

}
