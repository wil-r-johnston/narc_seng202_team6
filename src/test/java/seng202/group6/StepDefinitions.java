package seng202.group6;

import io.cucumber.java.bs.A;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import seng202.group6.Models.Crime;
import seng202.group6.Models.Frequency;
import seng202.group6.Models.TimeType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static seng202.group6.Services.RankService.*;


public class StepDefinitions {

    //Ranking 1: by crime type
    ArrayList<Crime> crimeArrayList = new ArrayList<>();
    ArrayList<Frequency> expectedCrimeList = new ArrayList<>();

    @Given("I add a new crime with the crime type {string}")
    public void theCrimeType(String crime) {
        Crime crime1 = new Crime();
        crime1.setPrimaryDescription(crime);
        crimeArrayList.add(crime1);
    }

    @When("I rank crimes")
    public void iRankCrimes() {
        expectedCrimeList = rankedTypeList(crimeArrayList);
    }

    @Then("the most common crime should be {string} with frequency value {int}")
    public void theMostCommonCrimeShouldBeWithFrequencyValue(String crimeName, Integer frequency) {
        Frequency freq = new Frequency(crimeName, frequency);
        assert(expectedCrimeList.get(0).equals(freq));
    }

    //Ranking 2: crime by area
    ArrayList<Crime> crimeAreaArrayList = new ArrayList<>();
    ArrayList<Frequency> expectedAreaCrimeList = new ArrayList<>();

    @Given("I add {int} new crimes with the crime area {string}")
    public void iAddNewCrimesWithTheCrimeArea(Integer count, String area) {

        for (int i = 0; i < count; i++) {
            Crime c1 = new Crime();
            c1.setBlock(area);
            crimeAreaArrayList.add(c1);
        }
    }

    @When("I rank crimes by area")
    public void iRankCrimesByArea() {
        expectedAreaCrimeList = rankedAreaList(crimeAreaArrayList);
    }

    @Then("the most common crime area should be {string} with frequency {int} followed by {string} with frequency {int}")
    public void theMostCommonCrimeAreaShouldBeWithFrequencyFollowedByWithFrequency(String crimeArea1, Integer frequency1, String crimeArea2, Integer frequency2) {
        Frequency freq1 = new Frequency(crimeArea1, frequency1);
        Frequency freq2 = new Frequency(crimeArea2, frequency2);

        assert(expectedAreaCrimeList.get(0).equals(freq1));
        assert(expectedAreaCrimeList.get(1).equals(freq2));
    }

    //Ranking 3: crimes by time
    ArrayList<Crime> crimeTimeArrayList = new ArrayList<>();
    ArrayList<Frequency> expectedTimeCrimeList = new ArrayList<>();

    @Given("I add {int} new crimes with the crime time {int}")
    public void iAddNewCrimesWithTheCrimeTime(Integer number, Integer time) {
        for (int i = 0; i < number; i++) {
            Crime c1 = new Crime();
            c1.setDate(LocalDateTime.of(2020, 11, 21, time, 5, 0));
            crimeTimeArrayList.add(c1);
        }
    }

    @When("I rank crimes by time")
    public void iRankCrimesByTime() {
        expectedTimeCrimeList = rankedTimeList(crimeTimeArrayList, TimeType.HOUR_OF_DAY);
    }

    @Then("the most common crime area should be {string} with frequency {int}")
    public void theMostCommonCrimeAreaShouldBeWithFrequency(String time, Integer frequency) {
        Frequency freq1 = new Frequency(time, frequency);
        assert(expectedTimeCrimeList.get(0).equals(freq1));
    }

}