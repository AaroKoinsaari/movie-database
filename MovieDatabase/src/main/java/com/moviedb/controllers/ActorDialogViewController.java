package com.moviedb.controllers;

import com.moviedb.dao.ActorDao;
import com.moviedb.models.Actor;
import com.moviedb.models.Movie;
import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ListChooser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

    @FXML
    private ListChooser<Actor> listChooser;


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
            String userInput = input.getUserText();  // Get the text from ISuggestionRequest object
            if (userInput.length() >= 3) {
                try {
                    return actorDao.findActorsByStartingName(userInput);
                } catch (SQLException e) {
                    System.out.println("SQLState: " + e.getSQLState());
                    System.out.println("Error Code: " + e.getErrorCode());
                    System.out.println("Message: " + e.getMessage());
                    return Collections.emptyList();
                }
            } else {
                return Collections.emptyList();
            }
        }).setOnAutoCompleted(event -> {
            // Set the suggestion into the TextField if user chooses it
            String selectedActorName = event.getCompletion();
            nameTextField.setText(selectedActorName);
        });
    }


    @FXML
    public void handleAdd(ActionEvent actionEvent) {
        String actorName = nameTextField.getText().trim();
        Optional<Actor> actorOpt = actorDao.getActorByName(actorName);

        if (actorOpt.isPresent()) {
            // TODO: Tähän tarkistus onko kyseinen näyttelijä jo listassa
            listChooser.add(actorOpt.get());  // Add actor straight to list if exists
        } else {
            try {  // Create new and add it to list with the ID database gives after creating new Actor
                Actor newActor = new Actor(actorName);
                int newActorId = actorDao.create(newActor);
                Optional<Actor> createdActorOpt = actorDao.read(newActorId);
                createdActorOpt.ifPresent(createdActor -> {
                    listChooser.add(createdActor);
                });
            } catch (Exception e) {
                System.out.println("Message: " + e.getMessage());
            }
        }
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
