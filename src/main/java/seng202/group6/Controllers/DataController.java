package seng202.group6.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import seng202.group6.Models.Crime;
import seng202.group6.Models.TimeType;
import seng202.group6.Services.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller class for data screen in user interface, associated with dataScreen.fxml.
 * Is a child class of DataController
 */

public class DataController extends MasterController implements Initializable {

    private Set<String> types = new HashSet<>();
    private Set<String> locations = new HashSet<>();



    @FXML
    private Button homeButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button dataButton;

    @FXML
    private Button importButton;

    @FXML
    private Button graphButton;

    @FXML
    protected TableView<Crime> tableView;

    @FXML
    private TableColumn<Crime, String> caseNumColumn;

    @FXML
    private TableColumn<Crime, String> primaryDescColumn;

    @FXML
    private TableColumn<Crime, String> locationColumn;

    @FXML
    private TableColumn<Crime, String> dateColumn;

    @FXML
    private TableColumn<Crime, String> beatColumn;

    @FXML
    private TableColumn<Crime, String> wardColumn;

    @FXML
    private TableColumn<Crime, String> arrestColumn;

    @FXML
    private TableColumn<Crime, String> domesticColumn;

    @FXML
    private Button viewCrime;

    @FXML
    private VBox filterBox;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private MenuButton crimeTypeDropdown;

    @FXML
    private MenuButton locationDropdown;

    @FXML
    private RadioButton anyArrest; //Likewise with needing?

    @FXML
    private RadioButton yesArrest;

    @FXML
    private RadioButton noArrest;

    @FXML
    private RadioButton anyDomestic; //Likewise with needing?

    @FXML
    private RadioButton yesDomestic;

    @FXML
    private RadioButton noDomestic;


    @FXML
    private TextField wardSearch;

    @FXML
    private TextField beatSearch;

    @FXML
    private Button addCrime;

    @FXML
    private Button editCrime;

    @FXML
    private Button deleteCrime;

    @FXML
    private Pane mapPane;

    @FXML
    private Button reloadMapButton;


    /**
     * Method to override initialise method from Initializable interface. Decides whether
     * to show map or table based on which button is clicked. Initializes all buttons and
     * dropdowns. Populates table with data in crimeData. Sets all buttons
     * to not be traversable so they cannot be clicked by pressing tab + enter or arrow keys
     * + enter while on the application.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        homeButton.setFocusTraversable(false);
        mapButton.setFocusTraversable(false);
        importButton.setFocusTraversable(false);
        graphButton.setFocusTraversable(false);

        tableView.setFocusTraversable(false);

        WebView mapView = DynamicMapService.getMapView();
        mapView.setMaxSize(mapPane.getPrefWidth(), mapPane.getPrefHeight());
        mapPane.getChildren().add(mapView);

        buildFilterSets();
        buildDropdowns();

        filterBox.setVisible(true);
        caseNumColumn.setCellValueFactory(new PropertyValueFactory<>("caseNumber"));
        primaryDescColumn.setCellValueFactory(new PropertyValueFactory<>("primaryDescription"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("readableLocation"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("readableDate"));
        wardColumn.setCellValueFactory(new PropertyValueFactory<>("readableWard"));
        beatColumn.setCellValueFactory(new PropertyValueFactory<>("readableBeat"));
        arrestColumn.setCellValueFactory(new PropertyValueFactory<>("readableArrest"));
        domesticColumn.setCellValueFactory(new PropertyValueFactory<>("readableDomestic"));

        tableView.setItems(FXCollections.observableArrayList(crimeData));

        if (!choseMap) {
            switchToTable();
        } else {
            switchToMap();
        }

    }

    /**
     * Method to view detailed description of a specific crime, checks if a crime is selected
     * from the data table and returns error message if not. Calls function from MasterController
     * if a crime has been selected
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void selectCrime() throws IOException {
        Crime crime = tableView.getSelectionModel().getSelectedItem();
        if (crime != null) {
            launchViewScreen(crime);
        }
    }

    /**
     * Method to call change to home screen method in MasterController when the home button
     * is clicked
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void clickHome() throws IOException {
        changeToHomeScreen();
    }

    /**
     * Switches to view the map instead of the table.
     */
    public void clickMap() {
        switchToMap();
    }

