package com.moviedb.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.Dialogs;

import com.moviedb.dao.ActorDao;
import com.moviedb.dao.GenreDao;
import com.moviedb.dao.MovieDao;
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
    private MovieDao movieDao;
    private ActorDao actorDao;
    private GenreDao genreDao;
    private Movie currentMovie;  // The movie currently chosen from the list

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
    private Button resetButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ListView<Movie> moviesListView;
    @FXML
    private ListView<Actor> actorsListView;
    @FXML
    private ListView<Genre> genresListView;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField releaseYearTextField;
    @FXML
    private TextField directorTextField;


    /**
     * Gets the current list of genres.
     *
     * @return New list of genres from the genres list view.
     */
    public List<Genre> getGenreList() {
        return new ArrayList<>(genresListView.getItems());
    }


    /**
     * Gets the current list of actors.
     *
     * @return New list of actors from the actors list view.
     */
    protected List<Actor> getActorList() {
        return new ArrayList<>(actorsListView.getItems());
    }


    /**
     * Sets the new list of genres.
     *
     * @param selectedGenres List of selected genres to update the genre list with.
     */
    protected void setGenreList(List<Genre> selectedGenres) {
        genresListView.getItems().clear();
        genresListView.getItems().addAll(selectedGenres);
    }


    /**
     * Sets the name of the database to be used by the controller.
     *
     * @param dbName The name of the database to set.
     */
    protected void setDatabaseName(String dbName) {
        this.databaseName = dbName;
    }


    /**
     * Sets the new list of actors.
     * @param actors List of actors to update the actors list with.
     */
    public void setActorList(List<Actor> actors) {
        actorsListView.getItems().setAll(actors);
    }


    /**
     * Initializes the controller.
     * Sets up mouse click event listeners for the movies, actors, and genres lists.
     * The listener for the movies list updates the focus variables and fills in the details of the selected movie.
     * The listeners for the actors and genres lists simply update the respective focus variables.
     *
     * @param url The URL used for resolving relative paths, can be null.
     * @param rb  The resource bundle for localizing objects, can be null.
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

        // Listener for release year field
        releaseYearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !InputValidator.isValidReleaseYear(newValue)) {
                releaseYearTextField.setStyle("-fx-control-inner-background: #ffdddd;");
            } else {
                releaseYearTextField.setStyle("-fx-control-inner-background: white;");
            }
        });
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
            movieDao.create(new Movie("paska", 2023, "testi2",
                    Arrays.asList(2, 3), Arrays.asList(1, 6)));

            moviesListView.getItems().addAll(movieDao.readAll());  // Add all movies to the list view
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }

        setupSearchTextFieldListener();
        setupComboBoxListener();
    }



    /**
     * Sets up a listener for the search text field. When the user types in the search field,
     * the movies list view is updated to show only the movies that match the search criteria.
     * If the search field is cleared, the original list of movies is reloaded from the database.
     */
    private void setupSearchTextFieldListener() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2) {
                List<Movie> filteredMovies = searchMoviesStartingWith(newValue);
                updateMovieListView(filteredMovies);
            } else if (newValue.isEmpty()) {
                // Fetch the original list of movies from db once the search field is empty
                try {
                    List<Movie> allMovies = movieDao.readAll();
                    updateMovieListView(allMovies);
                } catch (SQLException e) {
                    System.out.println("SQLState: " + e.getSQLState());
                    System.out.println("Error Code: " + e.getErrorCode());
                    System.out.println("Message: " + e.getMessage());
                }
            }
        });
    }


    /**
     * Sets up a listener for the search combo box. When a new criterion is selected,
     * the movies list view is sorted according to the selected criterion.
     */
    private void setupComboBoxListener() {
        searchComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            sortMoviesBy(String.valueOf(newValue));
        });
    }


    /**
     * Sorts the movies in the movies list view based on the specified criterion.
     *
     * @param criterion The criterion to sort by.
     */
    private void sortMoviesBy(String criterion) {
        ObservableList<Movie> sortedList;
        switch (criterion.toLowerCase()) {
            case "title":
                sortedList = moviesListView.getItems().sorted(Comparator.comparing(Movie::getTitle));
                break;
            case "release year":
                sortedList = moviesListView.getItems().sorted(Comparator.comparing(Movie::getReleaseYear));
                break;
            case "director":
                sortedList = moviesListView.getItems().sorted(Comparator.comparing(Movie::getDirector));
                break;
            default:
                return;
        }
        moviesListView.setItems(sortedList);
    }


    /**
     * Filters the movies in the movies list view to only show movies that start with the specified prefix.
     * The search is case-insensitive.
     *
     * @param prefix The prefix to filter the movies by.
     * @return       A list of movies that start with the specified prefix.
     */
    private List<Movie> searchMoviesStartingWith(String prefix) {
        String lowerCasePrefix = prefix.toLowerCase();

        return moviesListView.getItems().stream()
                .filter(movie -> movie.getTitle().toLowerCase().startsWith(lowerCasePrefix))
                .collect(Collectors.toList());
    }


    /**
     * Updates the movies list view with the provided list of movies.
     * Clears the current list and adds all the movies from the provided list.
     *
     * @param movies The list of movies to display in the movies list view.
     */
    private void updateMovieListView(List<Movie> movies) {
        moviesListView.getItems().clear();
        moviesListView.getItems().addAll(movies);
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
     * Handles the 'Save' button click event. Updates the movie details in the database,
     * based on the data collected from the user interface. It either updates an existing movie or
     * creates a new one, depending on whether 'currentMovie' is null or not. After updating or creating
     * the movie, it updates the corresponding entry in the ListView to reflect the changes.
     *
     * @param event The ActionEvent triggered by the 'Save' button click.
     */
    @FXML
    void handleSave(ActionEvent event) {
        System.out.println("Save button clicked!");

        try {
            if (validateInputs()) {
                Movie updatedMovie = createMovieFromInput();

                // Movie exists
                if (currentMovie != null) {
                    updatedMovie.setId(currentMovie.getId());
                    if (movieDao.update(updatedMovie)) {
                        addOrUpdateMovieInListView(updatedMovie);
                        currentMovie = null;  // Reset the current movie in memory
                        clearFields();
                    }
                } else {  // Movie doesn't exist, create new one
                    int newMovieId = movieDao.create(updatedMovie);
                    updatedMovie.setId(newMovieId);
                    moviesListView.getItems().add(updatedMovie);
                    clearFields();
                }
            }

        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Adds a new movie to the ListView or updates an existing one. Searches for the movie
     * in the ListView based on its ID. If the movie is found, it updates the existing movie entry. If not,
     * it adds the new movie to the ListView.
     *
     * @param movie The movie to be added or updated in the ListView.
     */
    private void addOrUpdateMovieInListView(Movie movie) {
        for (int i = 0; i < moviesListView.getItems().size(); i++) {
            if (moviesListView.getItems().get(i).getId() == movie.getId()) {
                moviesListView.getItems().remove(i);
                moviesListView.getItems().add(i, movie); // Remove and re-add to update
                return;
            }
        }
        moviesListView.getItems().add(movie); // Add new if not found
    }



    /**
     * Handles the 'Reset' button click event
     * Resets the current movie object and clears all input fields.
     *
     * @param event The ActionEvent triggered by the 'Reset' button click.
     */
    @FXML
    public void handleReset(ActionEvent event) {
        currentMovie = null;
        clearFields();
    }


    /**
     * Handles the 'Add' button click event.
     * Determines which entity (movie, actor, or genre) is currently focused based on the
     * focus variables and opens the corresponding dialog.
     *
     * @param event The ActionEvent triggered by the 'Add' button click.
     */
    @FXML
    void handleAdd(ActionEvent event) {
        System.out.println("Add button clicked. Movie focused: " + isMovieListFocused +
                ", Actor focused: " + isActorListFocused + ", Genres focused: " + isGenresListFocused);

        if (isActorListFocused) {
            openActorDialog(event);
        } else if (isGenresListFocused) {
            openGenreDialog(event);
        }
    }


    /**
     * Handles the deletion of movies, actors, or genres based on the focused list.
     *
     * @param event The ActionEvent triggered by the delete action.
     */
    @FXML
    void handleDelete(ActionEvent event) {
        if (isMovieListFocused) {
            Movie selectedMovie = moviesListView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                removeObjectFromList(moviesListView, selectedMovie);  // Removes also from db
            }
        } else if (isActorListFocused) {
            Actor selectedActor = actorsListView.getSelectionModel().getSelectedItem();
            if (selectedActor != null) {
                removeObjectFromList(actorsListView, selectedActor);
            }
        } else if (isGenresListFocused) {
            Genre selectedGenre = genresListView.getSelectionModel().getSelectedItem();
            if (selectedGenre != null) {
                removeObjectFromList(genresListView, selectedGenre);
            }
        }
    }


    /**
     * Opens the actor dialog.
     *
     * @param event The ActionEvent that triggered the opening of the actor dialog.
     */
    private void openActorDialog(ActionEvent event) {
        openDialog("/views/ActorDialogView.fxml", "New Actor Details", event);
    }


    /**
     * Opens the genre dialog.
     *
     * @param event The ActionEvent that triggered the opening of the genre dialog.
     */
    private void openGenreDialog(ActionEvent event) {
        openDialog("/views/GenreDialogView.fxml", "Choose the Genres", event);
    }


    /**
     * Opens a dialog window based on the provided FXML file path and dialog title.
     * Initializes the dialog with the necessary controller and sets up the stage and scene.
     *
     * @param fxmlPath    The path to the FXML file for the dialog view.
     * @param dialogTitle The title of the dialog window.
     * @param event       The ActionEvent that triggered the dialog opening.
     */
    private void openDialog(String fxmlPath, String dialogTitle, ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage ownerStage = (Stage) source.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ActorDialogViewController) {
                ((ActorDialogViewController) controller).initializeController(this, connection);
            } else if (controller instanceof GenreDialogViewController) {
                ((GenreDialogViewController) controller).initializeController(this, connection);
            }

            Scene scene = new Scene(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle(dialogTitle);
            dialogStage.setScene(scene);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(ownerStage);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Removes an object from the provided ListView.
     * If the object is a Movie, it is also deleted from the database.
     *
     * @param listView The ListView from which the object is to be removed.
     * @param object   The object to remove, which can be of any type.
     * @param <T>      The type of objects contained in the ListView.
     */
    private <T> void removeObjectFromList(ListView<T> listView, T object) {
        if (object instanceof Movie movie) {
            deleteMovie(movie);
            listView.getItems().remove(movie);
        } else {
            listView.getItems().remove(object);
        }
    }


    /**
     * Deletes a movie from the database and updates the UI accordingly.
     *
     * @param selectedMovie The movie to be deleted from the database.
     */
    private void deleteMovie(Movie selectedMovie) {
        try {
            movieDao.delete(selectedMovie.getId());
            currentMovie = null;  // Reset the currently selected movie
            clearFields();
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    /**
     * Fills in the movie details into the relevant fields and ListView components.
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
        searchTextField.clear();
        titleTextField.clear();
        releaseYearTextField.clear();
        directorTextField.clear();
        actorsListView.getItems().clear();
        genresListView.getItems().clear();
    }


    /**
     * Validates input fields for movie details and ensures that
     * both actors and genres lists are not empty.
     *
     * @return boolean indicating whether inputs are valid or not.
     */
    private boolean validateInputs() {
        String updatedMovieTitle = titleTextField.getText();
        String releaseYearText = releaseYearTextField.getText();
        String updatedDirector = directorTextField.getText();

        boolean isValid = !updatedMovieTitle.isEmpty() &&
                InputValidator.isValidReleaseYear(releaseYearText) &&
                InputValidator.isValidDirectorName(updatedDirector) &&
                !actorsListView.getItems().isEmpty() &&
                !genresListView.getItems().isEmpty();

        if (!isValid) {
            Dialogs.showMessageDialog("Invalid input data!");
        }

        return isValid;
    }


    /**
     * Creates a new Movie object from user inputs, extracting the movie details
     * and lists of actor and genre IDs.
     *
     * @return Movie object created from input fields.
     */
    private Movie createMovieFromInput() {
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

        return new Movie(updatedMovieTitle, updatedReleaseYear, updatedDirector,
                updatedActorIds, updatedGenreIds);
    }
}
