package com.moviedb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.moviedb.models.Movie;


/**
 * Data Access Object for the Movie class.
 * This class provides an abstraction layer between the application and the underlying database.
 * It handles all database operations related to movies, including creating, reading, updating, and deleting movie records.
 * <p>
 * The MovieDao class ensures that movie data is accessed and manipulated in a consistent and database-agnostic manner.
 * It encapsulates all SQL queries and shields the rest of the application from direct database interactions,
 * promoting cleaner separation of concerns and making the codebase more maintainable.
 */
public class MovieDao {

    // Connection used to execute SQL queries and interact with the database.
    private Connection connection;

    // The URL pointing to the SQL database location.
    private static final String DB_URL = "jdbc:sqlite:";

    // Logger for logging errors
    private static final Logger logger = Logger.getLogger(MovieDao.class.getName());

    // Define the SQL queries
    private static final String SQL_INSERT_MOVIE = "INSERT INTO movies(title, release_year, director, writer, producer, " +
            "cinematographer, budget, country) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_ACTOR = "INSERT INTO movie_actors(movie_id, actor_id) VALUES(?, ?)";
    private static final String SQL_INSERT_GENRE = "INSERT INTO movie_genres(movie_id, genre_id) VALUES(?, ?)";
    private static final String SQL_READ_MOVIE = "SELECT title, release_year, director, writer, producer, " +
            "cinematographer, budget, country FROM movies WHERE id = ?";
    private static final String SQL_READ_ALL_MOVIES = "SELECT id, title, release_year, director, writer, producer, " +
            "cinematographer, budget, country FROM movies";

    private static final String SQL_UPDATE_MOVIE_MAIN_DETAILS = "UPDATE movies SET title = ?, release_year = ?, director = ?, " +
            "writer = ?, producer = ?, cinematographer = ?, budget = ?, country = ? WHERE id = ?";
    private static final String SQL_DELETE_ACTORS = "DELETE FROM movie_actors WHERE movie_id = ?";
    private static final String SQL_DELETE_GENRES = "DELETE FROM movie_genres WHERE movie_id = ?";
    private static final String SQL_DELETE_MOVIE = "DELETE FROM movies WHERE id = ?";


