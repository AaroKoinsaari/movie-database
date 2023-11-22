package com.moviedb.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import fi.jyu.mit.fxgui.ComboBoxChooser;

import com.moviedb.dao.MovieDao;
import com.moviedb.dao.GenreDao;
import com.moviedb.dao.ActorDao;
import com.moviedb.models.Actor;
import com.moviedb.models.Genre;
import com.moviedb.models.Movie;


/**
 * Controller class that is responsible for handling the UI logic related to managing movies
 * It includes all the main functionality to select and update movie details.
 */
public class MainViewController implements Initializable {

    private String databaseName;
    private Connection connection;
    private Movie currentMovie;  // The movie currently chosen from the list
    private MovieDao movieDao;
    private ActorDao actorDao;
    private GenreDao genreDao;

    // Focus variables to determine which list is active
    private boolean isMovieListFocused;
    private boolean isActorListFocused;
    private boolean isGenresListFocused;

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
    private ListView<Movie> moviesListView;
    @FXML
    public ListView<Actor> actorsListView;
    @FXML
    public ListView<Genre> genresListView;
    @FXML
    public TextField titleTextField;
    @FXML
    public TextField releaseYearTextField;
    @FXML
    public TextField directorTextField;


    /**
     * Sets the name of the database to be used by the controller.
     *
     * @param dbName The name of the database to set.
     */
    protected void setDatabaseName(String dbName) {
        this.databaseName = dbName;
    }


