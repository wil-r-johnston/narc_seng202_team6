package seng202.group6.Services;

import com.google.maps.model.LatLng;
import seng202.group6.Controllers.ImportController;
import seng202.group6.Models.Crime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The filter class is used to filter an arraylist of crimes by specific characteristics.
 * You first create a filter object, then call the setter methods to set the parameters of the filter.
 * Finally, applyFilter can be called, querying the database and returning a string.
 */

public class Filter {

    private LocalDate start;
    private LocalDate end;
    private HashSet<String> types = new HashSet<>();
    private HashSet<String> locations = new HashSet<>();
    private Boolean arrest;
    private Boolean domestic;
    private Set<Integer> beats = new HashSet<>();
    private Set<Integer> wards = new HashSet<>();
    private LatLng centre;


    /**
     * Builds an SQL query from the different filter settings
     * @return The query as a String
     */
    public String queryBuilder() {
        String tableName = ImportController.currentTable;
        String statement = "SELECT * FROM " + tableName + " ";
        statement += "WHERE ";

        boolean anyFilter = false;

        if (start != null) {
            anyFilter = true;
            statement += "occurrence_date >= '" + LocalDateTime.of(start, LocalTime.MIDNIGHT) + "' ";
            statement += "AND ";
        }

        if (end != null) {
            anyFilter = true;
            statement += "occurrence_date < '" + LocalDateTime.of(end, LocalTime.MIDNIGHT) + "' ";
            statement += "AND ";
        }

        if (!types.isEmpty()) {
            anyFilter = true;
            statement += "primary_description IN ('" + String.join("', '", types) + "') ";
            statement += "AND ";
        }

        if (!locations.isEmpty()) {
            anyFilter = true;
            statement += "location IN ('" + String.join("', '", locations) + "') ";
            statement += "AND ";
        }

        if (arrest != null) {
            anyFilter = true;
            statement += "arrest = " + arrest + " ";
            statement += "AND ";
        }

        if (domestic != null) {
            anyFilter = true;
            statement += "domestic = " + domestic + " ";
            statement += "AND ";
        }

        if (!beats.isEmpty()) {
            anyFilter = true;
            statement += "beat IN (" + String.join(", ", beats.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet()))
                    + ") ";
            statement += "AND ";
        }

        if (!wards.isEmpty()) {
            anyFilter = true;
            statement += "ward IN (" + String.join(", ", wards.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet()))
                    + ") ";
            statement += "AND ";
        }
        if (centre != null){
            anyFilter = true;
            double latRight = centre.lat - 0.0036;
            double latLeft = centre.lat + 0.0031;
            double lngUp = centre.lng - 0.0062;
            double lngDown = centre.lng + 0.0062;
            statement += "(latitude BETWEEN "+latRight+" AND "
                    +latLeft+") AND (longitude BETWEEN "+lngUp +" AND "+lngDown+")";
        } else {
            statement = statement.substring(0, statement.length() - 5); //Get rid of trailing AND
        }

        if (!anyFilter) { //Makes sure blank filters work
            statement = "SELECT * FROM " + tableName;
        }

        statement += ";";

        return statement;

    }

    /**
     * Gets an arraylist of crimes from the database based on the properties set
     * @return An arraylist of Crimes
     */
    public ArrayList<Crime> applyFilter() throws SQLException {
        ResultSet result = SQLiteDatabase.executeQuery(queryBuilder());
        return SQLiteDatabase.convertResultSet(result);
    }

    /**
     * Sets the filter to filter from a specific start date
     * @param start The start date
     */
    public void setStart(LocalDate start) {
        this.start = start;
    }

    /**
     * Sets the filter to filter until a specific end date
     * @param end The end date
     */
    public void setEnd(LocalDate end) {
        if (end != null) {
            this.end = end.plusDays(1);
        }

    }

    /**
     * Sets the filter to filter for crimes with their primary description (type) in types
     * @param types A set of Strings of primary descriptions.
     */
    public void setTypes(HashSet<String> types) {
        this.types = types;
    }

    /**
     * Sets the filter to filter for crimes with their location description in locations
     * @param locations A set of Strings of location descriptions
     */
    public void setLocations(HashSet<String> locations) {
        this.locations = locations;
    }

    /**
     * Sets the filter to filter for crimes with the given value of arrest
     * @param arrest boolean value for if there was an arrest
     */
    public void setArrest(boolean arrest) {
        this.arrest = arrest;
    }

    /**
     * Sets the filter to filter for crimes with the given value of domestic
     * @param domestic boolean value for if the crime was domestic
     */
    public void setDomestic(boolean domestic) {
        this.domestic = domestic;
    }

    public void setCentre(LatLng centre) {
        this.centre = centre;
    }

    /**
     * Sets the filter to filter for crimes with the given beats
     * @param beatString A comma separated list of beats
     */
    public void setBeats(String beatString) {
        if (!beatString.isEmpty()) {
            for (String beat : beatString.split(",\\s*")) {
                if (beat.matches("[0-9]+")){
                    beats.add(Integer.parseInt(beat));}
            }
        }
    }

    /**
     * Sets the filter to filter for crimes with the given wards
     * @param wardString A comma separated list of wards
     */
    public void setWards(String wardString) {
        if (!wardString.isEmpty()) {
            for (String ward : wardString.split(",\\s*")) {
                if (ward.matches("[0-9]+")) {
                    wards.add(Integer.parseInt(ward));
                }
            }
        }

    }


}





