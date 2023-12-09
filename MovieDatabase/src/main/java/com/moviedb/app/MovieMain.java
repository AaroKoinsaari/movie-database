package com.moviedb.app;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * Main class for the Movie Database application.
 * Serves as the entry point for the application.
 * Initializes the JavaFX environment and sets up the initial view of the application.
 *
 * @author Aaro Koinsaari
 * @version 2023-12-09
 */
public class MovieMain extends Application {

    // Logger for logging errors
    private static final Logger logger = Logger.getLogger(MovieMain.class.getName());


    /**
     * Initializes and shows the launch view of the JavaFX application.
     * It sets up the primary stage with the launch scene and displays it.
     * The 'LaunchView' acts as the starting point of the application, leading to the main application window.
     *
     * @param primaryStage The primary stage for this application, onto which the initial scene is set.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource("/views/LaunchView.fxml"));
            final Pane root = ldr.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Movies");
            primaryStage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error launching the program: " + e.getMessage(), e);
        }
    }


    /**
     * The main entry point for the JavaFX application.
     *
     * @param args Command line arguments passed to the application. Not used in this application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
