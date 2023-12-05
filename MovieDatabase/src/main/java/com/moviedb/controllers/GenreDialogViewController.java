package com.moviedb.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import com.moviedb.dao.GenreDao;
import com.moviedb.dao.MovieDao;
import com.moviedb.models.Genre;
import com.moviedb.models.Movie;


/**
 * Controller class that is responsible for handling the UI logic related to adding and managing genres
 * associated with a movie. It includes functionality for displaying and selecting genres
 * for a movie by listing all available genres as CheckBoxes.
 */
public class GenreDialogViewController {

    private Connection connection;

    private Movie currentMovie;  // The movie currently being edited in the dialog

    private GenreDao genreDao;

    private MainViewController mainViewController;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ListView<CheckBox> listView;


    protected void initializeController(MainViewController mainViewController, Movie currentMovie, Connection connection) {
        this.mainViewController = mainViewController;
        this.currentMovie = currentMovie;
        this.connection = connection;
        this.genreDao = new GenreDao(connection);
        loadGenres();
    }


    /**
     * Handles the 'OK' button action by iterating through the list
     * of CheckBoxes representing genres, and collects the IDs of all selected
     * genres into a list, which are then associated with the current movie.
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

        mainViewController.setSelectedGenres(selectedGenres);

        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }


    /**
     * Closes the dialog window when the 'Cancel' button is clicked.
     *
     * @param event The ActionEvent triggered by the 'Cancel' button click.
     */
    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    /**
     * Populates the ListView with CheckBoxes for each genre.
     * Each CheckBox is labeled with a genre's name and marked as selected if it's already
     * associated with the current movie. The Genre objects are attached as user data to the CheckBoxes.
     */
    private void loadGenres() {
        List<Genre> genres = genreDao.readAll();
        List<Genre> selectedGenres = mainViewController.getSelectedGenres();

        for (Genre genre : genres) {
            CheckBox cb = new CheckBox(genre.getName());
            cb.setUserData(genre);

            // Set the check box selected if the genre is added to the movie already
            if (selectedGenres.contains(genre)) {
                cb.setSelected(true);
            }

            listView.getItems().add(cb);
        }
    }
}