    /**
     * Default constructor that initializes the connection to the default SQLite database.
     */
    public MovieDao() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating MovieDao instance. SQLState: " + e.getSQLState() +
                    ", Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
        }
    }


    /**
     * Constructor that accepts a specific database connection.
     *
     * @param connection The specific connection to database.
     */
    public MovieDao(Connection connection) {
        this.connection = connection;
    }


    /**
     * Creates and adds a movie to the SQL database.
     *
     * @param movie The movie to be added.
     * @return The generated ID of the added movie, or -1 if an error occurs.
     * @throws SQLException If there's an error during the database operation.
     */
    public int create(Movie movie) throws SQLException {
        int generatedMovieId = -1; // -1 for default error state

        try {
            connection.setAutoCommit(false); // Start transaction

            // Insert the main details
            try (PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT_MOVIE,
                    Statement.RETURN_GENERATED_KEYS)) { // ID for the added movie in the generated key
                setPreparedStatementForMovie(pstmt, movie);
                pstmt.executeUpdate();

                generatedMovieId = fetchGeneratedId(pstmt);

                // Ensure to have a valid movie ID before proceeding
                if (generatedMovieId <= 0) {
                    throw new SQLException("Failed to retrieve generated movie ID");
                }

                // Add actors and genres to their join tables
                insertActorsToMovie(connection, generatedMovieId, movie.getActorIds());
                insertGenresToMovie(connection, generatedMovieId, movie.getGenreIds());

                connection.commit(); // Commit the transaction
            }
        } catch (SQLException e) {
            try {
                connection.rollback(); // Attempt to rollback on failure
                logger.log(Level.INFO, "Transaction rolled back due to SQLException", e);
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
            }
            throw e; // Re-throw the exception to be handled by the caller
        } finally {
            try {
                connection.setAutoCommit(true); // Restore default behavior
            } catch (SQLException autoCommitEx) {
                logger.log(Level.SEVERE, "Error resetting auto-commit behavior", autoCommitEx);
            }
        }

        return generatedMovieId;
    }


    /**
     * Retrieves the generated ID for the last inserted row.
     * <p>
     * This method handles ID retrieval for different databases. It uses "SELECT last_insert_rowid()"
     * for SQLite due to its specific behavior with auto-increment keys. For other databases like H2,
     * it utilizes the standard JDBC getGeneratedKeys method. This approach is necessary to resolve
     * compatibility issues between production (SQLite) and testing environments (H2), ensuring
     * consistent behavior across different database systems.
     *
     * @param pstmt The PreparedStatement from which the generated key is retrieved.
     * @return The generated ID of the last inserted row, or -1 if an error occurs.
     * @throws SQLException If there's an error during the database operation.
     */
    private int fetchGeneratedId(PreparedStatement pstmt) throws SQLException {
        int generatedId = -1;
        String databaseProductName = connection.getMetaData().getDatabaseProductName();

        if (databaseProductName.equals("SQLite")) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    generatedId = rs.getInt(1); // Retrieve the generated key (ID)
                }
            }
        } else {  // For H2 db
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        }
        return generatedId;
    }



    /**
     * Sets the parameters of a PreparedStatement for movie insertion.
     * Takes a PreparedStatement and a Movie object and sets the
     * parameters based on the properties of the Movie object.
     *
     * @param pstmt The PreparedStatement to be configured for inserting a movie.
     * @param movie The Movie object containing the movie details.
     * @throws SQLException if there's an error setting the PreparedStatement parameters.
     */
    private void setPreparedStatementForMovie(PreparedStatement pstmt, Movie movie) throws SQLException {
        pstmt.setString(1, movie.getTitle());
        pstmt.setInt(2, movie.getReleaseYear());
        pstmt.setString(3, movie.getDirector());
        pstmt.setString(4, movie.getWriter());
        pstmt.setString(5, movie.getProducer());
        pstmt.setString(6, movie.getCinematographer());
        pstmt.setInt(7, movie.getBudget());
        pstmt.setString(8, movie.getCountry());
    }


    /**
     * Inserts associations between the movie and its actors into the database.
     * Iterates over a list of actor IDs and inserts each actor ID along with
     * the movie ID into the 'movie_actors' table.
     *
     * @param connection The database connection to use for the insertion.
     * @param movieId The ID of the movie for which actors are being inserted.
     * @param actorIds A list of actor IDs to be associated with the movie.
     * @throws SQLException if there's an error inserting the actor records.
     */
    private void insertActorsToMovie(Connection connection, int movieId, List<Integer> actorIds) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT_ACTOR)) {
            for (int actorId : actorIds) {
                pstmt.setInt(1, movieId);
                pstmt.setInt(2, actorId);
                pstmt.executeUpdate();
            }
        }
    }


    /**
     * Inserts associations between the movie and its genres into the database.
     * Iterates over a list of genre IDs and inserts each genre ID along with
     * the movie ID into the 'movie_genres' table.
     *
     * @param connection The database connection to use for the insertion.
     * @param movieId The ID of the movie for which genres are being inserted.
     * @param genreIds A list of genre IDs to be associated with the movie.
     * @throws SQLException if there's an error inserting the genre records.
     */
    private void insertGenresToMovie(Connection connection, int movieId, List<Integer> genreIds) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT_GENRE)) {
            for (int genreId : genreIds) {
                pstmt.setInt(1, movieId);
                pstmt.setInt(2, genreId);
                pstmt.executeUpdate();
            }
        }
    }


    /**
     * Retrieves a movie based on its ID, including its additional details.
     *
     * @param id The unique identifier of the movie to be fetched.
     * @return The Movie object if found, null otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    public Movie read(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_READ_MOVIE)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return convertResultSetToMovie(rs, id);
            }
        }
        return null;
    }


    /**
     * Retrieves all movies from the database with their detailed information including
     * associated actor and genre IDs.
     *
     * @return A list of all movies in the database, with full details.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<Movie> readAll() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_READ_ALL_MOVIES);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                movies.add(convertResultSetToMovie(rs, id));
            }
        }
        return movies;
    }


    /**
     * Creates a Movie object from a ResultSet.
     *
     * @param rs The ResultSet from which movie data is extracted.
     * @param id The ID of the movie.
     * @return A Movie object populated with data from the ResultSet.
     * @throws SQLException If there's an error during data extraction.
     */
    private Movie convertResultSetToMovie(ResultSet rs, int id) throws SQLException {
        String title = rs.getString("title");
        int releaseYear = rs.getInt("release_year");
        String director = rs.getString("director");
        String writer = rs.getString("writer");
        String producer = rs.getString("producer");
        String cinematographer = rs.getString("cinematographer");
        int budget = rs.getInt("budget");
        String country = rs.getString("country");

        // Convert Sets to Lists
        List<Integer> actorIds = new ArrayList<>(fetchAssociatedIds(id, "movie_actors", "actor_id"));
        List<Integer> genreIds = new ArrayList<>(fetchAssociatedIds(id, "movie_genres", "genre_id"));

        return new Movie(id, title, releaseYear, director, writer, producer,
                cinematographer, budget, country, actorIds, genreIds);
    }


    /**
     * Fetches a set of either actor or genre IDs based on a specified movie ID and table name.
     *
     * @param movieId The ID of the movie for which the actor or genre IDs are to be fetched.
     * @param tableName The name of the table (either "movie_actors" or "movie_genres") to fetch IDs from.
     * @param columnName The name of the column (either "actor_id" or "genre_id") to fetch IDs from.
     * @return A Set of Integers representing actor or genre IDs associated with the given movie ID.
     * @throws SQLException If there's an error during the database operation.
     */
    private Set<Integer> fetchAssociatedIds(int movieId, String tableName, String columnName) throws SQLException {
        Set<Integer> ids = new HashSet<>();

        // Validate table and column names to prevent SQL injection
        validateTableNameAndColumnName(tableName, columnName);

        // Construct SQL query dynamically using safe table and column names
        String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE movie_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(columnName)); // Fetch and add each ID to the set
                }
            }
        }
        return ids; // Return the set of associated IDs
    }


    /**
     * Validates that the given table name and column name are allowed.
     * Throws IllegalArgumentException if they are not valid.
     *
     * @param table The table name to validate.
     * @param column The column name to validate.
     */
    private void validateTableNameAndColumnName(String table, String column) {
        List<String> allowedTables = Arrays.asList("movie_actors", "movie_genres");
        List<String> allowedColumns = Arrays.asList("actor_id", "genre_id");

        if (!allowedTables.contains(table) || !allowedColumns.contains(column)) {
            throw new IllegalArgumentException("Invalid table or column name");
        }
    }


    /**
     * Updates the details of a specified movie in the SQL database, which
     * includes updating the main movie details as well as associated actors and genres.
     * It handles database transactions and rolls back in case of an error.
     *
     * @param updatedMovie The movie object containing updated details with the correct ID of the movie to be updated.
     * @return boolean true if the update was successful, otherwise false.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean update(Movie updatedMovie) throws SQLException {
        boolean updateSuccessful = false;

        try {
            connection.setAutoCommit(false);  // Start transaction

            // Check that the movie exists
            Movie existingMovie = read(updatedMovie.getId());
            if (existingMovie == null) {  // Can't update if doesn't exist
                connection.setAutoCommit(true);
                return false;
            }

            // Update main movie details if they have changed
            if (hasMainDetailsChanged(existingMovie, updatedMovie)) {
                updateMovieMainDetails(updatedMovie);
            }

            // Update actor links if they have changed
            if (!existingMovie.getActorIds().equals(updatedMovie.getActorIds())) {
                updateMovieLinks(updatedMovie.getId(), new HashSet<>(updatedMovie.getActorIds()), "movie_actors", "actor_id");
            }

            // Update genre links if they have changed
            if (!existingMovie.getGenreIds().equals(updatedMovie.getGenreIds())) {
                updateMovieLinks(updatedMovie.getId(), new HashSet<>(updatedMovie.getGenreIds()), "movie_genres", "genre_id");
            }

            connection.commit();  // Commit the transaction if all updates were successful
            updateSuccessful = true;
        } catch (SQLException e) {
            try {
                connection.rollback();  // Try to rollback on error
                logger.log(Level.SEVERE, "Error updating movie with ID: " + updatedMovie.getId(), e);
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);  // Restore default behavior
            } catch (SQLException autoCommitEx) {
                logger.log(Level.SEVERE, "Error resetting auto-commit behavior", autoCommitEx);
            }
        }

        return updateSuccessful;
    }


    /**
     * Checks if the main details of the movie have changed.
     *
     * @param existingMovie The current movie in the database.
     * @param updatedMovie The movie with updated details.
     * @return boolean true if there are changes in the main details, otherwise false.
     */
    private boolean hasMainDetailsChanged(Movie existingMovie, Movie updatedMovie) {
        return !Objects.equals(existingMovie.getTitle(), updatedMovie.getTitle()) ||
                existingMovie.getReleaseYear() != updatedMovie.getReleaseYear() ||
                !Objects.equals(existingMovie.getDirector(), updatedMovie.getDirector()) ||
                !Objects.equals(existingMovie.getWriter(), updatedMovie.getWriter()) ||
                !Objects.equals(existingMovie.getProducer(), updatedMovie.getProducer()) ||
                !Objects.equals(existingMovie.getCinematographer(), updatedMovie.getCinematographer()) ||
                existingMovie.getBudget() != updatedMovie.getBudget() ||
                !Objects.equals(existingMovie.getCountry(), updatedMovie.getCountry());
    }


    /**
     * Updates the main details of a specified movie in the database.
     *
     * @param updatedMovie The Movie object containing the new details.
     * @throws SQLException If there's an error during the database operation.
     */
    private void updateMovieMainDetails(Movie updatedMovie) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE_MOVIE_MAIN_DETAILS)) {
            // Use existing method to set common fields
            setPreparedStatementForMovie(pstmt, updatedMovie);

            // Set the movie ID as the last parameter for the WHERE clause
            pstmt.setInt(9, updatedMovie.getId());

            pstmt.executeUpdate();
        }
    }


    /**
     * Updates the link associations for a movie in the database by adding new links
     * and removing outdated ones for a specific Movie.
     *
     * @param movieId The ID of the movie for which links are being updated.
     * @param updatedIds A set of new IDs (either actor or genre IDs) to be associated with the movie.
     * @param table The name of the table where the links are stored (either 'movie_actors' or 'movie_genres').
     * @param idColumn The name of the column in the join table that stores the IDs (either 'actor_id' or 'genre_id').
     * @throws SQLException If there's an error during the database operation.
     */
    private void updateMovieLinks(int movieId, Set<Integer> updatedIds, String table, String idColumn) throws SQLException {
        // Check for valid table and column names to prevent SQL injection
        validateTableNameAndColumnName(table, idColumn);

        Set<Integer> currentIds = fetchAssociatedIds(movieId, table, idColumn);

        // Delete links that are not in the updated list
        for (Integer id : currentIds) {
            if (!updatedIds.contains(id)) {
                removeLinkFromMovie(movieId, id, table, idColumn);
            }
        }

        // Add new links
        for (Integer id : updatedIds) {
            if (!currentIds.contains(id)) {
                addLinkToMovie(movieId, id, table, idColumn);
            }
        }
    }


    /**
     * Removes a specific link between a movie and an actor or genre in the database.
     * <p>
     * NOTE: IntelliJ IDEA may warn about SQL injection for the concatenation in the SQL query and
     * SuppressWarnings won't remove this. The warning is, however, a false positive here because
     * 'validateTableNameAndColumnName' ensures 'table' and 'idColumn' are from a safe list, and
     * PreparedStatement is used, ensuring 'movieId' and 'id' are properly escaped.
     *
     * @param movieId The ID of the movie from which the link is being removed.
     * @param id The ID of the linked entity (actor ID or genre ID) to be removed.
     * @param table The name of the join table (either 'movie_actors' or 'movie_genres').
     * @param idColumn The name of the column in the join table that stores the actor's
     *                 or genre's IDs (either 'actor_id' or 'genre_id').
     * @throws SQLException If there's an error during the database operation.
     */
    public void removeLinkFromMovie(int movieId, int id, String table, String idColumn) throws SQLException {
        // Check for valid table and column names to prevent SQL injection
        validateTableNameAndColumnName(table, idColumn);

        String sql = "DELETE FROM " + table + " WHERE movie_id = ? AND " + idColumn + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }


    /**
     * Adds a new link between a movie and an actor or genre in the database.
     * <p>
     * NOTE: IntelliJ IDEA may warn about SQL injection for the concatenation in the SQL query and
     * SuppressWarnings won't remove this. The warning is, however, a false positive here because
     * 'validateTableNameAndColumnName' ensures 'table' and 'idColumn' are from a safe list, and
     * PreparedStatement is used, ensuring 'movieId' and 'id' are properly escaped.
     *
     * @param movieId The ID of the movie to which the link is being added.
     * @param id The ID of the associated entity (actor ID or genre ID) to be added.
     * @param table The name of the join table ('movie_actors' or 'movie_genres').
     * @param idColumn The name of the column in the join table that stores the actor's
     *                 or genre's IDs ('actor_id' or 'genre_id').
     * @throws SQLException If there's an error during the database operation.
     */
    private void addLinkToMovie(int movieId, int id, String table, String idColumn) throws SQLException {
        // Check for valid table and colun names to prevent SQL injection
        validateTableNameAndColumnName(table, idColumn);

        String sql = "INSERT INTO " + table + " (movie_id, " + idColumn + ") VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }


    /**
     * Deletes a movie and its associations from the SQL database based on its ID, which
     * Includes deleting entries from movie_actors and movie_genres tables.
     * It handles database transactions and rolls back in case of an error.
     *
     * @param movieId The ID of the movie to be deleted.
     * @return true if the movie and its associations were successfully deleted, false otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean delete(int movieId) throws SQLException {
        int rowsAffected;

        try {
            connection.setAutoCommit(false); // Start transaction

            // Delete associations from movie_actors
            try (PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE_ACTORS)) {
                pstmt.setInt(1, movieId);
                pstmt.executeUpdate();
            }

            // Delete associations from movie_genres
            try (PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE_GENRES)) {
                pstmt.setInt(1, movieId);
                pstmt.executeUpdate();
            }

            // Delete the movie
            try (PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE_MOVIE)) {
                pstmt.setInt(1, movieId);
                rowsAffected = pstmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback(); // Try to rollback on error
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
            }
            logger.log(Level.SEVERE, "Error deleting movie with ID: " + movieId, e);
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Restore default behavior
            } catch (SQLException autoCommitEx) {
                logger.log(Level.SEVERE, "Error resetting auto-commit behavior", autoCommitEx);
            }
        }
        return rowsAffected > 0;
    }
}
