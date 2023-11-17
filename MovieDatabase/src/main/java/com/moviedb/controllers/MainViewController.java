package com.moviedb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.ListChooser;

import com.moviedb.dao.MovieDao;
import com.moviedb.dao.GenreDao;
import com.moviedb.dao.ActorDao;
import com.moviedb.models.Actor;
import com.moviedb.models.Genre;
import com.moviedb.models.Movie;

public class MainViewController {

    private String databaseName;
    private Connection connection;
    private MovieDao movieDao;
    private ActorDao actorDao;
    private GenreDao genreDao;

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBoxChooser<?> searchComboBox;


    @FXML
    private Button saveButton;

    @FXML
    public Button addButton;

    @FXML
    public Button deleteButton;

    @FXML
    private ListChooser<String> moviesListChooser;

    @FXML
    public ListChooser actorsListChooser;

    @FXML
    public ListChooser genresListChooser;

    @FXML
    public TextField titleTextField;

    @FXML
    public TextField releaseYearTextField;

    @FXML
    public TextField directorTextField;

    @FXML
    void handleSave(ActionEvent event) {

    }

    @FXML
    void handleAdd(ActionEvent event) {

    }

    @FXML
    void handleDelete(ActionEvent event) {

    }


    /**
     * Initializes the database connection and sets up the DAOs to allow
     * the application to interact with the database during runtime.
     * Adds a SelectionListener to Movie list.
     *
     * @param dbName The name of the database file (without the file extension) to connect to.
     */
    public void initializeDatabase(String dbName) {
        // Establish connections and load Movies
        this.databaseName = dbName;
        openDatabaseConnection();
        this.movieDao = new MovieDao(connection);
        this.actorDao = new ActorDao(connection);
        this.genreDao = new GenreDao(connection);
    }


    public void initializeUI() {
        loadMoviesFromDB();

        // Add a selection listener to the moviesListChooser to fill the information of selected movie
        moviesListChooser.addSelectionListener(event -> {
            String selectedMovieTitle = moviesListChooser.getSelectedObject();
            if (selectedMovieTitle != null) {
                try {
                    Movie selectedMovie = movieDao.getMovieByTitle(selectedMovieTitle);
                    fillMovieDetails(selectedMovie);
                } catch (SQLException e) {
                    System.out.println("SQLState: " + e.getSQLState());
                    System.out.println("Error Code: " + e.getErrorCode());
                    System.out.println("Message: " + e.getMessage());
                }
            }
        });
    }


    /**
     * Opens a connection to the correct SQLite database based on the database name.
     */
    private void openDatabaseConnection() {
        String dbPath = "src/main/java/com/moviedb/database/" + databaseName + ".db";

        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Loads all movies from the database and populates the moviesListChooser component with their titles.
     * Fetches a list of all Movie objects using the movieDao's readAll method,
     * clears the existing list in moviesListChooser, and then adds the title of each movie to the list.
     */
    private void loadMoviesFromDB() {
        try {
            List<Movie> movies = movieDao.readAll();
            if (movies.isEmpty()) {
                moviesListChooser.clear();
                return;
            }
            moviesListChooser.clear();
            for (Movie movie : movies) {
                moviesListChooser.add(movie.getTitle());
            }
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Fills in the movie details into the relevant fields and ListChooser components.
     * Updates the title, release year, and director text fields with the selected movie's details.
     * Also clears and repopulates the actorsListChooser and genresListChooser with the actors and genres
     * associated with the selected movie.
     *
     * @param selectedMovie The Movie object whose details are to be displayed.
     */
    private void fillMovieDetails(Movie selectedMovie) {
        titleTextField.setText(selectedMovie.getTitle());
        releaseYearTextField.setText(String.valueOf(selectedMovie.getReleaseYear()));  // Format to String
        directorTextField.setText(selectedMovie.getDirector());

        // Clear the current lists
        actorsListChooser.clear();
        genresListChooser.clear();

        // Add all actors to its ListChooser component
        for (Integer actorId : selectedMovie.getActorIds()) {
            Optional<Actor> actorOptional = actorDao.getActorById(actorId);
            actorOptional.ifPresent(actor -> actorsListChooser.add(actor));  // Add actor to list if it exists
        }

        // Add all genres to its ListChooser component
        for (Integer genreId : selectedMovie.getGenreIds()) {
            Optional<Genre> genreOptional = genreDao.getGenreById(genreId);
            genreOptional.ifPresent(genre -> genresListChooser.add(genre));  // Add genre to list if it exists
        }
    }
}
