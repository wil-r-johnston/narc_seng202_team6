package seng202.group6.Controllers;

import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import seng202.group6.Services.ParserService;
import seng202.group6.Services.SQLiteDatabase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for import screen in user interface, is associated with importScreen.fxml.
 * Is a child class of MasterController
 */

public class ImportController extends MasterController implements Initializable {

    @FXML
    private Button homeButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button dataButton;

    @FXML
    private Button graphButton;

    @FXML
    private Label noDataSelected;

    @FXML
    private ListView tableList;

    @FXML
    private Label uploadSuccess;

    @FXML
    private Label currentTableText;

    private ArrayList<String> tableNames = new ArrayList<>();
    public static String currentTable; //current table that is being viewed

    /**
     * Method to override initialise method from Initializable interface. Sets all buttons
     * to not be traversable so they cannot be clicked by pressing tab + enter or arrow keys
     * + enter while on the application. Sets the message to show user what table they are
     * currently viewing. Attempts to read all table names from the database and populate
     * list of tables in the ListView. If there are no tables in the database, an error is
     * caught and the ListView is left empty.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        homeButton.setFocusTraversable(false);
        mapButton.setFocusTraversable(false);
        dataButton.setFocusTraversable(false);
        graphButton.setFocusTraversable(false);

        currentTableText.setText("You are currently viewing: " + currentTable);

        try {
            ResultSet sqlTables = SQLiteDatabase.getTableNames();
            while (sqlTables.next()) {
                tableNames.add(sqlTables.getString(1));
            }
            tableList.setItems(FXCollections.observableArrayList(tableNames));
            sqlTables.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to get the first table created in the database.
     * @return String of the name of the first table in the database
     * @throws SQLException Error occurs if no tables have been added to the database,
     * this is caught by higher methods.
     */
    public static String getFirstTable() throws SQLException {

        ResultSet tableNames = SQLiteDatabase.getTableNames();
        String toReturn = tableNames.getString(1);
        tableNames.close();
        return toReturn;

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
     * Method to call change to map screen method in MasterController when the map button
     * is clicked
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    
    public void clickMap() throws IOException {
        changeToMapScreen();
    }

    /**
     * Method to call change to data screen method in MasterController when the data button
     * is clicked
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */

    public void clickData() throws IOException {
        changeToDataScreen();
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
     * Method used to upload a file into the system. First creates a new FileChooser instance
     * which prompts the user to select a file from their local computer. If the user does select
     * a valid file to upload, the file is sent to ParserService to read and receives the number of rows
     * in the file that were omitted. The dataset is populated after the file has been read and
     * added to the database, a message is shown to the user showing how many rows were omitted
     * and the current table view is set to the uploaded file
     * @param tableName String input for the name of the table that is in the database.
     * @throws CsvValidationException Throws an error caused when reading the csv file
     * @throws SQLException Throws an error caused when adding data into the database from the file
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void uploadFile(String tableName) throws CsvValidationException, SQLException, IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open crime data file");
        int[] recordsOmitted = {};
        boolean validUpload = true;
        File crimeFile = fileChooser.showOpenDialog(stage);
        if (crimeFile == null) {
            validUpload = false;
        } else {
            //Creating new tables and giving them a name should be done here

            recordsOmitted = ParserService.csvToDatabase(crimeFile, tableName); //TODO: deal with thrown exceptions

            // need to make method to check if file is csv format and if they actually selected a file
            // also need to get checks for correct format in parser
        }

        MasterController.populateCrimeArray(tableName);

        if (validUpload) {
            uploadSuccess.setText("File uploaded successfully. " + recordsOmitted[0] +
                    " records omitted due to database error and " + recordsOmitted[1] +
                    " records omitted due to invalid fields.");
            uploadSuccess.setVisible(true);
            currentTable = tableName;
            currentTableText.setText("You are currently viewing: " + currentTable);
        }
    }

    /**
     * Method called when create new table button is clicked. User is prompted to enter
     * a name for the table to be created. If the name is valid, the table is created
     * in the database and the uploadFile method is called passing in the name of the
     * table.
     */
    public void clickImportNew()  {

        TextInputDialog txtDlg = new TextInputDialog();
        txtDlg.setHeaderText("Please enter the name of the table you want to create");
        txtDlg.setTitle("Create New Table");
        txtDlg.setContentText("Table name:");

        // returns String optional
        Optional<String> tableName = txtDlg.showAndWait();
        boolean validName = true;

        if (tableName.isPresent()) {
            if (tableName.get().equals("")) {
                (new Alert(Alert.AlertType.ERROR, "Invalid table name: Cannot be empty")).show();
                validName = false;
            } else if (tableName.get().contains(" ")) {
                (new Alert(Alert.AlertType.ERROR, "Invalid table name: Cannot contain spaces")).show();
                validName = false;
            } else if (!Character.isLetter(tableName.get().charAt(0))) {
                (new Alert(Alert.AlertType.ERROR, "Invalid table name: Must start with an alphabetical letter")).show();
                validName = false;
            }
            if (validName) {
                try {
                    SQLiteDatabase.createTable(tableName.get());
                    uploadFile(tableName.get());
                    tableNames.add(tableName.get());
                    tableList.setItems(FXCollections.observableArrayList(tableNames));
                } catch (SQLException e) {
                    (new Alert(Alert.AlertType.ERROR, "Unable to add to database")).show();
                    e.printStackTrace();
                } catch (CsvValidationException | IOException e) {
                    (new Alert(Alert.AlertType.ERROR, "Unable to read file")).show();
                }
            }
        }

    }

    /**
     * Method called when add to existing table button is clicked. If a table has been
     * selected from the list view, uploadFile method is called passing the name of the
     * table from the selection. Otherwise, an error message is shown to the user prompting
     * them to select a table.
     */
    public void clickAddData()  {

        String tableName = (String) tableList.getSelectionModel().getSelectedItem();
        if (tableName != null) {
            try {
                uploadFile(tableName);
            } catch (SQLException e) {
                (new Alert(Alert.AlertType.ERROR, "Unable to add to database")).show();
            } catch (CsvValidationException | IOException e) {
                (new Alert(Alert.AlertType.ERROR, "Unable to read file")).show();
            }
        } else {
            noDataSelected.setVisible(true);
        }
    }

    /**
     * Method called when delete data table button is clicked. If a table has been selected
     * from the ListView. The system attempts to drop the table from the database, if this fails,
     * an error message is shown to the user. If this is successful, system attempts to set the
     * table to view to the first table created in the database, if this is unsuccessful, there
     * are no data tables in the database and so there is no data to view, so the data is set to
     * be empty.
     */
    public void clickDeleteTable() {

        String tableName = (String) tableList.getSelectionModel().getSelectedItem();

        if (tableName != null) {

            try {

                SQLiteDatabase.dropTable(tableName);
                tableNames.remove(tableName);
                tableList.setItems(FXCollections.observableArrayList(tableNames));

                try {
                    currentTable = getFirstTable();
                    MasterController.populateCrimeArray(currentTable);
                    currentTableText.setText("You are currently viewing: " + getFirstTable());
                } catch (SQLException e) {
                    currentTableText.setText("You are currently viewing: No table selected");
                    crimeData = new ArrayList<>(); //if there is no more tables in database, sets data to empty list
                }

            } catch (SQLException e){
                (new Alert(Alert.AlertType.ERROR, "Unable to delete from database")).show();
                e.printStackTrace();
            }


        } else {
            noDataSelected.setVisible(true);
        }

    }

    /**
     * Method called when view this data button is clicked. If a table has been selected
     * from the ListView, the table selected is shown in the data table and the map screen,
     * and the current table viewing message is updated. If there is no table selected, the
     * user is prompted to select a table and nothing changes.
     */
    public void clickChangeData() {
        String tableName = (String) tableList.getSelectionModel().getSelectedItem();
        if (tableName != null) {
            MasterController.populateCrimeArray(tableName);
            currentTable = tableName;
            currentTableText.setText("You are currently viewing: " + currentTable);
        } else {
            noDataSelected.setVisible(true);
        }
    }

    /**
     * Method called when export button is clicked. Opens a file save dialog to save the currently
     * selected table as a CSV. If no table is selected, shows the prompt to select the table.
     */
    public void clickExport() {

        String tableName = (String) tableList.getSelectionModel().getSelectedItem();

        if (tableName != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
            fileChooser.setInitialFileName(tableName + ".csv");
            File saveFile = fileChooser.showSaveDialog(stage);


            try {
                ParserService.databaseToCSV(saveFile, tableName);
            } catch (IOException e) {
                (new Alert(Alert.AlertType.ERROR, "Error saving file")).show();
            } catch (SQLException e) {
                (new Alert(Alert.AlertType.ERROR, "Error reading database")).show();
            }
        } else {
            noDataSelected.setVisible(true);
        }


    }





}
