package seng202.group6.ModelTests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.group6.Models.Crime;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class CrimeTest {


    @Test
    public void differentCrimesTest() {
        LocalDateTime time = LocalDateTime.now();
        Crime crime1 = new Crime("jfc123",
                time,
                "023XXXX",
                "a1",
                "crime",
                "crimecrime",
                true,
                true,
                2,
                8,
                "fbi",
                "fbi",
                23.2,
                8.9);
        Crime crime2 = new Crime("jfc123",
                time,
                "023XXXX",
                "a1",
                "crime",
                "crimecrime",
                true,
                false,
                2,
                8,
                "fbi",
                "fbi",
                23.2,
                8.9);

        assertFalse(crime1.equals(crime2));

    }

    @Test
    public void sameCrimesTest() {
        LocalDateTime time = LocalDateTime.now();
        Crime crime1 = new Crime("jfc123",
                time,
                "023XXXX",
                "a1",
                "crime",
                "crimecrime",
                true,
                true,
                2,
                8,
                "fbi",
                "fbi",
                23.2,
                8.9);
        Crime crime2 = new Crime("jfc123",
                time,
                "023XXXX",
                "a1",
                "crime",
                "crimecrime",
                true,
                true,
                2,
                8,
                "fbi",
                "fbi",
                23.2,
                8.9);
        assertTrue(crime1.equals(crime2));
    }

    @Test
    public void getReadableDateTest() {
        LocalDateTime time = LocalDateTime.of(2020, 12, 31, 23, 59, 0);
        Crime crime = new Crime();
        crime.setDate(time);
        assertEquals("31-12-2020 23:59:00", crime.getReadableDate());
    }


}
