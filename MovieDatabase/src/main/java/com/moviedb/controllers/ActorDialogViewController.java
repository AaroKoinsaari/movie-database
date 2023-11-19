package com.moviedb.controllers;

import com.moviedb.dao.ActorDao;
import com.moviedb.models.Actor;
import com.moviedb.models.Movie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.control.textfield.AutoCompletionBinding;

public class ActorDialogViewController implements Initializable {

    private Connection connection;

    private Movie currentMovie;

    private ActorDao actorDao;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;


    public void setConnection(Connection connection) {
        this.connection = connection;
        this.actorDao = new ActorDao(connection);
    }

    public void setCurrentMovie(Movie currentMovie) {
        this.currentMovie = currentMovie;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TextFields.bindAutoCompletion(nameTextField, input -> {
            String userInput = nameTextField.getText();
            if (userInput.length() >= 3) {
                try {
                    return actorDao.findActorsByStartingName(userInput);
                } catch (SQLException e) {
                    System.out.println("SQLState: " + e.getSQLState());
                    System.out.println("Error Code: " + e.getErrorCode());
                    System.out.println("Message: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                return new ArrayList<>();
            }
        }).setOnAutoCompleted(event -> {
            String selectedActor = event.getCompletion();
            nameTextField.setText(selectedActor);
        });
    }


    @FXML
    public void handleAdd(ActionEvent actionEvent) {

    }


    @FXML
    void handleOK(ActionEvent event) {

    }

    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
