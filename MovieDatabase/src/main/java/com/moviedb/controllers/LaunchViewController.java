package com.moviedb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

import fi.jyu.mit.fxgui.*;


public class LaunchViewController {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField databaseNameField;

    @FXML
    private Button okButton;

    @FXML
    void handleCancelButton(ActionEvent event) {
        Dialogs.showMessageDialog("Not working yet");
    }

    @FXML
    public void handleOkButton(ActionEvent event) {
        String dbName = databaseNameField.getText();
        String dbPath = "../database/" + dbName + ".db";

        if (databaseExists(dbPath)) {
            openMainView(dbName);
        } else {
            Dialogs.showQuestionDialog("Database does not exist", "Do you want to create a new database?",
                    "Yes", "No");
        }
    }


    private boolean databaseExists(String dbPath) {
        File dbFile = new File(dbPath);
        return dbFile.exists();
    }


    private void openMainView(String dbName) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            Parent root = loader.load();

            MainViewController controller = loader.getController();
            controller.initializeDatabase(dbName);

            // Create new Scene and set it as current window
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Main View - " + dbName);
            stage.show();

            // Close the "old" window
            Stage currentStage = (Stage) databaseNameField.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Message: " + e.getMessage());
        }
    }
}