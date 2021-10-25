package seng202.group6.Services;

import seng202.group6.Models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Class used for ranking of crimes in terms of area (block number), type of crime,
 * and time. You can rank by different measures of time by passing a TimeType.
 */


public class RankService {


    /**
     * Rank crimes using the given function
     * @param crimes An ArrayList of Crimes to rank
     * @param function A function that takes a crime and returns a property of that crime
     *                 For example (crime -> crime.getPrimaryDescription())
     * @return An ArrayList of Frequency objects.
     */
    private static ArrayList<Frequency> rankedList(ArrayList<Crime> crimes, Function<Crime, String> function) {
        HashMap<String, Integer> frequencies = new HashMap<>();
        for (Crime crime : crimes) {
            String key = function.apply(crime);
            frequencies.put(key, frequencies.getOrDefault(key, 0) + 1);
        }

        ArrayList<Frequency> data = new ArrayList<>();
        for (String key : frequencies.keySet()) {
            data.add(new Frequency(key, frequencies.get(key)));
        }

        data.sort(Comparator.comparing(Frequency::getCount));
        Collections.reverse(data);
        return data;
    }

    /**
     * Sorts an array of crimes into an ordered list of frequencies based on their primary description
     * @param crimes An array list of Crime objects
     * @return A sorted array list of Frequencies
     */
    public static ArrayList<Frequency> rankedTypeList(ArrayList<Crime> crimes) {
        return rankedList(crimes, Crime::getPrimaryDescription);

    }



    /**
     * Sorts an ArrayList of crimes into an ordered list of frequencies based on their block number (the first
     * three numbers of the block)
     * @param crimes An array list of Crime objects
     * @return A sorted array list of Frequencies
     */
    public static ArrayList<Frequency> rankedAreaList(ArrayList<Crime> crimes) {
        return rankedList(crimes, crime -> crime.getBlock().substring(0, 3));
    }

    /**
     * Sorts an ArrayList of crimes into an ordered list of frequencies based on their current time.
     * Different measures of time can be selected by changing timeType
     * @param crimes An array list of Crime objects
     * @param timeType A TimeType enum representing what type of time filtering is required.
     * @return A sorted array list of Frequencies
     */
    public static ArrayList<Frequency> rankedTimeList(ArrayList<Crime> crimes, TimeType timeType) {

        ArrayList<Frequency> out;

        switch (timeType) {
            case MONTH_OF_YEAR ->
                out = rankedList(crimes, crime -> String.valueOf(crime.getDate().getMonthValue()));
            case DAY_OF_WEEK ->
                out = rankedList(crimes, crime -> String.valueOf(crime.getDate().getDayOfWeek().getValue()));
            default -> //HOUR_OF_DAY
                out = rankedList(crimes, crime -> String.valueOf(crime.getDate().getHour()));
        }
        return out;
    }



   

}
