package seng202.group6.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import seng202.group6.Models.Crime;
import seng202.group6.Services.StaticMapService;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller class for viewing detailed view of specific crime, associated with
 * viewCrimeScreen.fxml. Is a child class of MasterController
 */

public class ViewController extends MasterController implements Initializable {

    @FXML
    private Text caseNumber;

    @FXML
    private Text date;

    @FXML
    private Text block;

    @FXML
    private Text IUCR;

    @FXML
    private Text primaryDescription;

    @FXML
    private Text secondaryDescription;

    @FXML
    private Text location;

    @FXML
    private Text arrest;

    @FXML
    private Text domestic;

    @FXML
    private Text beat;

    @FXML
    private Text ward;

    @FXML
    private Text fbiCD;

    @FXML
    private Text latitude;

    @FXML
    private Text longitude;

    @FXML
    private ImageView imageView;

    /**
     * Method to initialize view scene, overrides initialize method from Initializable
     * interface. Sets all the specific text fields to their correlating values in the
     * specific crime given. Specific crime is taken from crimeToView in MasterController
     * class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Crime viewedCrime = MasterController.currentCrime;
        String centre = viewedCrime.getLatitude() + "," + viewedCrime.getLongitude();
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        crimes.add(viewedCrime);

        caseNumber.setText(viewedCrime.getCaseNumber());
        date.setText(viewedCrime.getReadableDate());
        block.setText(viewedCrime.getBlock());
        IUCR.setText(viewedCrime.getIucr());
        primaryDescription.setText(viewedCrime.getPrimaryDescription());
        secondaryDescription.setText(viewedCrime.getSecondaryDescription());
        location.setText(viewedCrime.getLocationDescription());
        arrest.setText(viewedCrime.isArrest() == true ? "YES" : "NO");
        domestic.setText(viewedCrime.isArrest() == true ? "YES" : "NO");
        beat.setText(viewedCrime.getReadableBeat());
        ward.setText(viewedCrime.getReadableWard());
        fbiCD.setText(viewedCrime.getFBI());
        latitude.setText(viewedCrime.getReadableLatitude());
        longitude.setText(viewedCrime.getReadableLongitude());
        if (!viewedCrime.getReadableDate().equals("NOT GIVEN") &&
                !viewedCrime.getReadableLatitude().equals("NOT GIVEN")) {
            imageView.setImage(StaticMapService.getStaticMap(centre, crimes, 261, 182));
        }
    }



}
