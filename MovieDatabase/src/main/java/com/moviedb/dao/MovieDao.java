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
import fi.jyu.mit.fxgui.Dialogs;


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
    private static final String DB_URL = "jdbc:sqlite:database/moviedatabase.db";

    // Logger for logging errors
    private static final Logger logger = Logger.getLogger(MovieDao.class.getName());


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
        // Define the SQL queries
        String sqlInsertMovie = "INSERT INTO movies(title, release_year, director, writer, producer, " +
                "cinematographer, budget, country) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlInsertActor = "INSERT INTO movie_actors(movie_id, actor_id) VALUES(?, ?)";
        String sqlInsertGenre = "INSERT INTO movie_genres(movie_id, genre_id) VALUES(?, ?)";
        String sqlLastInsertId = "SELECT last_insert_rowid()";

        int generatedMovieId = -1;  // -1 for default error state

        connection.setAutoCommit(false);  // Start transaction

        // Insert the main details
        try (PreparedStatement pstmtMovie = connection.prepareStatement(sqlInsertMovie,
                Statement.RETURN_GENERATED_KEYS);  // ID for the added movie in the generated key
             PreparedStatement pstmtActor = connection.prepareStatement(sqlInsertActor);
             PreparedStatement pstmtGenre = connection.prepareStatement(sqlInsertGenre)) {
            pstmtMovie.setString(1, movie.getTitle());
            pstmtMovie.setInt(2, movie.getReleaseYear());
            pstmtMovie.setString(3, movie.getDirector());
            pstmtMovie.setString(4, movie.getWriter());
            pstmtMovie.setString(5, movie.getProducer());
            pstmtMovie.setString(6, movie.getCinematographer());
            pstmtMovie.setInt(7, movie.getBudget());
            pstmtMovie.setString(8, movie.getCountry());
            pstmtMovie.executeUpdate();

            // Retrieve the id generated for the added movie
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlLastInsertId)) {
                if (rs.next()) {
                    generatedMovieId = rs.getInt(1);
                }
            }

            // Ensure to have a valid movie ID before proceeding
            if (generatedMovieId <= 0) {
                throw new SQLException("Failed to retrieve generated movie ID");
            }

            // Add the actors to their join table
            for (int actorId : movie.getActorIds()) {
                pstmtActor.setInt(1, generatedMovieId);
                pstmtActor.setInt(2, actorId);
                pstmtActor.executeUpdate();
            }

            // Add the genres to their join table
            for (int genreId : movie.getGenreIds()) {
                pstmtGenre.setInt(1, generatedMovieId);
                pstmtGenre.setInt(2, genreId);
                pstmtGenre.executeUpdate();
            }

            connection.commit();  // Commit the transaction

        } catch (SQLException e) {
            try {
                connection.rollback();  // Attempt to rollback on failure
                logger.log(Level.INFO, "Transaction rolled back due to SQLException", e);
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
            }
            throw e;  // Re-throw the exception to be handled by the caller
        } finally {
            try {
                connection.setAutoCommit(true);  // Reset default behavior
            } catch (SQLException autoCommitEx) {
                logger.log(Level.SEVERE, "Error resetting auto-commit behavior", autoCommitEx);
            }
        }

        return generatedMovieId;
    }



    /**
     * Retrieves a movie based on its ID, including its additional details.
     *
     * @param id The unique identifier of the movie to be fetched.
     * @return The Movie object if found, null otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    public Movie read(int id) throws SQLException {
        String sql = "SELECT title, release_year, director, writer, producer, " +
                "cinematographer, budget, country FROM movies WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            // If result is found, convert it to a Movie object
            if (rs.next()) {
                String title = rs.getString("title");
                int releaseYear = rs.getInt("release_year");
                String director = rs.getString("director");
                String writer = rs.getString("writer");
                String producer = rs.getString("producer");
                String cinematographer = rs.getString("cinematographer");
                int budget = rs.getInt("budget");
                String country = rs.getString("country");

                // Fetch the list of actors and genres
                List<Integer> actorIds = fetchAssociatedIds(id, "movie_actors", "actor_id");
                List<Integer> genreIds = fetchAssociatedIds(id, "movie_genres", "genre_id");

                return new Movie(id, title, releaseYear, director, writer, producer,
                        cinematographer, budget, country, actorIds, genreIds);
            }
        }
        return null;
    }


    /**
     * Retrieves all movies from the database with their detailed information including
     * and associated actor and genre IDs.
     *
     * @return A list of all movies in the database, with full details.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<Movie> readAll() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT id, title, release_year, director, writer, producer, cinematographer, budget, country FROM movies";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int releaseYear = rs.getInt("release_year");
                String director = rs.getString("director");
                String writer = rs.getString("writer");
                String producer = rs.getString("producer");
                String cinematographer = rs.getString("cinematographer");
                int budget = rs.getInt("budget");
                String country = rs.getString("country");
                List<Integer> actorIds = fetchAssociatedIds(id, "movie_actors", "actor_id");
                List<Integer> genreIds = fetchAssociatedIds(id, "movie_genres", "genre_id");

                movies.add(new Movie(id, title, releaseYear, director, writer, producer,
                                     cinematographer, budget, country, actorIds, genreIds));
            }
        }
        return movies;
    }


    /**
     * Fetches a list of either actor or genre IDs based on a specified movie ID and table name.
     *
     * @param movieId The ID of the movie for which the actor or genre IDs are to be fetched.
     * @param tableName The name of the table (either "actors" or "genres") to fetch IDs from.
     * @return A List of Integers representing actor or genre IDs associated with the given movie ID.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<Integer> fetchAssociatedIds(int movieId, String tableName, String columnName) throws SQLException {
        List<Integer> ids = new ArrayList<>();

        // Whitelist allowed table and column names
        List<String> allowedTables = Arrays.asList("movie_actors", "movie_genres");
        List<String> allowedColumns = Arrays.asList("actor_id", "genre_id");

        if (!allowedTables.contains(tableName) || (!allowedColumns.contains(columnName))) {
            throw new IllegalArgumentException();
        }

        String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE movie_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt(columnName));
            }
        }
        return ids;
    }


    /**
     * Updates the details of a specified movie in the SQL database.
     * This includes updating the main movie details as well as associated actors and genres.
     * The method handles database transactions and rolls back in case of an error.
     *
     * @param updatedMovie The movie object containing updated details with the correct ID of the movie to be updated.
     * @return boolean true if the update was successful, otherwise false.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean update(Movie updatedMovie) throws SQLException {
        boolean updateSuccessful = false;
        Movie existingMovie = read(updatedMovie.getId());

        try {
            connection.setAutoCommit(false);  // Start transaction

            // Check if main details have changed before updating
            if (hasMainDetailsChanged(existingMovie, updatedMovie)) {
                updateMovieMainDetails(updatedMovie);
            }

            // Update genre links if changed
            if (!existingMovie.getActorIds().equals(updatedMovie.getActorIds())) {
                updateMovieLinks(updatedMovie.getId(), new HashSet<>(updatedMovie.getActorIds()), "movie_actors", "actor_id");
            }

            // Update genre links if changed
            if (!existingMovie.getGenreIds().equals(updatedMovie.getGenreIds())) {
                updateMovieLinks(updatedMovie.getId(), new HashSet<>(updatedMovie.getGenreIds()), "movie_genres", "genre_id");
            }

            connection.commit();
            updateSuccessful = true;
        } catch (SQLException e) {
            connection.rollback();  // Rollback transactions on error
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);  // Restore default behavior
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
     * This includes updating the title, release year, director, writer, producer, cinematographer, budget, and country.
     *
     * @param updatedMovie The Movie object containing the new details.
     * @throws SQLException If there's an error during the database operation.
     */
    private void updateMovieMainDetails(Movie updatedMovie) throws SQLException {
        String sql = "UPDATE movies SET title = ?, release_year = ?, director = ?, " +
                     "writer = ?, producer = ?, cinematographer = ?, budget = ?, country = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, updatedMovie.getTitle());
            pstmt.setInt(2, updatedMovie.getReleaseYear());
            pstmt.setString(3, updatedMovie.getDirector());
            pstmt.setString(4, updatedMovie.getWriter());
            pstmt.setString(5, updatedMovie.getProducer());
            pstmt.setString(6, updatedMovie.getCinematographer());
            pstmt.setInt(7, updatedMovie.getBudget());
            pstmt.setString(8, updatedMovie.getCountry());
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
        Set<Integer> currentIds = getCurrentIds(movieId, table, idColumn);

        // Delete links that are not on the updated list
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
     * Retrieves a set of current IDs linked with a specific movie from a join table.
     *
     * @param movieId The ID of the movie for which linked IDs are being fetched.
     * @param table The name of the join table from which to fetch the links (either 'movie_actors' or 'movie_genres').
     * @param idColumn The name of the column in the join table that stores the IDs ('actor_id' or 'genre_id').
     * @return A set of integer IDs currently linked with the movie in the specified join table.
     * @throws SQLException If there's an error during the database operation.
     */
    private Set<Integer> getCurrentIds(int movieId, String table, String idColumn) throws SQLException {
        Set<Integer> ids = new HashSet<>();

        String sql = "SELECT " + idColumn + " FROM " + table + " WHERE movie_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(idColumn));
                }
            }
        }
        return ids;
    }


    /**
     * Removes a specific link between a movie and an actor or genre in the database.
     *
     * @param movieId The ID of the movie from which the link is being removed.
     * @param id The ID of the linked entity (actor ID or genre ID) to be removed.
     * @param table The name of the join table (either 'movie_actors' or 'movie_genres').
     * @param idColumn The name of the column in the join table that stores the actor's
     *                 or genre's IDs (either 'actor_id' or 'genre_id').
     * @throws SQLException If there's an error during the database operation.
     */
    public void removeLinkFromMovie(int movieId, int id, String table, String idColumn) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE movie_id = ? AND " + idColumn + " = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }


    /**
     * Adds a new link between a movie and an actor or genre in the database.
     *
     * @param movieId The ID of the movie to which the link is being added.
     * @param id The ID of the associated entity (actor ID or genre ID) to be added.
     * @param table The name of the join table ('movie_actors' or 'movie_genres').
     * @param idColumn The name of the column in the join table that stores the actor's
     *                 or genre's IDs ('actor_id' or 'genre_id').
     * @throws SQLException If there's an error during the database operation.
     */
    private void addLinkToMovie(int movieId, int id, String table, String idColumn) throws SQLException {
        String sql = "INSERT INTO " + table + " (movie_id, " + idColumn + ") VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }


    /**
     * Deletes a movie and its associations from the SQL database based on its ID.
     * This includes deleting entries from movie_actors and movie_genres tables.
     * The method handles database transactions and rolls back in case of an error.
     * @param movieId The ID of the movie to be deleted.
     * @return true if the movie and its associations were successfully deleted, false otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean delete(int movieId) throws SQLException {
        String deleteActorsSql = "DELETE FROM movie_actors WHERE movie_id = ?";
        String deleteGenresSql = "DELETE FROM movie_genres WHERE movie_id = ?";
        String deleteMovieSql = "DELETE FROM movies WHERE id = ?";
        int rowsAffected = 0;

        try {
            connection.setAutoCommit(false); // Start transaction

            // Delete associations from movie_actors
            try (PreparedStatement pstmt = connection.prepareStatement(deleteActorsSql)) {
                pstmt.setInt(1, movieId);
                pstmt.executeUpdate();
            }

            // Delete associations from movie_genres
            try (PreparedStatement pstmt = connection.prepareStatement(deleteGenresSql)) {
                pstmt.setInt(1, movieId);
                pstmt.executeUpdate();
            }

            // Delete the movie
            try (PreparedStatement pstmt = connection.prepareStatement(deleteMovieSql)) {
                pstmt.setInt(1, movieId);
                rowsAffected = pstmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction on error
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true); // Restore default behavior
        }
        return rowsAffected > 0;
    }
}
