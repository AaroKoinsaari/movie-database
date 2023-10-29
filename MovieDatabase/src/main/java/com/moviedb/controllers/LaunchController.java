package com.moviedb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import fi.jyu.mit.fxgui.*;

public class LaunchController {

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
    void handleOkButton(ActionEvent event) {
        Dialogs.showMessageDialog("Not working yet");
    }
}