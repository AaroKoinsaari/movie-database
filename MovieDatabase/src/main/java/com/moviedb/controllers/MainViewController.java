package com.moviedb.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    /**
     * Represents the focus of the current list in the user interface.
     * Used to determine which list (movies, actors, or genres) is currently active
     * and therefore which actions (add, delete, etc.) should be performed on.
     */
    public enum ListFocus {
        MOVIE, ACTOR, GENRE, NONE
    }

    private String databaseName;
    private Connection connection;
    private MovieDao movieDao;
    private ActorDao actorDao;
    private GenreDao genreDao;
    private Stage primaryStage;  // Static reference to the main window
    private Movie currentMovie;  // The movie currently chosen from the list
    private String currentSortCriterion = "title";  // Default sorting criterion
    private ListFocus currentListFocus = ListFocus.NONE;  // Determines the focused list

    // Logger for logging errors
    private static final Logger logger = Logger.getLogger(MainViewController.class.getName());

    @FXML
    private TextField searchTextField;
    @FXML
    private ComboBoxChooser<String> searchComboBox;
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
    @FXML
    public TextField writerTextField;
    @FXML
    public TextField producerTextField;
    @FXML
    public TextField cinematographyTextField;
    @FXML
    public TextField budgetTextField;
    @FXML
    public TextField countryTextField;
    @FXML
    private Label alertLabel;  // For showing a successful save
    @FXML
    private MenuItem menuAbout;
    @FXML
    private MenuItem menuAddActor;
    @FXML
    private MenuItem menuAddGenre;
    @FXML
    private MenuItem menuDelete;
    @FXML
    private MenuItem menuHelp;
    @FXML
    private MenuItem menuNewMovie;
    @FXML
    private MenuItem menuQuit;
    @FXML
    private MenuItem menuSave;


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
     *
     * @param actors List of actors to update the actors list with.
     */
    public void setActorList(List<Actor> actors) {
        actorsListView.getItems().setAll(actors);
    }


    /**
     * Sets the primary stage of the application.
     *
     * @param primaryStage The main stage to be used throughout the application.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
        // Set action events for the menu items
        menuSave.setOnAction(this::handleSave);
        menuDelete.setOnAction(this::handleDelete);
        menuDelete.setOnAction(this::handleDelete);
        menuNewMovie.setOnAction(this::handleReset);
        menuHelp.setOnAction(e -> {
            Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
            helpAlert.setTitle("Help");
            helpAlert.setHeaderText("Movie Database Application User Guide");
            helpAlert.setContentText(
                    """
                    1. Managing Movies
                       - Create New Movie: Enter details and click 'Save' to start new entry, or clear old entry by clicking 'Reset'.
                       - Edit Movie: Select a movie, modify details, and click 'Save'.
                       - Delete Movie: Select a movie and click 'Delete'.
                
                    2. Managing Actors
                       - Add Actors: Select 'Actors' list, click 'Add', and add actors.
                       - Delete Actor: Select an actor and click 'Delete'.
                
                    3. Managing Genres
                       - Add/Modify Genres: Select 'Genres' list, click 'Add', choose genres, and press 'OK'.
                
                    4. Searching and Filtering
                       - Use the search bar for quick search.
                
                    5. Sorting Movies
                       - Sort movies using the box under the search bar."""
            );


            helpAlert.showAndWait();
        });

        menuAbout.setOnAction(e -> Dialogs.showMessageDialog(
            """
                    Movie Database
                    Version: 1.0

                    Author:
                    Aaro Koinsaari

                    © 2023
                    Aaro Koinsaari"""
        ));

        // Listen and update list focus
        menuAddActor.setOnAction(e -> {
            currentListFocus = ListFocus.ACTOR;
            handleAdd();
        });
        menuAddGenre.setOnAction(e -> {
            currentListFocus = ListFocus.GENRE;
            handleAdd();
        });

        menuQuit.setOnAction(e -> Platform.exit());

        // Listener for movies list to fill the information of selected movie when clicked
        moviesListView.setOnMouseClicked(event -> {
            logger.log(Level.INFO, "Movies list focused");

            currentListFocus = ListFocus.MOVIE;

            Movie selectedMovie = moviesListView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                try {
                    fillMovieDetails(selectedMovie);
                    currentMovie = selectedMovie;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error filling movie details: " + e.getMessage(), e);
                    // Inform the user
                    Dialogs.showMessageDialog("Error occured while filling the movie details to the form!");
                }
            }
        });

        // Listener for the actors list
        actorsListView.setOnMouseClicked(event -> {
            logger.log(Level.INFO, "Actors list focused");
            currentListFocus = ListFocus.ACTOR;
        });

        // Listener for the genres list
        genresListView.setOnMouseClicked(event -> {
            logger.log(Level.INFO, "Genres list focused");
            currentListFocus = ListFocus.GENRE;
        });

        // Listener for release year field
        releaseYearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && InputValidator.isValidReleaseYear(newValue)) {
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

        fillDatabase();

        // Initiate listeners
        setupSearchTextFieldListener();
        setupComboBoxListener();

        try {
            updateMovieListView(movieDao.readAll());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching all the movies from database. SQLState: " + e.getSQLState() +
                    ", Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
            Dialogs.showMessageDialog("Failed to load movie data from database!");  // Inform the user
        }
        sortMoviesBy("title");  // At the starting point movies are sorted by title
    }

    private void fillDatabase() {
        Stream.of(
                new Actor("Robert De Niro"),    // ID 1
                new Actor("Leonardo DiCaprio"), // ID 2
                new Actor("Morgan Freeman"),    // ID 3
                new Actor("Uma Thurman"),       // ID 4
                new Actor("Tom Hanks"),         // ID 5
                new Actor("Keanu Reeves"),      // ID 6
                new Actor("Marlon Brando"),     // ID 7
                new Actor("Brad Pitt"),         // ID 8
                new Actor("Edward Norton"),     // ID 9
                new Actor("Christian Bale"),    // ID 10
                new Actor("Liam Neeson"),       // ID 11
                new Actor("Elijah Wood"),       // ID 12
                new Actor("Mark Hamill"),       // ID 13
                new Actor("Harrison Ford")      // ID 14
        ).forEach(actorDao::create);

        Stream.of(
                new Movie("Inception", 2010, "Christopher Nolan", "Christopher Nolan", "Emma Thomas",
                        "Wally Pfister", 160000000, "USA",
                        List.of(2), Arrays.asList(1, 17, 20)), // Leonardo DiCaprio, Action, Sci-Fi, Thriller
                new Movie("The Shawshank Redemption", 1994, "Frank Darabont", "Stephen King", "Niki Marvin",
                        "Roger Deakins", 25000000, "USA",
                        List.of(3), Arrays.asList(6, 21)), // Morgan Freeman, Drama, Crime
                new Movie("Pulp Fiction", 1994, "Quentin Tarantino", "Quentin Tarantino", "Lawrence Bender",
                        "Andrzej Sekula", 8000000, "USA",
                        Arrays.asList(4, 8), Arrays.asList(6, 15, 21)), // Uma Thurman, Brad Pitt, Crime, Mystery, Drama
                new Movie("Forrest Gump", 1994, "Robert Zemeckis", "Winston Groom", "Wendy Finerman",
                        "Don Burgess", 55000000, "USA",
                        List.of(5), Arrays.asList(6, 21, 16)), // Tom Hanks, Drama, Comedy, Romance
                new Movie("The Matrix", 1999, "Lana Wachowski", "Lilly Wachowski", "Joel Silver",
                        "Bill Pope", 63000000, "USA",
                        List.of(6), Arrays.asList(1, 17, 20)), // Keanu Reeves, Action, Sci-Fi, Thriller
                new Movie("The Godfather", 1972, "Francis Ford Coppola", "Mario Puzo", "Albert S. Ruddy",
                        "Gordon Willis", 6000000, "USA",
                        List.of(7), Arrays.asList(6, 21)), // Marlon Brando, Crime, Drama
                new Movie("Fight Club", 1999, "David Fincher", "Chuck Palahniuk", "Art Linson",
                        "Jeff Cronenweth", 63000000, "Germany/USA",
                        Arrays.asList(8, 9), Arrays.asList(6, 21)), // Brad Pitt, Edward Norton, Drama, Crime
                new Movie("The Dark Knight", 2008, "Christopher Nolan", "Jonathan Nolan", "Christopher Nolan",
                        "Wally Pfister", 185000000, "USA/UK",
                        List.of(10), Arrays.asList(1, 20)), // Christian Bale, Action, Thriller
                new Movie("Schindler's List", 1993, "Steven Spielberg", "Thomas Keneally", "Steven Spielberg",
                        "Janusz Kamiński", 22000000, "USA",
                        List.of(11), Arrays.asList(4, 21)), // Liam Neeson, Biography, Drama
                new Movie("The Lord of the Rings: The Return of the King", 2003, "Peter Jackson", "J.R.R. Tolkien", "Peter Jackson",
                        "Andrew Lesnie", 94000000, "New Zealand/USA",
                        List.of(12), Arrays.asList(2, 10, 21)), // Elijah Wood, Adventure, Fantasy, Drama
                new Movie("Star Wars: Episode V - The Empire Strikes Back", 1980, "Irvin Kershner", "Leigh Brackett", "Gary Kurtz",
                        "Peter Suschitzky", 18000000, "USA",
                        Arrays.asList(13, 14), Arrays.asList(1, 2, 20)) // Mark Hamill, Harrison Ford, Action, Adventure, Sci-Fi

        ).forEach(movieDao::create);

    }


    /**
     * Opens a connection to the correct SQLite database based on the database name.
     */
    private void openDatabaseConnection() {
        String dbPath = "src/main/java/com/moviedb/database/" + databaseName + ".db";

        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to open database connection. SQLState: " + e.getSQLState() +
                    ", Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
            Dialogs.showMessageDialog("Failed to open the database connection!");  // Inform the user
        }
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

                // Sort the filtered movies alphabetically by title
                filteredMovies.sort(Comparator.comparing(Movie::getTitle));
                updateMovieListView(filteredMovies);
            } else if (newValue.isEmpty()) {

                // Fetch the original list of movies from db once the search field is empty
                try {
                    List<Movie> allMovies = movieDao.readAll();
                    updateMovieListView(allMovies);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Failed to fetch all movies from database. SQLState: " + e.getSQLState() +
                            ", Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
                    Dialogs.showMessageDialog("Failed to load movies from the database!");  // Inform the user
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
            currentSortCriterion = String.valueOf(newValue);
            sortMoviesBy(currentSortCriterion);
        });
    }


    /**
     * Sorts the movies in the movies list view based on the specified criterion.
     *
     * @param criterion The criterion to sort by.
     */
    private void sortMoviesBy(String criterion) {
        Comparator<Movie> comparator;
        switch (criterion.toLowerCase()) {
            case "title":
                comparator = Comparator.comparing(Movie::getTitle);
                break;
            case "release year":
                comparator = Comparator.comparing(Movie::getReleaseYear);
                break;
            case "director":
                comparator = Comparator.comparing(Movie::getDirector);
                break;
            case "writer":
                comparator = Comparator.comparing(Movie::getWriter);
                break;
            case "producer":
                comparator = Comparator.comparing(Movie::getProducer);
                break;
            case "cinematography":
                comparator = Comparator.comparing(Movie::getCinematographer);
                break;
            case "budget":
                comparator = Comparator.comparingInt(Movie::getBudget);
                break;
            case "country":
                comparator = Comparator.comparing(Movie::getCountry);
                break;
            default:
                return;
        }

        List<Movie> sortedList = new ArrayList<>(moviesListView.getItems());
        sortedList.sort(comparator);
        moviesListView.setItems(FXCollections.observableArrayList(sortedList));
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
        sortMoviesBy(currentSortCriterion);
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
        logger.log(Level.INFO, "Save button clicked");

        try {
            if (validateInputs()) {
                Movie updatedMovie = createMovieFromInput();

                // Movie exists
                if (currentMovie != null) {
                    updatedMovie.setId(currentMovie.getId());
                    if (movieDao.update(updatedMovie)) {
                        addOrUpdateMovieInListView(updatedMovie);

                        // Reset the current movie in memory
                        // If clearFields is reactivated, this has to be too so the
                        // program doesn't keep the "old" movie in memory
                        //currentMovie = null;
                    }
                } else {  // Movie doesn't exist, create new one
                    int newMovieId = movieDao.create(updatedMovie);
                    updatedMovie.setId(newMovieId);
                    moviesListView.getItems().add(updatedMovie);
                }
                // Sort the list again when new movie was added
                sortMoviesBy(currentSortCriterion);

                // For emptying the form after successful save
                // Reactivate 'currentMovie = null' above also if this is reactivated
                //clearFields();

                // Show the save was successful
                alertLabel.setVisible(true);
                PauseTransition delay = new PauseTransition(Duration.seconds(5));
                delay.setOnFinished(e -> alertLabel.setVisible(false));  // Hide after 5 seconds
                delay.play();
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save movie. SQLState: " + e.getSQLState() +
                    ", Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
            Dialogs.showMessageDialog("Failed to save movie data to the database!");  // Inform the user
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
     * Determines which entity (actor or genre) is currently focused based on the
     * currentListFocus enum and opens the corresponding dialog.
     *
     */
    @FXML
    void handleAdd() {
        logger.log(Level.INFO, "Add button clicked. Current focus: " + currentListFocus);

        switch (currentListFocus) {
            case ACTOR:
                openActorDialog();
                break;
            case GENRE:
                openGenreDialog();
                break;
            default:  // No action
                currentListFocus = ListFocus.NONE;
                break;
        }
    }


    /**
     * Handles the deletion of movies, actors, or genres based on the current focused list.
     * Uses the currentListFocus enum to determine which list (movies, actors, or genres) is currently active
     * and performs the deletion on the selected item from that list.
     *
     * @param event The ActionEvent triggered by the delete action.
     */
    @FXML
    void handleDelete(ActionEvent event) {
        switch (currentListFocus) {
            case MOVIE:
                Movie selectedMovie = moviesListView.getSelectionModel().getSelectedItem();
                if (selectedMovie != null) {
                    removeObjectFromList(moviesListView, selectedMovie);  // Removes also from db
                }
                break;
            case ACTOR:
                Actor selectedActor = actorsListView.getSelectionModel().getSelectedItem();
                if (selectedActor != null) {
                    removeObjectFromList(actorsListView, selectedActor);
                }
                break;
            case GENRE:
                Genre selectedGenre = genresListView.getSelectionModel().getSelectedItem();
                if (selectedGenre != null) {
                    removeObjectFromList(genresListView, selectedGenre);
                }
                break;
            default:  // No action
                currentListFocus = ListFocus.NONE;
                break;
        }
    }


    /**
     * Opens the actor dialog.
     *
     */
    private void openActorDialog() {
        openDialog("/views/ActorDialogView.fxml", "New Actor Details");
    }


    /**
     * Opens the genre dialog.
     *
     */
    private void openGenreDialog() {
        openDialog("/views/GenreDialogView.fxml", "Choose the Genres");
    }


    /**
     * Opens a dialog window based on the provided FXML file path and dialog title.
     * Initializes the dialog with the necessary controller and sets up the stage and scene.
     *
     * @param fxmlPath    The path to the FXML file for the dialog view.
     * @param dialogTitle The title of the dialog window.
     */
    private void openDialog(String fxmlPath, String dialogTitle) {
        try {
            Stage ownerStage = this.primaryStage;

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
            logger.log(Level.SEVERE, "Error opening dialog: " + e.getMessage(), e);
            Dialogs.showMessageDialog("Failed to open the dialog window!");  // Inform the user
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
        if (!(object instanceof Integer)) {  // Make sure the object is not Integer
            if (object instanceof Movie movie) {
                deleteMovie(movie);
            }
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
            logger.log(Level.SEVERE, "Deleting the movie from database failed. SQLState: " + e.getSQLState() +
                    ", Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
            Dialogs.showMessageDialog("Failed to delete the movie!");  // Inform the user
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
        writerTextField.setText(selectedMovie.getWriter());
        producerTextField.setText(selectedMovie.getProducer());
        cinematographyTextField.setText(selectedMovie.getCinematographer());
        budgetTextField.setText(String.valueOf(selectedMovie.getBudget()));
        countryTextField.setText(selectedMovie.getCountry());

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
        writerTextField.clear();
        producerTextField.clear();
        cinematographyTextField.clear();
        budgetTextField.clear();
        countryTextField.clear();
        actorsListView.getItems().clear();
        genresListView.getItems().clear();
    }


    /**
     * Validates input fields for movie details and ensures that
     * both actors and genres lists are not empty. Provides specific feedback on invalid inputs.
     *
     * @return boolean indicating whether inputs are valid or not.
     */
    private boolean validateInputs() {
        StringBuilder errorMsg = new StringBuilder();
        String updatedMovieTitle = titleTextField.getText();
        String releaseYearText = releaseYearTextField.getText();
        String updatedDirector = directorTextField.getText();
        String writer = writerTextField.getText();
        String producer = producerTextField.getText();
        String cinematographer = cinematographyTextField.getText();
        String budget = budgetTextField.getText();
        String country = countryTextField.getText();

        if (updatedMovieTitle.isEmpty()) errorMsg.append("Title is required.\n");
        if (InputValidator.isValidReleaseYear(releaseYearText)) errorMsg.append("Give release year between 1900-2099.\n");
        if (InputValidator.isValidText(updatedDirector)) errorMsg.append("Director's name is invalid.\n");
        if (InputValidator.isValidText(writer)) errorMsg.append("Writer's name is invalid.\n");
        if (InputValidator.isValidText(producer)) errorMsg.append("Producer's name is invalid.\n");
        if (InputValidator.isValidText(cinematographer)) errorMsg.append("Cinematographer's name is invalid.\n");
        if (!InputValidator.isInteger(budget)) errorMsg.append("Give budget as integer.\n");
        if (InputValidator.isValidText(country)) errorMsg.append("Invalid country name.\n");
        if (actorsListView.getItems().isEmpty()) errorMsg.append("At least one actor is required.\n");
        if (genresListView.getItems().isEmpty()) errorMsg.append("At least one genre is required.\n");

        boolean isValid = errorMsg.isEmpty();

        if (!isValid) {
            Dialogs.showMessageDialog(errorMsg.toString());
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
        String updatedWriter = writerTextField.getText();
        String updatedProducer = producerTextField.getText();
        String updatedCinematographer = cinematographyTextField.getText();
        int updatedBudget = Integer.parseInt(budgetTextField.getText());
        String updatedCountry = countryTextField.getText();

        List<Integer> updatedActorIds = new ArrayList<>();
        for (Actor actor : actorsListView.getItems()) {
            updatedActorIds.add(actor.getId());
        }

        List<Integer> updatedGenreIds = new ArrayList<>();
        for (Genre genre : genresListView.getItems()) {
            updatedGenreIds.add(genre.getId());
        }

        return new Movie(updatedMovieTitle, updatedReleaseYear, updatedDirector,
                        updatedWriter, updatedProducer, updatedCinematographer,
                        updatedBudget, updatedCountry, updatedActorIds, updatedGenreIds);
    }
}
