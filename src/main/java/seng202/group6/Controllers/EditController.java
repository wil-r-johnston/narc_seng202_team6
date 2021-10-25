package seng202.group6.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng202.group6.Models.Crime;
import seng202.group6.Services.SQLiteDatabase;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

import static seng202.group6.Services.ParserService.parseDateString;

/**
 * Controller class for editing the details of or adding a new crime, associated with
 * editCrimeScreen.fxml. Is a child class of MasterController
 */

public class EditController extends MasterController implements Initializable {


    protected static boolean isNewCrime;

    @FXML
    private TextField caseNumber;

    @FXML
    private DatePicker date;

    @FXML
    private TextField block;

    @FXML
    private TextField IUCR;

    @FXML
    private TextField primaryDescription;

    @FXML
    private TextField secondaryDescription;

    @FXML
    private TextField location;

    @FXML
    private CheckBox arrest;

    @FXML
    private CheckBox domestic;

    @FXML
    private TextField beat;

    @FXML
    private TextField ward;

    @FXML
    private TextField fbiCD;

    @FXML
    private TextField latitude;

    @FXML
    private TextField longitude;

    /**
     * Method to override initialise method from Initializable interface. If the crime associated
     * with the window is a new crime, i.e the crime is being created and added, all fields of a
     * crime are set to empty and the user is able to fill in all fields. If the crime is not a new
     * crime, i.e the crime is being edited, all fields of the crime are set to the current values,
     * and the case number of a crime is set to uneditable as this is not able to be changed by the
     * user.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Crime viewedCrime = MasterController.currentCrime;

        if (!isNewCrime) {
            caseNumber.setText(viewedCrime.getCaseNumber());
            date.setValue(viewedCrime.getDate().toLocalDate());
            block.setText(viewedCrime.getBlock());
            IUCR.setText(viewedCrime.getIucr());
            primaryDescription.setText(viewedCrime.getPrimaryDescription());
            secondaryDescription.setText(viewedCrime.getSecondaryDescription());
            location.setText(viewedCrime.getLocationDescription());
            arrest.setSelected(viewedCrime.isArrest());
            domestic.setSelected(viewedCrime.isDomestic());
            beat.setText(viewedCrime.getBeat() == -1 ? "" : Integer.toString(viewedCrime.getBeat()));
            ward.setText(viewedCrime.getWard() == -1 ? "" : Integer.toString(viewedCrime.getWard()));
            fbiCD.setText(viewedCrime.getFBI());
            latitude.setText(viewedCrime.getLatitude() == -1.0 ? "" : Double.toString(viewedCrime.getLatitude()));
            longitude.setText(viewedCrime.getLongitude() == -1.0 ? "" : Double.toString(viewedCrime.getLongitude()));
        } else {
            caseNumber.setDisable(false);
            caseNumber.setEditable(true);
        }


    }

    /**
     * Method to apply the changes or additions the user has made when the apply button is clicked.
     * First checks if the inputted values for beat and ward are valid, i.e they are integer numbers.
     * If they are not, the user is given an error message and the changes are not added to the
     * database. If they are, all values for the crime are set to the inputted values for each
     * of the respective fields. Then the crime is checked if it is a new or edited crime, if it is
     * a new crime, then the system attempts to add the crime to the database. If it is not a new crime,
     * the system attempts to update the current crime in the database. If an error occurs during either
     * of these processes, the user is shown an error saying the crime was unable to be saved to the
     * database, and the crime is not added or updated in the database.
     * @param event Button click event used to get and close the edit crime window
     * @throws IOException Throws an error if reading from fxml when changing screens fails
     */
    public void clickApply(ActionEvent event) throws IOException {

        Crime editedCrime = new Crime();

        if (caseNumber.getText().equals("") || caseNumber.getText() == null) {
            (new Alert(Alert.AlertType.ERROR, "Case number formatted incorrectly. Needs to be not null")).show();
            return;
        }

        try {
            editedCrime.setDate(date.getValue().atStartOfDay());
        } catch(Exception e) {
            (new Alert(Alert.AlertType.ERROR, "Date formatted incorrectly. dd/mm/yyyy needed")).show();
            return;
        }

        try {
            Integer.parseInt(block.getText().substring(0,3));
        } catch (NumberFormatException e) {
            (new Alert(Alert.AlertType.ERROR, "Block formatted incorrectly. First three characters " +
                    "need to be integers.")).show();
            return;
        } catch (StringIndexOutOfBoundsException e) {
            (new Alert(Alert.AlertType.ERROR, "Block formatted incorrectly. Needs to be longer than 3 characters " +
                    "and the first three characters " +
                    "need to be integers.")).show();
            return;
        }

        try {
            if (beat.getText() == null || beat.getText().equals("")) {
                editedCrime.setBeat(-1);
            } else {
                editedCrime.setBeat(Integer.parseInt(beat.getText()));
            }
        } catch (NumberFormatException e) {
            (new Alert(Alert.AlertType.ERROR, "Beat formatted incorrectly. Needs to be an integer.")).show();
            return;
        }

        try {
            if (ward.getText() == null || ward.getText().equals("")) {
                editedCrime.setWard(-1);
            }  else {
                editedCrime.setWard(Integer.parseInt(ward.getText()));
            }
        } catch (NumberFormatException e) {
            (new Alert(Alert.AlertType.ERROR, "Ward formatted incorrectly. Needs to be an integer.")).show();
            return;
        }

        try {
            if (latitude.getText() == null || latitude.getText().equals("")) {
                editedCrime.setLatitude(-1);
            }  else {
                editedCrime.setLatitude(Double.parseDouble(latitude.getText()));
            }
        } catch (NumberFormatException e){
            (new Alert(Alert.AlertType.ERROR, "Latitude formatted incorrectly. Needs to be a double.")).show();
            return;
        }

        try {
            if (longitude.getText() == null || longitude.getText().equals("")) {
                editedCrime.setLongitude(-1);
            }  else {
                editedCrime.setLongitude(Double.parseDouble(longitude.getText()));
            }
        } catch (NumberFormatException e){
            (new Alert(Alert.AlertType.ERROR, "Longitude formatted incorrectly. Needs to be a double.")).show();
            return;
        }

        currentCrime.setDate(date.getValue().atStartOfDay());
        currentCrime.setCaseNumber(caseNumber.getText());
        currentCrime.setBlock(block.getText());
        currentCrime.setIucr(IUCR.getText());
        currentCrime.setPrimaryDescription(primaryDescription.getText());
        currentCrime.setSecondaryDescription(secondaryDescription.getText());
        currentCrime.setLocationDescription(location.getText());
        currentCrime.setArrest(arrest.isSelected());
        currentCrime.setDomestic(domestic.isSelected());
        currentCrime.setFBI(fbiCD.getText());
        currentCrime.setWard(editedCrime.getWard());
        currentCrime.setBeat(editedCrime.getBeat());
        currentCrime.setLatitude(editedCrime.getLatitude());
        currentCrime.setLongitude(editedCrime.getLongitude());

        try {
            if (isNewCrime) {
                SQLiteDatabase.insertIntoTable(ImportController.currentTable, MasterController.currentCrime);
                crimeData.add(MasterController.currentCrime);
            } else {
                SQLiteDatabase.updateInTable(ImportController.currentTable, currentCrime);
            }

            SQLiteDatabase.endTransaction();

            changeToDataScreen();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (SQLException e) {
            (new Alert(Alert.AlertType.ERROR, "Invalid Crime - could not add to database. This is possibly due to" +
                    "using a pre-existing case number")).show();
            e.printStackTrace();
        }

    }
}
