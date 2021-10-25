package seng202.group6.ServiceTests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import seng202.group6.Models.Crime;
import seng202.group6.Models.Frequency;
import seng202.group6.Models.TimeType;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seng202.group6.Services.RankService.*;

public class RankServiceTest {

    private static ArrayList<Crime> crimes = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        crimes.add(new Crime("JE163990",
                LocalDateTime.of(2020, 11, 21, 3, 5, 0),
                "073XX S SOUTH SHORE DR",  "820", "THEFT",
                "$500 AND UNDER", true, false,
                0,334, "7", "6", 11.23, 13133.21));
        crimes.add(new Crime("JE163990",
                LocalDateTime.of(2020, 12, 23, 3, 5, 0),
                "073XX S SOUTH SHORE DR", "820", "THEFT",
                "$500 AND UNDER", true, false,
                0,334, "7", "6", 11.23, 13133.21));
        crimes.add(new Crime("JE163990",
                LocalDateTime.of(2020, 10, 23, 5, 5, 0),
                "827XX S SOUTH SHORE DR", "820", "THEFT",
                "$500 AND UNDER", true, false,
                0,334, "7", "6", 11.23, 13133.21));
    }

    @Test
    public void testHourlyRank() {
        ArrayList<Frequency> hourly = rankedTimeList(crimes, TimeType.HOUR_OF_DAY);

        ArrayList<Frequency> expectedHourly = new ArrayList<>();
        expectedHourly.add(new Frequency("3", 2));
        expectedHourly.add(new Frequency("5", 1));

        assertEquals(expectedHourly, hourly);
    }

    @Test
    public void testWeeklyRank() {
        ArrayList<Frequency> weekly = rankedTimeList(crimes, TimeType.DAY_OF_WEEK);
        ArrayList<Frequency> expectedWeekly = new ArrayList<>();
        expectedWeekly.add(new Frequency("6", 1));
        expectedWeekly.add(new Frequency("5", 1));
        expectedWeekly.add(new Frequency("3", 1));


        //All have same count, so could be in any order
        assertTrue(weekly.containsAll(expectedWeekly));
        assertEquals(expectedWeekly.size(), weekly.size());



    }

    @Test
    public void testYearlyRank() {
        ArrayList<Frequency> yearly = rankedTimeList(crimes, TimeType.MONTH_OF_YEAR);
        ArrayList<Frequency> expectedYearly = new ArrayList<>();
        expectedYearly.add(new Frequency("10", 1));
        expectedYearly.add(new Frequency("11", 1));
        expectedYearly.add(new Frequency("12", 1));

        //All have same count, so could be in any order
        assertTrue(yearly.containsAll(expectedYearly));
        assertEquals(expectedYearly.size(), yearly.size());
    }

    @Test
    public void testTypeRank() {
        ArrayList<Frequency> types = rankedTypeList(crimes);
        ArrayList<Frequency> expectedTypes = new ArrayList<>();
        expectedTypes.add(new Frequency("THEFT", 3));

        assertEquals(expectedTypes, types);

    }

    @Test
    public void testAreaRank() {
        ArrayList<Frequency> areas = rankedAreaList(crimes);
        ArrayList<Frequency> expectedAreas = new ArrayList<>();
        expectedAreas.add(new Frequency("073", 2));
        expectedAreas.add(new Frequency("827", 1));

        assertEquals(expectedAreas, areas);

    }






}
