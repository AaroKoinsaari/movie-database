package com.moviedb.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


/**
 * @author Aaro Koinsaari
 * @version 29.10.2023
 */
public class MovieMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource("/views/LaunchView.fxml"));
            final Pane root = ldr.load();
            //final MoviesGUIController moviesCtrl = (MoviesGUIController)ldr.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Movies");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args Ei käytössä
     */
    public static void main(String[] args) {
        launch(args);
    }
}