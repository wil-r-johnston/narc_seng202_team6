package seng202.group6.Services;

import javafx.scene.chart.XYChart;
import seng202.group6.Models.Crime;
import seng202.group6.Models.Frequency;
import seng202.group6.Models.TimeType;

import java.util.ArrayList;
import java.util.Comparator;



import static seng202.group6.Services.RankService.rankedTimeList;

public class GraphService {

    public static int maxValue = 0;
    public static int minValue = 0;
    public static String[] times;



    public static XYChart.Series<String, Number> getChartData(TimeType timeType, ArrayList<Crime> crimeData) {
        ArrayList<Frequency> timeFrequencyData;
        switch (timeType) {
            case HOUR_OF_DAY -> {
                minValue = 0;
                maxValue = 24;
                times = new String[]{"0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00",
                        "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00",
                        "22:00", "23:00"};
            }
            case DAY_OF_WEEK -> {
                maxValue = 8;
                minValue = 1;
                times = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            }
            default -> { //MONTH_OF_YEAR
                maxValue = 13;
                minValue = 1;
                times = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            }
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        timeFrequencyData = rankedTimeList(crimeData, timeType);

        timeFrequencyData.sort(Comparator.comparing(Frequency::getValue));
        for (int i = minValue; i < maxValue; i++) {
            boolean found = false;
            int index = 0;
            for (Frequency time : timeFrequencyData) {
                if (Integer.parseInt(time.getValue()) == i) {
                    found = true;
                    index = timeFrequencyData.indexOf(time);
                }
            }
            if (found) {
                if (timeType == TimeType.HOUR_OF_DAY) {
                    series.getData().add(new XYChart.Data<>(times[i], timeFrequencyData.get(index).getCount()));
                } else { //cater for the days of week and month of year
                    series.getData().add(new XYChart.Data<>(times[i-1], timeFrequencyData.get(index).getCount()));
                }
            } else {
                if (timeType == TimeType.HOUR_OF_DAY) {
                    series.getData().add(new XYChart.Data<>(times[i], 0));
                } else {
                    series.getData().add(new XYChart.Data<>(times[i-1], 0));
                }

            }

            }

        return series;
    }
}

