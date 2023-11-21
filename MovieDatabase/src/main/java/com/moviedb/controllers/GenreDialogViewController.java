package com.moviedb.controllers;

import com.moviedb.dao.GenreDao;
import com.moviedb.dao.MovieDao;
import com.moviedb.models.Actor;
import com.moviedb.models.Genre;
import com.moviedb.models.Movie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import net.synedra.validatorfx.Check;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


public class GenreDialogViewController {

    private Connection connection;
    private GenreDao genreDao;

    private MovieDao movieDao;

    private Movie currentMovie;

    @FXML
    private Button cancelButton;

    @FXML
    private ListView<CheckBox> listView;

    @FXML
    private Button okButton;


    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    @FXML
    void handleOK(ActionEvent event) {
        List<Integer> selectedGenreIds = new ArrayList<>();

        // Collect all the results in an array and set it to the current movie
        for (CheckBox cb : listView.getItems()) {
            if (cb.isSelected()) {
                Genre genre = (Genre) cb.getUserData();
                selectedGenreIds.add(genre.getId());
            }
        }
        currentMovie.setGenreIds(selectedGenreIds);

        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }


    public void setConnection(Connection connection) {
        this.connection = connection;
        this.movieDao = new MovieDao(connection);
        this.genreDao = new GenreDao(connection);
        loadGenres();
    }

    public void setCurrentMovie(Movie currentMovie) {
        this.currentMovie = currentMovie;
    }

    private void loadGenres() {
        List<Genre> genres = genreDao.readAll();
        List<Integer> selectedGenreIds = currentMovie.getGenreIds();  // Currently selected genres

        for (Genre genre : genres) {
            CheckBox cb = new CheckBox(genre.getName());
            cb.setUserData(genre);

            // Set the check box selected if the genre is added to the movie already
            if (selectedGenreIds.contains(genre.getId())) {
                cb.setSelected(true);
            }

            listView.getItems().add(cb);
        }
    }

}
