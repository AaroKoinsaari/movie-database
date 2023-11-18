package com.moviedb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MovieDialogViewController {

    @FXML
    private ListView<String> actorList;

    @FXML
    private Button addActorButton;

    @FXML
    private Button addGenreButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField directorTextField;

    @FXML
    private ListView<String> genresList;

    @FXML
    private Button okButton;

    @FXML
    private TextField releaseYearTextField;

    @FXML
    private TextField titleTextField;

    @FXML
    void handleAddActor(ActionEvent event) {

    }

    @FXML
    void handleAddGenre(ActionEvent event) {

    }

    @FXML
    void handleCancel(ActionEvent event) {

    }

    @FXML
    void handleOK(ActionEvent event) {

    }

}
