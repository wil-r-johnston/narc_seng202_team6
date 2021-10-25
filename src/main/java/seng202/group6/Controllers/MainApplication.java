package seng202.group6.Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import seng202.group6.Services.DynamicMapService;
import seng202.group6.Services.SQLiteDatabase;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Main application class for the user interface, launches an initial stage, displaying
 * the home screen of the user interface
 */

public class MainApplication extends Application {

    /**
     * Method to launch the application. Overrides start method in the Application class.
     * Sets all values for the application stage, connects to database, and loads map.
     * Also tries to populate dataset with the first table created in the database. An
     * error occurs if no tables have been created and the data is left as empty.
     * @param primaryStage Main stage to launch application
     * @throws IOException Throws an error if reading from the fxml file fails
     * @throws SQLException Throws an error if system cannot connect to database
     */
    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        Parent homeScreen = FXMLLoader.load(getClass().getResource("homeScreen.fxml"));
        primaryStage.setTitle("NARC");
        primaryStage.setScene(new Scene(homeScreen, 1500, 850));
        primaryStage.setMinHeight(850);
        primaryStage.setMinWidth(1500);
        primaryStage.show();
        MasterController.stage = primaryStage;
        SQLiteDatabase.connectToDatabase(SQLiteDatabase.getJdbcUrl());
        DynamicMapService.initializeDynamicMap();
        try {
            String tableName = ImportController.getFirstTable();
            MasterController.populateCrimeArray(tableName);
            ImportController.currentTable = tableName;
        } catch (SQLException e) {
            ImportController.currentTable = "There is no table to view";
        }
    }

    /**
     * Calls launch method from Application class
     * @param args String[] value of given arguments to main method
     */

    public static void main(String[] args) {
        launch();
    }

}