package com.moviedb.dao;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import com.moviedb.models.Movie;


/**
 * Data Access Object for the Movie class.
 */
public class MovieDao {

    /** Connection used to execute SQL queries and interact with the database. */
    private Connection connection;

    /** The URL pointing to the SQL database location. */
    private static final String DB_URL = "jdbc:sqlite:database/moviedatabase.db";


    /**
     * Default constructor that initializes the connection to the default SQLite database.
     */
    public MovieDao() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
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
        String sqlInsertMovie = "INSERT INTO movies(title, release_year, director) VALUES(?, ?, ?)";
        String sqlInsertActor = "INSERT INTO movie_actors(movie_id, actor_id) VALUES(?, ?)";
        String sqlInsertGenre = "INSERT INTO movie_genres(movie_id, genre_id) VALUES(?, ?)";

        String sqlLastInsertId = "SELECT last_insert_rowid()";

        int generatedMovieId = -1;  // -1 for default error state

        try {
            // Insert the main details
            try (PreparedStatement pstmtMovie = connection.prepareStatement(sqlInsertMovie,
                    Statement.RETURN_GENERATED_KEYS);  // ID for the added movie in the generated key
                 PreparedStatement pstmtActor = connection.prepareStatement(sqlInsertActor);
                 PreparedStatement pstmtGenre = connection.prepareStatement(sqlInsertGenre)) {
                pstmtMovie.setString(1, movie.getTitle());
                pstmtMovie.setInt(2, movie.getReleaseYear());
                pstmtMovie.setString(3, movie.getDirector());
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
                    throw new SQLException();
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedMovieId;
    }


    /**
     * Retrieves a movie based on its ID.
     *
     * @param id The unique identifier of the movie to be fetched.
     * @return The Movie object if found, null otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    public Movie read(int id) {
        String sql = "SELECT title, release_year, director FROM movies WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            // If result is found, convert it to a Movie object
            if (rs.next()) {
                String title = rs.getString("title");
                int releaseYear = rs.getInt("release_year");
                String director = rs.getString("director");

                // Fetch the list of actors and genres
                List<Integer> actorIds = fetchAssociatedIds(id, "movie_actors", "actor_id");
                List<Integer> genreIds = fetchAssociatedIds(id, "movie_genres", "genre_id");

                return new Movie(id, title, releaseYear, director, actorIds, genreIds);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Movie> readAll() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int releaseYear = rs.getInt("release_year");
                String director = rs.getString("director");
                List<Integer> actors = fetchAssociatedIds(id, "movie_actors", "actor_id");
                List<Integer> genres = fetchAssociatedIds(id, "movie_genres", "genre_id");

                Movie movie = new Movie(id, title, releaseYear, director, actors, genres);
                movies.add(movie);
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
     * If associated actors or genres are modified, the relevant links in the database are updated accordingly.
     *
     * @param updatedMovie The movie object containing updated details with the correct ID of the movie to be updated.
     * @return boolean true if the update was successful, otherwise false.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean update(Movie updatedMovie) throws SQLException {
        boolean updateSuccessful = false;
        Movie existingMovie = read(updatedMovie.getId());

        try {
            connection.setAutoCommit(false);

            // Update movie details if they are updated
            if (!existingMovie.getTitle().equals(updatedMovie.getTitle()) ||
                    existingMovie.getReleaseYear() != updatedMovie.getReleaseYear() ||
                    !existingMovie.getDirector().equals(updatedMovie.getDirector())) {
                updateMovieMainDetails(updatedMovie);
            }

            // Update the actor links if they have changed
            if (!existingMovie.getActorIds().equals(updatedMovie.getActorIds())) {
                updateMovieLinks(updatedMovie.getId(), new HashSet<>(updatedMovie.getActorIds()), "movie_actors", "actor_id");
            }

            // Update the genre links if they have changed
            if (!existingMovie.getGenreIds().equals(updatedMovie.getGenreIds())) {
                updateMovieLinks(updatedMovie.getId(), new HashSet<>(updatedMovie.getGenreIds()), "movie_genres", "genre_id");
            }

            connection.commit();
            updateSuccessful = true;
        } catch (SQLException e) {
            connection.rollback();  // Undo changes if there's an error
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }

        return updateSuccessful;
    }


    /**
     * Updates the main details of a specified movie in the database (title, release year and director).
     *
     * @param updatedMovie The Movie object containing the new details.
     * @throws SQLException If there's an error during the database operation.
     */
    private void updateMovieMainDetails(Movie updatedMovie) throws SQLException {
        String sql = "UPDATE movies SET title = ?, release_year = ?, director = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, updatedMovie.getTitle());
            pstmt.setInt(2, updatedMovie.getReleaseYear());
            pstmt.setString(3, updatedMovie.getDirector());
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
    private void removeLinkFromMovie(int movieId, int id, String table, String idColumn) throws SQLException {
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
     * Deletes a movie from the SQL database based on its ID.
     *
     * @param movieId The ID of the movie to be deleted.
     * @return true if the movie was successfully deleted, false otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean delete(int movieId) {
        String sql = "DELETE FROM movies WHERE id = ?";
        int rowsAffected = 0;  // To make sure the SQL query affected at least one row

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            rowsAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsAffected > 0;
    }


    /**
     * Gets all the movie titles from movies table in the database.
     *
     * @return List of all the movies in the database.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<String> getAllMovieTitles() throws SQLException {
        List<String> movieTitles = new ArrayList<>();
        String query = "SELECT title FROM movies";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                movieTitles.add(rs.getString("title"));
            }
        }
        return movieTitles;
    }


    /**
     * Retrieves a movie based on its ID.
     *
     * @param movieTitle The unique identifier of the movie to be fetched.
     * @return The Movie object if found, null otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    public Movie getMovieByTitle(String movieTitle) throws SQLException {
        String sql = "SELECT id, release_year, director FROM movies WHERE title = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, movieTitle);
            ResultSet rs = pstmt.executeQuery();

            // If result is found, convert it to a Movie object
            if (rs.next()) {
                int id = rs.getInt("id");
                int releaseYear = rs.getInt("release_year");
                String director = rs.getString("director");

                // Fetch the list of actors and genres
                List<Integer> actorIds = fetchAssociatedIds(id, "movie_actors", "actor_id");
                List<Integer> genreIds = fetchAssociatedIds(id, "movie_genres", "genre_id");

                return new Movie(id, movieTitle, releaseYear, director, actorIds, genreIds);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addActorToMovie(int actorId, int movieId) throws SQLException {
        String sql = "INSERT INTO movie_actors (movie_id, actor_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, actorId);
            pstmt.executeUpdate();
        }
    }


    /**
     * TODO:
     *  - search methods
     *  - sorting methods
     *  - find methods
     */
}
