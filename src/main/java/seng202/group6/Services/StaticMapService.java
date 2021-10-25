package seng202.group6.Services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.ImageResult;
import com.google.maps.StaticMapsApi;
import com.google.maps.StaticMapsRequest;
import com.google.maps.StaticMapsRequest.Markers;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.Size;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javafx.embed.swing.SwingFXUtils;
import seng202.group6.Controllers.MasterController;
import seng202.group6.DynamicMapSRC.ApiKey;
import seng202.group6.Models.Crime;

/**
 * Service for creating a static map image with markers.
 */
public class StaticMapService {

    /**
     * Provides implementation for getting a static map image from the Google Maps API using an HTTP request.
     * @param centre The Address for the centre of the map. Can provide "lat,lng" as well as "address".
     * @param crimeData An ArrayList which holds the Crime objects whose locations are to be displayed as markers on
     *                  the map.
     * @param width Width of the static map image.
     * @param height Height of the static map image.
     * @return returns a javafx.scene.image.Image object or null if an error occurs.
     */
    public static Image getStaticMap(String centre, ArrayList<Crime> crimeData, int width, int height) {
        try {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(ApiKey.getApiKey()).build();
            GeocodingResult[] geocodingResult = geoCodeAddress(context, centre);
            LatLng centreLatLng = geocodingResult[0].geometry.location;
            Size size = new Size(width, height);
            StaticMapsRequest request = StaticMapsApi.newRequest(context, size);
            int zoom = highestLevelZoom(geocodingResult);
            request.center(centreLatLng).zoom(zoom).maptype(StaticMapsRequest.StaticMapType.roadmap);
            Markers markers = addStaticMapMarkers(crimeData, centreLatLng, zoom);
            request.markers(markers);
            ImageResult result = request.await();
            Image image = arrayToImage(result.imageData);
            context.shutdown();
            return image;
        } catch (Exception e) {
            System.out.println("Error in StaticMapService.getStaticMap: " + e);
            return null;
        }
    }

    /**
     * Gets geocoding data from the Google Maps api.
     * @param context GeoApiContext object.
     * @param address Address to get geocoding information about. Can also provide "lat,lng" String.
     * @return GeocodingResult[] object with data received from the Google Maps api.
     * @throws IOException Caused by requesting from Google Maps api.
     * @throws InterruptedException Caused by requesting from Google Maps api.
     * @throws ApiException Caused by requesting from Google Maps api.
     */
    public static GeocodingResult[] geoCodeAddress(GeoApiContext context, String address) throws IOException, InterruptedException, ApiException {
        GeocodingResult[] results =  GeocodingApi.geocode(context,
                address).await();
        return results;
    }

    /**
     * Finds the appropriate zoom level for the static map based on how much information the GeocodingResult[] object
     * has. The Google Maps api provides more information depending on how detailed the address given to it was.
     * @param results Result from geocoding an address.
     * @return An int which describes the zoom level for the static map.
     */
    public static int highestLevelZoom(GeocodingResult[] results) {
        AddressComponent[] addressComponents = results[0].addressComponents;
        AddressComponentType[] addressComponentTypes = addressComponents[0].types;
        int zoomLevel = 10;
        boolean isFoundZoom = false;
        int i = 0;
        while (!isFoundZoom && i < addressComponents.length) {
            switch (addressComponents[i].types[0].name()) {
                case "STREET_NUMBER":
                    ;
                case "ROUTE":
                    zoomLevel = 15;
                    isFoundZoom = true;
                    break;
                case "NEIGHBORHOOD":
                    zoomLevel = 14;
                    isFoundZoom = true;
                case "POLITICAL":
                    for (AddressComponentType type : addressComponentTypes) {
                        if (type.name() == "SUBLOCALITY"){
                            zoomLevel = 14;
                            isFoundZoom = true;
                        }
                    }
                    break;
                case "LOCALITY":
                    zoomLevel = 11;
                    isFoundZoom = true;
                    break;
                case "ADMINISTRATIVE_AREA_LEVEL_1":
                    zoomLevel = 8;
                    isFoundZoom = true;
                    break;
                case "COUNTRY":
                    zoomLevel = 5;
                    isFoundZoom = true;
                    break;
            }
            i++;
        }
        if (isFoundZoom != true) {
            zoomLevel = 10;
        }
        return zoomLevel;
    }

    /**
     * Creates a Markers object which holds the markers to be added to the static map. Crime locations are added in a
     * 6 km radius around the centreLocation. Marker size is based on the zoom level.
     * @param crimeData ArrayList which stores the crime objects whose locations will be added to request.
     * @param centreLocation Location of the centre of the static map.
     * @param zoom Zoom level of the static map.
     * @return Markers Object which holds the locations and size of markers to be added to static map.
     */
    public static Markers addStaticMapMarkers(ArrayList<Crime> crimeData, LatLng centreLocation, int zoom) {
        Markers markers = new Markers();
        int numMarkers = 0;
        for (int i =0; i < crimeData.size(); i++) {
            try {
                Double crimeLat = crimeData.get(i).getLatitude();
                Double crimeLng = crimeData.get(i).getLongitude();
                LatLng crimeLocation = new LatLng(crimeLat, crimeLng);
                Double distanceToCentre = getDistanceFromCentre(crimeData.get(i), centreLocation);
                if ((distanceToCentre < 6) && (numMarkers < 500)) {
                    markers.addLocation(crimeLocation);
                    numMarkers++;
                }
            } catch (java.lang.NumberFormatException e) {
            }
        }
        switch (zoom) {
            case 5:;
            case 8:;
            case 10:;
            case 11:
                markers.size(Markers.MarkersSize.tiny);
                break;
            case 15:
                markers.size(Markers.MarkersSize.normal);
                break;
            default:
                markers.size(Markers.MarkersSize.small);
        }
        return markers;
    }

    /**
     * Converts a bytearray into a javafx.scene.image.Image.
     * @param byte_array bytearray with image data.
     * @return Javafx Image.
     */
    public static Image arrayToImage(byte[] byte_array) {
        Image image = null;
        try {
            ByteArrayInputStream input_stream = new ByteArrayInputStream(byte_array);
            BufferedImage final_buffered_image = ImageIO.read(input_stream);
            image = SwingFXUtils.toFXImage(final_buffered_image, null);

        } catch (Exception e) {
            System.out.println("Error in producing map image: " + e);
        }

        return image;
    }

    /**
     * Gets the distance between a crime location and centre location.
     * @param otherCrime Crime to find distance to.
     * @param centre Location to find distance from.
     * @return Distance between a crime and location.
     */
    public static Double getDistanceFromCentre(Crime otherCrime, LatLng centre){
        Double x = (centre.lng - otherCrime.getLongitude()) * 111;
        Double y = (centre.lat - otherCrime.getLatitude()) * 111;
        return Math.hypot(x, y);
    }


}
