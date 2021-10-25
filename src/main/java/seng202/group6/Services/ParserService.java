package seng202.group6.Services;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.opencsv.exceptions.CsvValidationException;
import seng202.group6.Models.Crime;

import java.io.*;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class ParserService {
    /**
     * Gets the data from the given file and converts it into an arraylist.
     * @param file A file object in CSV form, containing a list of crimes
     * @throws IOException If the file cannot be read
     * @throws CsvValidationException If the CSV is formatted incorrectly
     */
    public static int[] csvToDatabase(File file, String tableName) throws IOException, CsvValidationException, SQLException {
        CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withSkipLines(1).build();
        int duplicatedCounter = 0;
        int invalidCounter = 0;
        while(reader.peek() != null) {
            String[] fields = reader.readNext();


            try {
                Crime crime = buildCrimeFromFields(fields);
                SQLiteDatabase.insertIntoTable(tableName, crime);   //Populates "Crimes" table in database
            } catch (SQLException e) {
                duplicatedCounter++;
            } catch (NumberFormatException  | DateTimeException | StringIndexOutOfBoundsException| ArrayIndexOutOfBoundsException e) {
                invalidCounter++;
            }
        }
        SQLiteDatabase.endTransaction();
        reader.close();
        return new int[] {duplicatedCounter, invalidCounter};
    }

    /**
     * Using methods in SQLDatabase, gets the data from a table and saves it as a CSV
     * @param file A file object (either empty or to be overwritten)
     * @param tableName The name of the table to read from
     * @throws IOException When writing the file fails
     * @throws SQLException When reading the database fails
     */
    public static void databaseToCSV(File file, String tableName) throws IOException, SQLException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));
        String[] header = {"CASE#", "DATE OF OCCURRENCE", "BLOCK", "IUCR", "PRIMARY DESCRIPTION", "SECONDARY DESCRIPTION",
                "LOCATION DESCRIPTION", "ARREST", "DOMESTIC", "BEAT", "WARD", "FBI CD", "X COORDINATE", "Y COORDINATE",
                "LATITUDE", "LONGITUDE", "LOCATION"};

        writer.writeNext(header);

        ArrayList<Crime> crimes = SQLiteDatabase.convertResultSet(SQLiteDatabase.selectAllFromTable(tableName));

        ArrayList<String[]> strings = new ArrayList<>(crimes.size());
        for (Crime crime: crimes) {
            strings.add(buildFieldsFromCrime(crime));
        }

        writer.writeAll(strings);

        writer.close();







    }

    /**
     * Creates a new crime object based on the data in fields
     * @param fields A String[] with the data from the CSV
     * @return The crime
     */
    public static Crime buildCrimeFromFields(String[] fields) {
        return new Crime (
                fields[0], //Case Num
                parseDateString(fields[1]), //Date
                fields[2], //Block
                fields[3], //IUCR
                fields[4], //Primary Description
                fields[5], //Secondary Description
                fields[7].equals("Y"), //Arrest
                fields[8].equals("Y"), //Domestic
                fields[9].equals("") ? -1 : Integer.parseInt(fields[9]), //Beat
                fields[10].equals("") ? -1 : Integer.parseInt(fields[10]), //Ward
                fields[11], //FBI
                fields[6],  //Location Description
                fields[14].equals("") ? 0.0 : Double.parseDouble(fields[14]), //Latitude
                fields[15].equals("") ? 0.0 : Double.parseDouble(fields[15]) //Longitude
        );
    }

    /**
     * Creates a list of strings from a crime object, ready for writing to the CSV
     * @param crime The crime object to be converted
     * @return A list of strings with fields in the same order as the supplied CSVs
     */
    public static String[] buildFieldsFromCrime(Crime crime) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
        String date = crime.getDate().format(format).toUpperCase(); //AM and PM should be uppercase as in original data

        String arrest = crime.getArrest() ? "Y" : "N";
        String domestic = crime.getDomestic() ? "Y" : "N";
        String lat = String.valueOf(crime.getLatitude());
        String lon = String.valueOf(crime.getLongitude());

        return new String[] {crime.getCaseNumber(),
                date,
                crime.getBlock(),
                crime.getIucr(),
                crime.getPrimaryDescription(),
                crime.getSecondaryDescription(),
                crime.getLocationDescription(),
                arrest,
                domestic,
                String.valueOf(crime.getBeat()),
                String.valueOf(crime.getWard()),
                crime.getFBI(),
                "", // X-coordinate and Y-coo
                "", //
                lat,
                lon,
                String.format("(%s, %s)", lat, lon)
        };

    }

    /**
     * This method takes a string representing a date of format MM/DD/YYYY Hour/Minute/Second AM/PM and converts it
     * into an object of type LocalDateTime to represent it
     * @param date A string representing a date of format MM/DD/YYYY Hour/Minute/Second AM/PM
     * @return Returns a LocalDateTime object representing the date-time passed to it as a string.
     */
    public static LocalDateTime parseDateString(String date) {
        int second = Integer.parseInt(date.substring(17, 19));
        int minute = Integer.parseInt(date.substring(14, 16));
        int hour = Integer.parseInt(date.substring(11, 13));
        int day = Integer.parseInt(date.substring(3, 5));
        int month = Integer.parseInt(date.substring(0, 2));
        int year = Integer.parseInt(date.substring(6, 10));
        if (date.endsWith("PM") && hour != 12){
            hour += 12;
        }else if(date.endsWith("AM") && hour == 12){
            hour = 0;
        }

        return LocalDateTime.of(year, month, day, hour, minute, second);
    }
}
