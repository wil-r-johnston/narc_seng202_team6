package seng202.group6.Models;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Crime is a model class, used to model each class that is fed into the database.
 * It stores all attributes of the crime it represents
 *
 */

public class Crime {
    private String caseNumber;
    private LocalDateTime date;
    private String block ;
    private String iucr;
    private String primaryDescription;
    private String secondaryDescription;
    private String locationDescription;
    private boolean arrest;
    private boolean domestic;
    private int beat;
    private int ward;
    private String FBI; //FBI crime code
    private double latitude;
    private double longitude;

    /**
     * Empty constructor for making a blank crime object
     */
    public Crime() {}

    /**
     * A constructor for type crime that gets fed a series of strings representing the various variables within it,
     * all parameters are fed in as strings
     * @param case_id A string representing the case number of the crime, two Letters followed by 6 digits of form AB123456
     * @param occurrence_date A LocalDateTime representing the date of the crime
     * @param block A string representing the block the crime occurred at, presented as a zip code and streets with the last two digits anonymized, an
     *              example is 073XX S SOUTH SHORE DR
     * @param iucr A string representing the Illinois Uniform Crime Reporting code
     * @param primary_description A string representing the textual description of the crime i.e. THEFT/ MURDER
     * @param secondary_description A string representing further detail of the nature of the crime, i.e. if the primary is
     *                             THEFT the secondary might be OVER $500
     * @param arrest Whether the perpetrator of the crime was arrested
     * @param domestic Whether the crime was domestic or not
     * @param beat The number of the police district the crime occurred in
     * @param ward The election district the crime occurred in
     * @param fbi A string representing the FBI code of the crime
     * @param location A string giving further detail about the location of the crime
     * @param latitude The latitudinal coordinates of the crime
     * @param longitude The longitudinal coordinates of the crime
     */
     public Crime(String case_id,
                  LocalDateTime occurrence_date,
                  String block,
                  String iucr,
                  String primary_description,
                  String secondary_description,
                  boolean arrest,
                  boolean domestic,
                  int beat,
                  int ward,
                  String fbi,
                  String location,
                  double latitude,
                  double longitude) {

         this.caseNumber = case_id;
         this.date = occurrence_date;
         this.block = block;
         this.iucr = iucr;
         this.primaryDescription = primary_description;
         this.secondaryDescription = secondary_description;
         this.arrest = arrest;
         this.domestic = domestic;
         this.beat = beat;
         this.ward = ward;
         this.FBI = fbi;
         this.locationDescription = location;
         this.latitude = latitude;
         this.longitude = longitude;
     }


    /**
     * An equality checker for a Crime and an object
     * @param other an object to be compared to
     * @return A boolean value of if the objects have the same variables
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Crime){
            Crime otherCrime = (Crime) other;
            return (this.caseNumber.equals(otherCrime.getCaseNumber()) &&
                    this.date.equals(otherCrime.getDate()) &&
                    this.block.equals(otherCrime.getBlock()) &&
                    this.iucr.equals(otherCrime.getIucr()) &&
                    this.primaryDescription.equals(otherCrime.getPrimaryDescription()) &&
                    this.secondaryDescription.equals(otherCrime.getSecondaryDescription()) &&
                    this.arrest == otherCrime.getArrest() &&
                    this.domestic == otherCrime.getDomestic() &&
                    this.beat == otherCrime.getBeat() &&
                    this.ward == otherCrime.getWard() &&
                    this.FBI.equals(otherCrime.getFBI()) &&
                    this.locationDescription.equals(otherCrime.getLocationDescription()) &&
                    this.latitude == otherCrime.getLatitude() &&
                    this.longitude == otherCrime.getLongitude());
        } else {
            return false;
        }
    }

    /**
     * Returns a string containing each variable in the crime object separated by commas
     * Blank variables are formatted as 'NULL' in the string
     * This string representation is the same format necessary for SQL INSERT statements
     * @return String representation of a Crime object
     */
    @Override
    public String toString() {
        return "'" + (this.caseNumber.equals("") ? "NULL" : this.caseNumber) + "', " +
        "'" + (this.date.equals("") ? "NULL" : this.date) + "', " +
        "'" + (this.block.equals("") ? "NULL" : this.block) + "', " +
        "'" + (this.iucr.equals("") ? "NULL" : this.iucr) + "', " +
        "'" + (this.primaryDescription.equals("") ? "NULL" : this.primaryDescription) + "', " +
        "'" + (this.secondaryDescription.equals("") ? "NULL" : this.secondaryDescription) + "', " +
        "'" + (this.locationDescription.equals("") ? "NULL" : this.locationDescription) + "', " +
        this.arrest + ", " +
        this.domestic + ", " +
        (this.beat == (-1) ? "NULL" : this.beat) + ", " +
        (this.ward == (-1) ? "NULL" : this.ward) + ", " +
        "'" + (this.FBI.equals("") ? "NULL" : this.FBI) + "', " +
        (this.latitude == (0.0) ? "NULL" : this.latitude) + ", " +
        (this.longitude == (0.0) ? "NULL" : this.longitude);
    }

    public boolean getArrest() { return this.arrest;}

    public String getReadableArrest() {
        return this.arrest == true ? "YES" : "NO";
    }

    public boolean getDomestic() {return this.domestic;}

    public String getReadableDomestic() {return this.domestic == true ? "YES" : "NO";}

    public String getCaseNumber() {return caseNumber;}

    public LocalDateTime getDate() {return date;}

    public String getReadableDate() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return getDate().format(format);
    }

    public String getFBI() { return FBI; }

    public String getBlock() { return block; }

    public String getIucr() { return iucr; }

    public String getPrimaryDescription() {
        return primaryDescription;
    }

    public String getSecondaryDescription() {
        return secondaryDescription;
    }

    public boolean isArrest() {
        return arrest;
    }

    public boolean isDomestic() {
        return domestic;
    }

    public int getBeat() {
        return beat;
    }

    public String getReadableBeat() {return beat == (-1) ? "NOT GIVEN" : Integer.toString(beat);}

    public int getWard() {
        return ward;
    }

    public String getReadableWard() {return ward == (-1) ? "NOT GIVEN" : Integer.toString(ward);}

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getReadableLocation() {return locationDescription.equals("NULL") || locationDescription.equals("") ? "NO LOCATION GIVEN" : locationDescription;}

    public double getLatitude() {
        return latitude;
    }

    public String getReadableLatitude() {
        return latitude == (-1) ? "NOT GIVEN" : Double.toString(latitude);
    }

    public double getLongitude() {
        return longitude;
    }

    public String getReadableLongitude() {
        return longitude == (-1) ? "NOT GIVEN" : Double.toString(longitude);
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setIucr(String iucr) {
        this.iucr = iucr;
    }

    public void setPrimaryDescription(String primaryDescription) {
        this.primaryDescription = primaryDescription;
    }

    public void setSecondaryDescription(String secondaryDescription) {this.secondaryDescription = secondaryDescription;}

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public void setArrest(boolean arrest) {
        this.arrest = arrest;
    }

    public void setDomestic(boolean domestic) {
        this.domestic = domestic;
    }

    public void setBeat(int beat) {
        this.beat = beat;
    }

    public void setWard(int ward) {
        this.ward = ward;
    }

    public void setFBI(String FBI) {
        this.FBI = FBI;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}
