package seng202.group6.Services;

import com.google.maps.model.LatLng;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.group6.Controllers.MasterController;
import seng202.group6.DynamicMapSRC.JavascriptMethods;
import seng202.group6.Models.Crime;
import seng202.group6.Models.DynamicMapMarker;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service for creating and manipulating a dynamic Google Map
 * which is loaded by a javafx.scene.web.WebEngine Object.
 */
public class DynamicMapService {
    /**
     * Holds a javafx WebView object. Allows map stay loaded when switching scenes.
     */
    private static WebView mapView;
    /**
     * Holds the window object of the html of the dynamic map. Used for adding JavascriptMethods object to
     * dynamic maps javascipt.
     */
    private static JSObject window;
    /**
     * Holds the JavascriptMethods object passed into the dynamic maps window object.
     */
    private static JavascriptMethods javascript = new JavascriptMethods();

    /**
     * Initializes the javascript dynamic map. Initializes mapView to hold the EmbedMaps.html file.
     * Initializes window to hold the dynamic maps window object. Adds a JavascriptMethods object
     * to the window of the dynamic maps html naming the member "javascriptMethods". This allows the
     * javascript loaded in mapView to call methods in the JavascriptMethods object.
     */
    public static void initializeDynamicMap() {
        mapView = new WebView();
        WebEngine webEngine = mapView.getEngine();
        window = (JSObject) mapView.getEngine().executeScript("window");
        window.setMember("javascriptMethods", javascript);
        try {
            webEngine.load(DynamicMapService.class.getResource("/seng202/group6/HTML/EmbedMaps.html").toExternalForm());
        } catch (Exception e) {
            System.out.println("Error in initializeDynamicMap: " + e);
        }
    }

    /**
     * Gets the WebView object mapView
     * @return mapView a WebView object
     */
    public static WebView getMapView() {
        return mapView;
    }

    /**
     * Takes an ArrayList of Crime objects and calls the javascript function addMarker in EmbedMapsScript.js
     * on each crime. Passes the latitude, longitude, primary description, date and case number of each crime through.
     * addMarker adds a marker on the dynamic map at the Crime object coordinates, with an on click set to show an
     * infowindow with the Crime objects primary description, date and case number.
     * @param crimes An ArrayList of Crime object which are to be added as markers to the dynamic map.
     */
    public static void loadMarkers(ArrayList<Crime> crimes) {
        for (int i = 0; i < crimes.size(); i++) {
            String script = "addMarker([";
            DynamicMapMarker marker = new DynamicMapMarker(crimes.get(i).getLatitude(), crimes.get(i).getLongitude(),
                    crimes.get(i).getPrimaryDescription(), crimes.get(i).getReadableDate(), crimes.get(i).getCaseNumber());
            script += marker.toString();
            script += ",])";
            mapView.getEngine().executeScript(script);
        }
    }

    /**
     * Calls the getLocation method in the dynamic map javascript. This javascript function
     * returns the coordinates of the variable returnLocation. returnLocation holds the coordinates
     * of the most recently searched for address in the map.
     * @return Latitude and Longitude of the most recently searched for address in the dynamic map.
     */
    public static LatLng getMapCentre() {
        JSObject location = (JSObject) mapView.getEngine().executeScript("getLocation()");
        Double lat = (Double) location.getMember("lat");
        Double lng = (Double) location.getMember("lng");
        LatLng centre = new LatLng();
        centre.lat = lat;
        centre.lng = lng;
        return centre;
    }

    /**
     * Calls the script to remove all the Crime markers on the dynamic map.
     */
    public static void removeMarkers() {
        mapView.getEngine().executeScript("removeMarkers()");
    }

    /**
     * Calls the script to remove the marker for the searched address on the dynamic map
     */
    public static void removeLocationMarker() {
        DynamicMapService.getMapView().getEngine().executeScript("removeLocationMarker()");
    }

    /**
     * Checks whether there is a filter active. Calls loadFilteredMarkers if there is or
     * load markers if there isn't.
     */
    public static void loadSearchMarkers() {
        Filter filter = MasterController.getFilter();
        if (filter != null) {
            try {
                loadFilteredMarkers(filter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Filter newFilter = new Filter();
                newFilter.setCentre(getMapCentre());
                ArrayList<Crime> crimes = newFilter.applyFilter();
                loadMarkers(crimes);
            } catch (Exception e) {
                System.out.println("Error in DynamicMapService.loadSearchMarkers: " + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets an ArrayList of Crime objects from applying a filter. Calls loadMarkers on this ArrayList.
     * @param filter Filter object to retrieve the required data from the database.
     * @throws SQLException
     */
    public static void loadFilteredMarkers(Filter filter) throws SQLException {
        filter.setCentre(getMapCentre());
        ArrayList<Crime> crimes = filter.applyFilter();
        loadMarkers(crimes);
    }

    /**
     * Calls the dynamic map script addLocationMarker(). This script adds a marker to the dynamic map at
     * the coordinates of the last searched address on the map.
     */
    public static void addLocationMarker() {
        DynamicMapService.getMapView().getEngine().executeScript("addLocationMarker()");
    }
}