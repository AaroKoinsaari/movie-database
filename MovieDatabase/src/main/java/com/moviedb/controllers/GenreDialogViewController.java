package com.moviedb.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import fi.jyu.mit.fxgui.Dialogs;

import com.moviedb.dao.ActorDao;
import com.moviedb.dao.GenreDao;
import com.moviedb.models.Genre;


/**
 * Controller class for managing genre selection in a movie context.
 * It displays available genres as CheckBoxes, allowing the user to select or deselect genres.
 */
public class GenreDialogViewController {

    private static final Logger logger = Logger.getLogger(ActorDao.class.getName());

    private GenreDao genreDao;
    private MainViewController mainViewController;  // Reference to the MainViewController to update selected genres.

    @FXML
    private ListView<CheckBox> listView;
    @FXML
    private Button cancelButton;


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
     * Loads and displays genres in the ListView with CheckBoxes.
     */
    private void loadGenres() {
        List<Genre> genres;
        try {
            genres = genreDao.readAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching genres from the database. SQL state: "
                    + e.getSQLState() + " Error code: " + e.getErrorCode() + " Message: " + e.getMessage(), e);
            Dialogs.showMessageDialog("Error fetching the genres from the database");
            return;  // Exit the method as genres cannot be loaded
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
     * Handles the action of the 'OK' button. Iterates through CheckBoxes to collect selected genres,
     * and updates the MainViewController with these selections.
     */
    @FXML
    void handleOK() {
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
     * Closes the dialog window when the 'Cancel' button is clicked without saving changes.
     */
    @FXML
    void handleCancel() {
        closeStage();
    }


    /**
     * Closes the current stage/window.
     */
    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
