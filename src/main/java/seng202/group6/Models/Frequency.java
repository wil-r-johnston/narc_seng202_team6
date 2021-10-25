package seng202.group6.Models;

public class Frequency {
    private String value;
    private int count;

    public Frequency(String value, int count) {
        this.value = value;
        this.count = count;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Frequency) {
            Frequency otherFrequency = (Frequency) other;
            return otherFrequency.getValue().equals(value) && otherFrequency.getCount() == count;
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return value + ": " + count;
    }

    public String getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public String getTime() {return value + ":00";}

    public String getDay() {

        switch(value) {
            case "1": return "Monday";
            case "2": return "Tuesday";
            case "3": return "Wednesday";
            case "4": return "Thursday";
            case "5": return "Friday";
            case "6": return "Saturday";
            default: return "Sunday";
        }
    }


    public String getMonth() {

        switch(value) {
            case "1": return "January";
            case "2": return "February";
            case "3": return "March";
            case "4": return "April";
            case "5": return "May";
            case "6": return "June";
            case "7": return "July";
            case "8": return "August";
            case "9": return "September";
            case "10": return "October";
            case "11": return "November";
            default: return "December";
        }
    }

}
