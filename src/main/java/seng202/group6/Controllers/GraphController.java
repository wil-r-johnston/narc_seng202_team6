package seng202.group6.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;

import seng202.group6.Models.Frequency;
import seng202.group6.Models.TimeType;

import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static seng202.group6.Services.GraphService.*;
import static seng202.group6.Services.RankService.rankedTypeList;

/**
 * Controller class for Graph screen in user interface, associated with graphScreen.fxml.
 */

public class GraphController extends MasterController implements Initializable {

    @FXML
    private Button homeButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button dataButton;

    @FXML
    private Button importButton;

    @FXML
    private PieChart pieChart;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    public CategoryAxis xAxis;

    private TimeType timeType = TimeType.HOUR_OF_DAY;

    private boolean flag = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        flag = false;

        try {
            applyChart();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapButton.setFocusTraversable(false);
        dataButton.setFocusTraversable(false);
        importButton.setFocusTraversable(false);
        homeButton.setFocusTraversable(false);

        pieChart.setVisible(false);

    }

    /**
     * Changes to home screen using method in MasterController when the home button is clicked
     */
    public void clickHome() throws IOException {
        changeToHomeScreen();
    }

    /**
     * Changes to map screen using method in MasterController when the map button is clicked
     */
    public void clickMap() throws IOException {
        changeToMapScreen();
    }

    /**
     * Changes to import screen using method in MasterController when the import button is clicked
     */
    public void clickImport() throws IOException {
        changeToImportScreen();
    }

    /**
     * Change to data screen using method in MasterController when the data button is clicked
     */
    public void clickData() throws IOException {
        changeToDataScreen();
    }

    /**
     * Method to make a line chart be created on the graph, it checks if the chart it is being requested to make has
     * already been made and if so it doesn't make it
     */
    private void applyChart() throws IOException {
        XYChart.Series<String, Number> series = getChartData(timeType, crimeData);
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

    /**
     * Set's the displayed graph to show crimes by hour of the day
     */
    public void clickDay() throws IOException {
        pieChart.setVisible(false);
        timeType = TimeType.HOUR_OF_DAY;
        xAxis.setLabel("Time of Day");
        applyChart();
        lineChart.setVisible(true);

    }

    /**
     * Displays a graph of the frequency of crimes by day of the week
     */
    public void clickWeek() throws IOException {
        pieChart.setVisible(false);
        timeType = TimeType.DAY_OF_WEEK;
        xAxis.setLabel("Day of Week");
        applyChart();
        lineChart.setVisible(true);
    }


    /**
     * Displays a graph of frequency of crime per month of the year
     */
    public void clickYear() throws IOException {
        pieChart.setVisible(false);
        timeType = TimeType.MONTH_OF_YEAR;
        xAxis.setLabel("Month of Year");
        applyChart();
        lineChart.setVisible(true);
    }


    /**
     * Causes the graph screen to display a pie chart showing the breakdown of crimes by type of crime
     */
    public void clickPie() {
        lineChart.setVisible(false);
        ArrayList<Frequency> data = rankedTypeList(crimeData);
        ObservableList<PieChart.Data> pcd = FXCollections.observableArrayList();
        for (Frequency crimeType : data) {
            pcd.add(new PieChart.Data(crimeType.getValue(), crimeType.getCount()));
        }

        if (!flag) {
            pieChart.setData(pcd);
        }
        pieChart.setVisible(true);
        flag = true;
    }

}
