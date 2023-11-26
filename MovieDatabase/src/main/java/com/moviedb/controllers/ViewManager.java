package com.moviedb.controllers;

import com.moviedb.models.Movie;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class ViewManager {

    Connection connection;
    Movie currentMovie;

    public static void openActorDialog(Movie currentMovie, Connection connection, Stage ownerStage) throws IOException {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource("/views/ActorDialogView.fxml"));
        Parent root = loader.load();

        ActorDialogViewController controller = loader.getController();
        controller.setConnection(connection);  // Pass the current connection
        controller.setCurrentMovie(currentMovie);

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
}