    /**
     * Initializes the controller.
     * Sets up mouse click event listeners for the movies, actors, and genres lists.
     * The listener for the movies list updates the focus variables and fills in the details of the selected movie.
     * The listeners for the actors and genres lists simply update the respective focus variables.
     *
     * @param url The URL used for resolving relative paths, can be null.
     * @param rb The resource bundle for localizing objects, can be null.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listener for movies list to fill the information of selected movie when clicked
        moviesListView.setOnMouseClicked(event -> {
            System.out.println("Movies lista aktivoitu");

            // Update the focus variables
            isMovieListFocused = true;
            isActorListFocused = false;
            isGenresListFocused = false;

            Movie selectedMovie = moviesListView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                try {
                    fillMovieDetails(selectedMovie);
                    currentMovie = selectedMovie;
                } catch (Exception e) {
                    System.out.println("Message: " + e.getMessage());
                }
            }
        });

        // Listener for the actors list
        actorsListView.setOnMouseClicked(event -> {
            System.out.println("Actors lista aktivoitu");

            // Update the focus variables
            isActorListFocused = true;
            isGenresListFocused = false;
            isMovieListFocused = false;
        });

        // Listener for the genres list
        genresListView.setOnMouseClicked(event -> {
            System.out.println("Genres lista aktivoitu");

            // Update the focus variables
            isGenresListFocused = true;
            isActorListFocused = false;
            isMovieListFocused = false;
        });
    }


    /**
     * Handles the 'Save' button click event.
     * Updates the selected movie details to the database by collecting the data from UI.
     *
     * @param event The ActionEvent triggered by the 'Save' button click.
     */
    @FXML
    void handleSave(ActionEvent event) {
        System.out.println("Save button clicked!");

        try {
            String updatedMovieTitle = titleTextField.getText();
            int updatedReleaseYear = Integer.parseInt(releaseYearTextField.getText());
            String updatedDirector = directorTextField.getText();

            List<Integer> updatedActorIds = new ArrayList<>();
            for (Actor actor : actorsListView.getItems()) {
                updatedActorIds.add(actor.getId());
            }

            List<Integer> updatedGenreIds = new ArrayList<>();
            for (Genre genre : genresListView.getItems()) {
                updatedGenreIds.add(genre.getId());
            }

            int originalMovieId = currentMovie.getId();

            Movie updatedMovie = new Movie(originalMovieId, updatedMovieTitle, updatedReleaseYear,
                    updatedDirector, updatedActorIds, updatedGenreIds);

            if (movieDao.update(updatedMovie)) {
                currentMovie = updatedMovie;
                clearFields();
                loadMoviesFromDB();
            }

        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Handles the 'Add' button click event.
     * Determines which entity (movie, actor, or genre) is currently focused based on the focus variables.
     * Opens the corresponding dialog to add a new movie, actor, or genre.
     *
     * @param event The ActionEvent triggered by the 'Add' button click.
     */
    @FXML
    void handleAdd(ActionEvent event) {
        System.out.println("Add button clicked. Movie focused: " + isMovieListFocused +
                ", Actor focused: " + isActorListFocused + ", Genres focused: " + isGenresListFocused);

        if (isMovieListFocused) {
            openAddMovieDialog();
        } else if (isActorListFocused) {
            openAddActorDialog();
        } else if (isGenresListFocused) {
            openAddGenreDialog();
        }
    }


    @FXML
    void handleDelete(ActionEvent event) {
        if (isMovieListFocused) {
            Movie selectedMovie = moviesListView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                deleteMovie(selectedMovie);
            }
        } else if (isActorListFocused && currentMovie != null) {
            Actor selectedActor = actorsListView.getSelectionModel().getSelectedItem();
            if (selectedActor != null) {
                deleteActor(currentMovie, selectedActor);
            }
        } else if (isGenresListFocused && currentMovie != null) {
            Genre selectedGenre = genresListView.getSelectionModel().getSelectedItem();
            if (selectedGenre != null) {
                deleteGenre(currentMovie, selectedGenre);
            }
        }
    }

    private void deleteMovie(Movie selectedMovie) {
        try {
            movieDao.delete(selectedMovie.getId());
            clearFields();
            loadMoviesFromDB();
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    private void deleteActor(Movie selectedMovie, Actor selectedActor) {
        try {
            movieDao.removeLinkFromMovie(selectedMovie.getId(), selectedActor.getId(),
                    "movie_actors", "actor_id");
            clearFields();
            loadMoviesFromDB();
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    private void deleteGenre(Movie selectedMovie, Genre selectedGenre) {
        try {
            movieDao.removeLinkFromMovie(selectedMovie.getId(), selectedGenre.getId(),
                    "movie_genres", "genre_id");
            clearFields();
            loadMoviesFromDB();
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Initializes the database connection and sets up the DAOs to allow
     * the application to interact with the database during runtime.
     */
    protected void initializeAndSetupDatabase() {
        if (databaseName == null || databaseName.isEmpty()) {
            return;
        }

        // Establish connections and load Movies
        openDatabaseConnection();
        this.movieDao = new MovieDao(connection);
        this.actorDao = new ActorDao(connection);
        this.genreDao = new GenreDao(connection);

        actorDao.create(new Actor("leo di caprio"));
        actorDao.create(new Actor("robert de niro"));
        actorDao.create(new Actor("robert downey jr."));


        try {
            movieDao.create(new Movie("test", 2023, "testi",
                    Arrays.asList(1, 2), Arrays.asList(2, 4)));
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }

        loadMoviesFromDB();
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
                moviesListView.getItems().clear();
                return;
            }
            moviesListView.getItems().clear();
            for (Movie movie : movies) {
                moviesListView.getItems().add(movie);
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
        actorsListView.getItems().clear();
        genresListView.getItems().clear();

        // Add all actors to its ListChooser component
        for (Integer actorId : selectedMovie.getActorIds()) {
            Optional<Actor> actorOptional = actorDao.getActorById(actorId);
            actorOptional.ifPresent(actor -> actorsListView.getItems().add(actor));  // Add actor to list if it exists
        }

        // Add all genres to its ListChooser component
        for (Integer genreId : selectedMovie.getGenreIds()) {
            Optional<Genre> genreOptional = genreDao.getGenreById(genreId);
            genreOptional.ifPresent(genre -> genresListView.getItems().add(genre));  // Add genre to list if it exists
        }
    }


    /**
     * Clears all input fields and selections in the UI.
     */
    private void clearFields() {
        titleTextField.clear();
        releaseYearTextField.clear();
        directorTextField.clear();
        actorsListView.getItems().clear();
        genresListView.getItems().clear();
    }


    /**
     * Opens the 'Add Actor' dialog.
     * Loads the ActorDialogView FXML, sets up the controller, and displays the dialog in a modal window.
     * Passes the current database connection and selected movie to the dialog controller.
     * After the dialog is closed, updates the movie details with any changes made.
     */
    private void openAddActorDialog() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ActorDialogView.fxml"));
            Parent root = loader.load();

            ActorDialogViewController controller = loader.getController();
            controller.setConnection(this.connection);  // Pass the current connection
            controller.setCurrentMovie(this.currentMovie);

            // Create new scene and stage
            Scene scene = new Scene(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Actor Details");
            dialogStage.setScene(scene);

            // Set the stage as modal
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(actorsListView.getScene().getWindow());
            dialogStage.showAndWait();  // Wait until the user closes the window

            // Update the selected movie by fetching the updated version from DB
            currentMovie = movieDao.read(currentMovie.getId());
            fillMovieDetails(currentMovie);
        } catch (IOException e) {
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Opens the 'Add Genre' dialog.
     * Loads the GenreDialogView FXML, sets up the controller, and displays the dialog in a modal window.
     * Provides the current movie and database connection to the dialog controller.
     * Updates the movie information from the database after the dialog is closed.
     */
    private void openAddGenreDialog() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GenreDialogView.fxml"));
            Parent root = loader.load();

            GenreDialogViewController controller = loader.getController();
            controller.setCurrentMovie(this.currentMovie);
            controller.setConnection(this.connection);  // Pass the current connection

            // Create new scene and stage
            Scene scene = new Scene(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Choose the Genres");
            dialogStage.setScene(scene);

            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(genresListView.getScene().getWindow());
            dialogStage.showAndWait();

            try {
                movieDao.update(currentMovie);
            } catch (SQLException e) {
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("Error Code: " + e.getErrorCode());
                System.out.println("Message: " + e.getMessage());
            }

            fillMovieDetails(currentMovie);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Opens the 'Add Movie' dialog.
     * Loads the MovieDialogView FXML and displays the dialog in a modal window.
     * Updates the movie details in the database after the dialog is closed.
     */
    private void openAddMovieDialog() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MovieDialogView.fxml"));
            Parent root = loader.load();

            // Create new scene and stage
            Scene scene = new Scene(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Movie Details");
            dialogStage.setScene(scene);

            // Set the stage as modal
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(moviesListView.getScene().getWindow());
            dialogStage.showAndWait();  // Wait until the user closes the window

            // Update the current movie by using the update method
            try {
                movieDao.update(currentMovie);
            } catch (SQLException e) {
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("Error Code: " + e.getErrorCode());
                System.out.println("Message: " + e.getMessage());
            }

            fillMovieDetails(currentMovie);
        } catch (IOException e) {
            System.out.println("Message: " + e.getMessage());
        }
    }
}
