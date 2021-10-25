package seng202.group6.DynamicMapSRC;

import seng202.group6.Controllers.ImportController;
import seng202.group6.Controllers.MasterController;
import seng202.group6.Models.Crime;
import seng202.group6.Services.DynamicMapService;
import seng202.group6.Services.SQLiteDatabase;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object which is passed into the dynamic maps html window object. Allows javascript functions to call
 * the methods of this object.
 */
public class JavascriptMethods {

    /**
     * Called by the javascript function onPlaceChanged. Called when an address is searched in the dyunamic map.
     * Removes all markers currently on the dynamic map then adds markers in the radius around the searched address.
     */
    public void addMarkersToMap(){
        DynamicMapService.removeMarkers();
        DynamicMapService.removeLocationMarker();
        DynamicMapService.addLocationMarker();
        DynamicMapService.loadSearchMarkers();
    }

    /**
     * Called by the javascript function viewMoreInfo. Called when the view more info button within a markers
     * infowindow is clicked. Takes a Crimes case number queries the database for this crime. Opens the
     * view more info window populated with the returned Crime from the database.
     * @param crimeID The case number of the crime to view more information about.
     */
    public void viewInfo(String crimeID) {
        String sql = "SELECT * FROM " + ImportController.currentTable + " WHERE case_id = '" + crimeID +"';";
        try {
            ArrayList<Crime> crimes = SQLiteDatabase.convertResultSet(SQLiteDatabase.executeQuery(sql));
            MasterController.launchViewScreen(crimes.get(0));
        } catch (SQLException | IOException e) {
            System.out.println("SQL Exception in JavascriptMethods.viewInfo: " + e);
            e.printStackTrace();
        }

    }
}
