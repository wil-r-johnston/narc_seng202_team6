package seng202.group6.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import seng202.group6.Models.Frequency;
import seng202.group6.Models.TimeType;
import seng202.group6.Services.RankService;

import java.io.IOException;
import java.util.ArrayList;

public class ViewRankingController extends MasterController {

    @FXML
    protected TableView<Frequency> tableView;

    @FXML
    private TableColumn<Frequency, String> valueColumn;

    @FXML
    private TableColumn<Frequency, String> frequencyColumn;

    @FXML
    private Button hourButton;

    @FXML
    private Button dayButton;

    @FXML
    private Button monthButton;

    /**
     * Method initializes the table to be viewed and feeds it the data needed after naming the columns appropriately
     */
    public void initialize(String title, ArrayList<Frequency> data) {

        tableView.setFocusTraversable(false);
        valueColumn.setText(title);

        if (title.equals("Hour of Day")) {
            hourButton.setVisible(true);
            dayButton.setVisible(true);
            monthButton.setVisible(true);
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        } else {
            hourButton.setVisible(false);
            dayButton.setVisible(false);
            monthButton.setVisible(false);
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        }

        frequencyColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        tableView.setItems(FXCollections.observableArrayList(data));

    }

    /**
     * Method switches table to hour of day and frequency
     */
    public void changeToHourTable() {
        valueColumn.setText("Hour of Day");
        ArrayList<Frequency> data = new ArrayList<>(RankService.rankedTimeList(crimeData, TimeType.HOUR_OF_DAY));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        tableView.setItems(FXCollections.observableArrayList(data));

    }

    /**
     * Method switches table to day of week and frequency
     */
    public void changeToDayTable() {
        valueColumn.setText("Day of Week");
        ArrayList<Frequency> data = new ArrayList<>(RankService.rankedTimeList(crimeData, TimeType.DAY_OF_WEEK));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        tableView.setItems(FXCollections.observableArrayList(data));
    }

    /**
     * Method switches table to month of year and frequency
     */
    public void changeToMonthTable() {
        valueColumn.setText("Month of Year");
        ArrayList<Frequency> data = new ArrayList<>(RankService.rankedTimeList(crimeData, TimeType.MONTH_OF_YEAR));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        tableView.setItems(FXCollections.observableArrayList(data));
    }


}
