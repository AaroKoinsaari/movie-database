package com.moviedb.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.control.textfield.TextFields;

import fi.jyu.mit.fxgui.Dialogs;

import com.moviedb.dao.ActorDao;
import com.moviedb.models.Actor;


/**
 * Controller class that is responsible for handling the UI logic related to adding and managing actors
 * associated with a movie. It includes functionality to add new actors, check for existing actors,
 * and confirm or cancel actions within the dialog.
 */
public class ActorDialogViewController implements Initializable {

    private ActorDao actorDao;
    private MainViewController mainViewController;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ListView<Actor> listView;

    @FXML
    private Label alertLabel;  // Displays alerts and notifications to the user


    /**
     * Initializes the controller with necessary references and sets up the ListView with actors.
     *
     * @param mainViewController The main controller of the application.
     * @param connection         The database connection for actor data retrieval.
     */
    protected void initializeController(MainViewController mainViewController, Connection connection) {
        this.mainViewController = mainViewController;
        this.actorDao = new ActorDao(connection);

        // Update listView using observableList
        ObservableList<Actor> actors = FXCollections.observableArrayList(mainViewController.getActorList());
        listView.setItems(actors);
    }


    /**
     * Initializes the controller with auto-completion for the actor name TextField.
     * Auto-completion suggestions are generated based on the user's input, with a minimum
     * of 3 characters required.
     * Selected suggestions are automatically filled into the TextField.
     *
     * @param url The URL used for resolving relative paths, can be null.
     * @param rb  The resource bundle for localizing objects, can be null.
     */
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


    /**
     * Triggers when the 'Add' button is clicked. Validates the actor name from the text field,
     * showing a dialog if empty. Searches for the actor in the database; adds them to the list
     * if not present or creates a new actor if they do not exist.
     *
     * @param event The ActionEvent triggered by the 'Add' button click.
     */
    @FXML
    void handleAdd(ActionEvent event) {
        alertLabel.setVisible(false);  // Reset the alert label

        String actorName = nameTextField.getText().trim();
        if (actorName.isEmpty()) {
            Dialogs.showMessageDialog("Actor name can not be empty");
            return;
        }

        Optional<Actor> actorOpt = actorDao.getActorByName(actorName);

        // Check if the actor already exists and if it's already on the list
        if (actorOpt.isPresent()) {
            addActorToListIfNotExists(actorOpt.get());
        } else {
            createAndAddNewActor(actorName);
        }
    }


    /**
     * Handles the 'OK' button action. Associates actors from the list with the current movie
     * by checking if they already are associated or not.
     *
     * @param event The ActionEvent triggered by the 'OK' button click.
     */
    @FXML
    void handleOK(ActionEvent event) {
        mainViewController.setActorList(listView.getItems());
        closeStage();
    }


    /**
     * Closes the dialog window when the 'Cancel' button is clicked.
     *
     * @param event The ActionEvent triggered by the 'Cancel' button click.
     */
    @FXML
    void handleCancel(ActionEvent event) {
        closeStage();
    }


    /**
     * Adds the specified actor to the list if they are not already present.
     *
     * @param actor The Actor object to be added to the list.
     */
    private void addActorToListIfNotExists(Actor actor) {
        if (!isActorOnTheList(actor)) {
            listView.getItems().add(actor);
            nameTextField.clear();
        } else {
            alertLabel.setVisible(true);
            // Dialogs.showMessageDialog("Actor '" + actor.getName() + "' is already in the list.");
            nameTextField.clear();
        }
    }


    /**
     * Creates a new actor with the given name and adds them to the list.
     * Handles the creation process in the database and updates the list view upon success.
     *
     * @param actorName The name of the actor to be created and added.
     */
    private void createAndAddNewActor(String actorName) {
        try {
            Actor newActor = new Actor(actorName);
            int newActorId = actorDao.create(newActor);
            Optional<Actor> createdActorOpt = actorDao.read(newActorId);
            createdActorOpt.ifPresent(listView.getItems()::add);

            nameTextField.clear();
            alertLabel.setText("New actor created!");
            alertLabel.setVisible(true);
        } catch (Exception e) {
            Dialogs.showMessageDialog("Error creating actor: " + e.getMessage());
            nameTextField.clear();
        }
    }


    /**
     * Checks if the specified actor is already in the list by comparing
     * the actor's ID with the IDs of actors in the list.
     *
     * @param actor The Actor object to check in the list.
     * @return true if the actor is in the list, false otherwise.
     */
    private boolean isActorOnTheList(Actor actor) {
        return listView.getItems().stream()
                .anyMatch(existingActor -> existingActor.getId() == actor.getId());
    }


    /**
     * Closes the current stage/window.
     */
    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
