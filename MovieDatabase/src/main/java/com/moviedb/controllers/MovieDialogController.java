package com.moviedb.controllers;

import fi.jyu.mit.fxgui.Dialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import fi.jyu.mit.fxgui.*;

public class MovieDialogController {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField editBudget;

    @FXML
    private TextField editCast;

    @FXML
    private TextField editDirector;

    @FXML
    private TextField editDuration;

    @FXML
    private TextField editRating;

    @FXML
    private TextField editRelYear;

    @FXML
    private TextField editTitle;

    @FXML
    private TextField editWriters;

    @FXML
    private Label labelError;

    @FXML
    private Button okButton;

    @FXML
    void handleCancel(ActionEvent event) {
        Dialogs.showMessageDialog("Not working yet");
    }

    @FXML
    void handleOK(ActionEvent event) {
        Dialogs.showMessageDialog("Not working yet");
    }
}
