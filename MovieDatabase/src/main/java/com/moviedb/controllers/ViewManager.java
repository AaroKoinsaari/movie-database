package com.moviedb.controllers;

import com.moviedb.models.Movie;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class ViewManager {


    /**
     * Opens the 'Add Actor' dialog.
     * Loads the ActorDialogView FXML, sets up the controller, and displays the dialog in a modal window.
     * Passes the current database connection and selected movie to the dialog controller.
     * After the dialog is closed, updates the movie details with any changes made.
     */
    public static void openActorDialog(Movie currentMovie, Connection connection, Stage ownerStage,
                                       MovieDialogViewController movieDialogViewController)
        throws IOException {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("/views/ActorDialogView.fxml"));
        Parent root = loader.load();

        ActorDialogViewController controller = loader.getController();
        controller.setConnection(connection);  // Pass the current connection

        if (currentMovie == null) {
            controller.setMovieDialogViewController(movieDialogViewController);
        } else {
            controller.setCurrentMovie(currentMovie);
        }


        // Create new scene and stage
        Scene scene = new Scene(root);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("New Actor Details");
        dialogStage.setScene(scene);

        // Set the stage as modal
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.showAndWait();  // Wait until the user closes the window
    }


    /**
     * Opens the 'Add Genre' dialog.
     * Loads the GenreDialogView FXML, sets up the controller, and displays the dialog in a modal window.
     * Provides the current movie and database connection to the dialog controller.
     */
    public static void openGenreDialog(Movie currentMovie, Connection connection, Stage ownerStage,
                                       MovieDialogViewController movieDialogViewController, List<Integer> selectedGenres)
        throws IOException {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("/views/GenreDialogView.fxml"));
        Parent root = loader.load();

        GenreDialogViewController controller = loader.getController();
        controller.setConnection(connection);  // Pass the current connection

        if (currentMovie == null) {
            controller.setMovieDialogViewController(movieDialogViewController);
            controller.setTemporarySelectedGenres(selectedGenres);
        } else {
            controller.setCurrentMovie(currentMovie);
        }

        // Create new scene and stage
        Scene scene = new Scene(root);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Choose the Genres");
        dialogStage.setScene(scene);

        // Set the stage as modal
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.showAndWait();  // Wait until the user closes the window
    }


    public static void openMovieDialog(Connection connection, Stage ownerStage)
        throws IOException {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("/views/MovieDialogView.fxml"));
        Parent root = loader.load();

        MovieDialogViewController controller = loader.getController();
        // controller.setCurrentMovie(currentMovie);
        controller.setConnection(connection);  // Pass the current connection

        // Create new scene and stage
        Scene scene = new Scene(root);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add new movie details");
        dialogStage.setScene(scene);

        // Set the stage as modal
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.showAndWait();  // Wait until the user closes the window
    }
}
