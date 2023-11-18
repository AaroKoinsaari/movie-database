package com.moviedb.controllers;

import com.moviedb.dao.ActorDao;
import com.moviedb.models.Actor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

public class ActorDialogViewController {

    private Connection connection;

    private ActorDao dao;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button okButton;

    @FXML
    private Label statusLabel;

    public void setConnection(Connection connection) {
        this.connection = connection;
        this.dao = new ActorDao(connection);
    }


    @FXML
    void handleOK(ActionEvent event) {
        String actorName = nameTextField.getText();
        try {
            dao.create(new Actor(actorName));
            statusLabel.setText("New actor added!");
            statusLabel.setVisible(true);
        } catch (Exception e) {
            statusLabel.setText("Adding new actor failed: " + e.getMessage());
            statusLabel.setVisible(true);
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