    /**
     * Switches to view table instead of the map.
     */
    public void clickData() {
        switchToTable();
    }

    /**
     * Method to call change to import screen method in MasterController when the import button
     * is clicked
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void clickImport() throws IOException {
        changeToImportScreen();
    }

    /**
     * Method to call change to graph screen method in MasterController when the graph button
     * is clicked
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void clickGraph() throws IOException {
        changeToGraphScreen();
    }

    /**
     * Takes all the filter fields from the view. Creates a filter object which is set with these fields.
     * Applies the filter which queries the database and returns an ArrayList with the required data.
     * Populates the table and map with this filtered data.
     */
    public void clickApply() {
        Filter filter = new Filter();
        filter.setStart(startDate.getValue()); // need to fix this shit with a try exception
        filter.setEnd(endDate.getValue());

        HashSet<String> selectedTypes = new HashSet<>();
        for (MenuItem item: crimeTypeDropdown.getItems()) {
            CheckBox box = (CheckBox) ((CustomMenuItem)item).getContent();
            if (box.isSelected()) {
                selectedTypes.add(box.getText());
            }
        }
        filter.setTypes(selectedTypes);

        HashSet<String> selectedLocations = new HashSet<>();
        for (MenuItem item: locationDropdown.getItems()) {
            CheckBox box = (CheckBox) ((CustomMenuItem)item).getContent();
            if (box.isSelected()) {
                selectedLocations.add(box.getText());
            }
        }
        filter.setLocations(selectedLocations);

        if (yesArrest.isSelected()) {
            filter.setArrest(true);
        } else if (noArrest.isSelected()) {
            filter.setArrest(false);
        }

        if (yesDomestic.isSelected()) {
            filter.setDomestic(true);
        } else if (noDomestic.isSelected()) {
            filter.setDomestic(false);
        }
        //If anyArrest or anyDomestic is selected that field is left as null

        filter.setBeats(beatSearch.getText());
        filter.setWards(wardSearch.getText());

        dataFilter = filter; //added to allow map to use data filter

        try {
            crimeData = filter.applyFilter();
        } catch (SQLException e) {
            (new Alert(Alert.AlertType.ERROR, "SQL Query Failed: " + e)).show();
            e.printStackTrace();
        }

        tableView.setItems(FXCollections.observableArrayList(crimeData));

        DynamicMapService.removeMarkers();
        try {
            DynamicMapService.loadFilteredMarkers(dataFilter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called when add crime button is clicked, sets isNewCrime value to true to tell edit
     * controller that the crime is a new crime to be added and not an edited crime. Launches edit
     * screen to prompt user for fields
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */

    public void clickAdd() throws IOException {
        Crime crime = new Crime();
        EditController.isNewCrime = true;
        launchEditScreen(crime);
    }

    /**
     * Method called when edit crime button is clicked, sets isNewCrime variable to false to tell edit
     * controller that the crime is an existing crime being edited. Checks to see if a crime has been
     * selected, if it has the launch edit screen is launched.
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void clickEdit() throws IOException {
        Crime crime = tableView.getSelectionModel().getSelectedItem();
        if (crime != null) {
            EditController.isNewCrime = false;
            launchEditScreen(crime);
        }
    }

    /**
     * Method called when delete crime button is clicked, checks to see if crime has been selected, if a
     * crime has been selected. It calls a method in SQLiteDatabase to delete that specific crime from the
     * database, if this is successful, the crime is deleted from the dataset and the table is updated. If
     * it is not, an error message is shown to the user and the crime is not deleted.
     */
    public void clickDelete() {
        Crime selectedCrime = tableView.getSelectionModel().getSelectedItem();
        if (selectedCrime != null) {
            try {
                SQLiteDatabase.deleteFromTable(ImportController.currentTable, selectedCrime);
                crimeData.remove(selectedCrime);
                tableView.setItems(FXCollections.observableArrayList(crimeData));
            } catch (SQLException e) {
                (new Alert(Alert.AlertType.ERROR, "Crime could not be deleted from database")).show();
            }
        }
    }

    /**
     * Method to build two sets of different primary and location descriptions respectively, these
     * sets are then used to populate the filter dropdown menu for their corresponding fields.
     */
    private void buildFilterSets() {
        for (Crime crime : unfilteredData) {
            types.add(crime.getPrimaryDescription());
            locations.add(crime.getLocationDescription());
        }
    }

    /**
     * Method to populate the filter dropdown menus for location and primary descriptions of the
     * crime dataset. The sets are looped through and each item is added to the dropdown menu.
     */
    private void buildDropdowns() {
        for (String type: types.stream().sorted().collect(Collectors.toList())) {
            if (type.equals("")) {
                type = "NO TYPE GIVEN";
            }
            CheckBox newBox = new CheckBox(type);
            CustomMenuItem newItem = new CustomMenuItem(newBox);
            newItem.setHideOnClick(false);

            crimeTypeDropdown.getItems().add(newItem);
        }

        for (String location: locations.stream().sorted().collect(Collectors.toList())) {
            if (location.equals("")) {
                location = "NO LOCATION GIVEN";
            }
            CheckBox newBox = new CheckBox(location);
            CustomMenuItem newItem = new CustomMenuItem(newBox);
            newItem.setHideOnClick(false);
            locationDropdown.getItems().add(newItem);
        }
    }

    /**
     * Resets the filter fields on the view. Resets the table and map to the raw dataset that was originally
     * imported and selected for viewing.
     */
    public void clickReset() {
        startDate.setValue(null);
        endDate.setValue(null);
        for (MenuItem item: crimeTypeDropdown.getItems()) {
            CheckBox box = (CheckBox) ((CustomMenuItem)item).getContent();
            box.setSelected(false);
        }
        for (MenuItem item: locationDropdown.getItems()) {
            CheckBox box = (CheckBox) ((CustomMenuItem)item).getContent();
            box.setSelected(false);
        }
        beatSearch.setText("");
        wardSearch.setText("");
        anyArrest.setSelected(true);
        anyDomestic.setSelected(true);

        MasterController.populateCrimeArray(ImportController.currentTable);
        tableView.setItems(FXCollections.observableArrayList(crimeData));

        dataFilter = null;
        DynamicMapService.removeMarkers();
        DynamicMapService.loadSearchMarkers();
    }

    /**
     * Method to launch a window to rank the current data by most densely populated areas of crime
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void clickRankArea() throws IOException {
        launchCrimeRankScreen("Block", RankService.rankedAreaList(crimeData));

    }

    /**
     * Method to launch a window to rank the current data by different types of crime
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void clickRankCrimeType() throws IOException {
        launchCrimeRankScreen("Type", RankService.rankedTypeList(crimeData));

    }

    /**
     * Method to launch a window to rank the current data by the time a crime occurred.
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void clickRankTime() throws IOException {
        launchCrimeRankScreen("Hour of Day", RankService.rankedTimeList(crimeData, TimeType.HOUR_OF_DAY));
    }

    /**
     * Hides view components only needed for map and makes visible the components only needed for the table.
     * Hides the map and makes the table visible.
     */
    public void switchToTable() {
        mapPane.setVisible(false);
        tableView.setVisible(true);
        viewCrime.setVisible(true);
        addCrime.setVisible(true);
        deleteCrime.setVisible(true);
        editCrime.setVisible(true);
        reloadMapButton.setVisible(false);
        dataButton.setStyle("-fx-background-color: #575757");
        mapButton.setStyle("-fx-background-color: #3d3d3d");
    }

    /**
     * Hides the view components only needed for table and makes visible the components only needed for the map.
     * Hides the table and makes the map visible.
     */
    public void switchToMap() {
        mapPane.setVisible(true);
        tableView.setVisible(false);
        viewCrime.setVisible(false);
        addCrime.setVisible(false);
        editCrime.setVisible(false);
        deleteCrime.setVisible(false);
        reloadMapButton.setVisible(true);
        dataButton.setStyle("-fx-background-color: #3d3d3d");
        mapButton.setStyle("-fx-background-color: #575757");
    }

    /**
     * Reloads the dynamic map by calling its initialize method. Re adds the WebView object as a child of the pane
     * which decides its size.
     */
    public void reloadMap() {
        DynamicMapService.initializeDynamicMap();
        WebView mapView = DynamicMapService.getMapView();
        mapView.setMaxSize(mapPane.getPrefWidth(), mapPane.getPrefHeight());
        mapPane.getChildren().add(mapView);
    }

}


