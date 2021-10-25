package seng202.group6;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.group6.Models.Crime;
import seng202.group6.Services.SQLiteDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DBStepDefinitions {

    public static String jdbcUrl =  "jdbc:sqlite:cucumber.db";
    public static String tableName = "test";
    public static Crime testCrime = new Crime("JE266628", LocalDateTime.parse("2021-06-15T09:30"),
            "080XX S DREXEL AVE", "820", "THEFT",
            "$500 AND UNDER", false, false, 631, 8, "6",
            "STREET", 41.748486365, -87.602675062);

    @Before
    public static void setUp() throws SQLException {
        SQLiteDatabase.connectToDatabase(jdbcUrl);
    }

    //INSERTING INTO TABLE
    //TEST CASE 1 - VALID INSERT

    @Given("There is a valid table in the database")
    public void thereIsAValidTableInTheDatabase() throws SQLException {
        SQLiteDatabase.createTable(tableName);
    }

    @When("I add a new crime with valid crime fields")
    public void iAddANewCrimeWithValidCrimeFields() {
        try {
            SQLiteDatabase.insertIntoTable(tableName, testCrime);
            SQLiteDatabase.endTransaction();
        } catch (SQLException e) {}
    }

    @Then("The crime should be added to the database")
    public void theCrimeShouldBeAddedToTheDatabase() throws SQLException {
        ResultSet result = SQLiteDatabase.selectAllFromTable(tableName);
        SQLiteDatabase.endTransaction();
        assertEquals(testCrime, SQLiteDatabase.convertResultSet(result).get(0));
        result.close();
    }

    //INSERTING INTO TABLE
    //TEST CASE 2 - DUPLICATE INSERT

    @When("I add a duplicate crime to the table")
    public void iAddADuplicateCrimeToTheTable() throws SQLException {
        SQLiteDatabase.createTable(tableName);
        try {
            SQLiteDatabase.insertIntoTable(tableName, testCrime);
            SQLiteDatabase.endTransaction();
        } catch (SQLException e) {}
    }

    @Then("The crime should not be added to the database")
    public void theCrimeShouldNotBeAddedToTheDatabase() {
        assertThrows(SQLException.class, ()-> {
            SQLiteDatabase.insertIntoTable(tableName, testCrime);
        });
    }

    //DELETING FROM DATABASE
    //TEST CASE 1 - DELETING ONE RECORD

    @Given("There is a valid crime in a table")
    public void thereIsAValidCrimeInATable() throws SQLException {
        SQLiteDatabase.createTable(tableName);
        try {
            SQLiteDatabase.insertIntoTable(tableName, testCrime);
            SQLiteDatabase.endTransaction();
        } catch (SQLException e) {}
    }

    @When("I delete the crime")
    public void iDeleteTheCrime() throws SQLException {
        SQLiteDatabase.deleteFromTable(tableName, testCrime);
    }

    @Then("The crime should be removed from the database")
    public void theCrimeShouldBeRemovedFromTheDatabase() throws SQLException {
        String sql = "SELECT * FROM " + tableName + " WHERE case_id = '" + testCrime.getCaseNumber() + "';";
        ResultSet result = SQLiteDatabase.executeQuery(sql);
        SQLiteDatabase.endTransaction();
        assertFalse(result.next());
        result.close();
    }

    //DELETING FROM DATABASE
    //TEST CASE 2 - DELETING A TABLE

    @When("I delete the table")
    public void iDeleteTheTable() throws SQLException {
        SQLiteDatabase.dropTable(tableName);
    }

    @Then("The table should be removed")
    public void theTableShouldBeRemoved() throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type ='table' AND name = '" + tableName + "';";
        ResultSet result = SQLiteDatabase.executeQuery(sql);
        SQLiteDatabase.endTransaction();
        assertFalse(result.next());
        result.close();
    }

}
