package com.moviedb.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fi.jyu.mit.fxgui.Dialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import com.moviedb.dao.GenreDao;
import com.moviedb.models.Genre;


/**
 * Controller class for managing genre selection in a movie context.
 * It displays available genres as CheckBoxes, allowing the user to select or deselect genres.
 */
public class GenreDialogViewController {

    /** Data Access Object for genre-related operations. */
    private GenreDao genreDao;

    /** Reference to the MainViewController to update selected genres. */
    private MainViewController mainViewController;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    /** ListView for displaying genres as CheckBoxes. */
    @FXML
    private ListView<CheckBox> listView;


    /**
     * Initializes the controller with necessary references and loads the genre list.
     *
     * @param mainViewController The main controller of the application.
     * @param connection         The database connection for genre data retrieval.
     */
    protected void initializeController(MainViewController mainViewController, Connection connection) {
        this.mainViewController = mainViewController;
        this.genreDao = new GenreDao(connection);
        loadGenres();
    }


    /**
     * Handles the action of the 'OK' button. Iterates through CheckBoxes to collect selected genres,
     * and updates the MainViewController with these selections.
     *
     * @param event The ActionEvent triggered by the 'OK' button click.
     */
    @FXML
    void handleOK(ActionEvent event) {
        List<Genre> selectedGenres = new ArrayList<>();

        for (CheckBox cb : listView.getItems()) {
            if (cb.isSelected()) {
                Genre genre = (Genre) cb.getUserData();
                selectedGenres.add(genre);
            }
        }

        mainViewController.setGenreList(selectedGenres);
        closeStage();
    }


    /**
     * Handles the action of the 'Cancel' button by closing the dialog window.
     *
     * @param event The ActionEvent triggered by the 'Cancel' button click.
     */
    @FXML
    void handleCancel(ActionEvent event) {
        closeStage();
    }


    /**
     * Populates the ListView with CheckBoxes, each representing a genre.
     * CheckBoxes are marked as selected based on the current selection in MainViewController.
     */
    private void loadGenres() {
        List<Genre> genres = null;
        try {
            genres = genreDao.readAll();
        } catch (SQLException e) {
            Dialogs.showMessageDialog("Loading genres failed");
        }
        List<Genre> selectedGenres = mainViewController.getGenreList();

        for (Genre genre : genres) {
            CheckBox cb = new CheckBox(genre.getName());
            cb.setUserData(genre);

            if (selectedGenres.contains(genre)) {
                cb.setSelected(true);
            }

            listView.getItems().add(cb);
        }
    }


    /**
     * Closes the current stage/window.
     */
    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
