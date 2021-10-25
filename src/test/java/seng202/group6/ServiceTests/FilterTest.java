package seng202.group6.ServiceTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import seng202.group6.Controllers.ImportController;
import seng202.group6.Models.Crime;
import seng202.group6.Services.Filter;
import seng202.group6.Services.SQLiteDatabase;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTest {

    @BeforeAll
    public static void setUp() {
        ImportController.currentTable = "testTable";
    }

    @Test
    public void testBlankFilter() {
        Filter filter = new Filter();
        String query = filter.queryBuilder();

        String expected = "SELECT * FROM testTable;";

        assertEquals(expected, query);
    }

    @Test
    public void testFilter() {
        Filter filter = new Filter();

        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();
        filter.setStart(start);
        filter.setEnd(end);

        HashSet<String> set = new HashSet<>();
        set.add("Test1");
        set.add("Test2");
        filter.setTypes(set);
        filter.setLocations(set);
        filter.setArrest(true);
        filter.setDomestic(false);
        filter.setBeats("12");
        filter.setWards("12, 13");

        String query = filter.queryBuilder();

        String expected = "SELECT * FROM testTable " +
                "WHERE occurrence_date >= '" + LocalDateTime.of(start, LocalTime.MIDNIGHT) + "' " +
                "AND occurrence_date < '" + LocalDateTime.of(end.plusDays(1), LocalTime.MIDNIGHT) + "' " +
                "AND primary_description IN ('Test1', 'Test2') " +
                "AND location IN ('Test1', 'Test2') " +
                "AND arrest = true " +
                "AND domestic = false " +
                "AND beat IN (12) " +
                "AND ward IN (12, 13);";

        assertEquals(expected, query);
    }

    @Test
    public void testApplyFilter() throws SQLException {
        String jdbcUrl = "jdbc:sqlite:test.db";
        Filter filter = new Filter();
        filter.setDomestic(true);
        SQLiteDatabase.connectToDatabase(jdbcUrl);
        SQLiteDatabase.createTable("testTable");

        Crime testCrime1 = new Crime("JE266628", LocalDateTime.parse("2021-06-15T09:30"),
                "080XX S DREXEL AVE", "820", "THEFT",
                "$500 AND UNDER", false, false, 631, 8, "6",
                "STREET", 41.748486365, -87.602675062);
        Crime testCrime2 = new Crime("JE266416", LocalDateTime.parse("2021-06-15T12:15"),
                "115XX S YALE AVE", "4387", "OTHER OFFENSE",
                "VIOLATE ORDER OF PROTECTION", false, true, 522, 34, "26",
                "RESIDENCE - PORCH / HALLWAY", 41.684663397, -87.628870501);

        SQLiteDatabase.insertIntoTable(ImportController.currentTable, testCrime1);
        SQLiteDatabase.insertIntoTable(ImportController.currentTable, testCrime2);
        SQLiteDatabase.endTransaction();
        ArrayList<Crime> result = filter.applyFilter();

        assertEquals(testCrime2, result.get(0));

        String sql = "DELETE FROM " + ImportController.currentTable + ";";    //Clear all rows from table
        Statement statement = SQLiteDatabase.getConnection().createStatement();
        statement.executeUpdate(sql);
        SQLiteDatabase.endTransaction();
    }
}
