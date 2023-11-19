package com.moviedb.dao;

import java.util.Arrays;
import java.util.List;
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
    private List<Integer> fetchAssociatedIds(int movieId, String tableName, String columnName) throws SQLException {
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
     *
     * TODO:
     * - Modify deleting and inserting into their own methods
     * - Improve efficiency by checking first if dependencies need updating at all
     */
    public boolean update(Movie updatedMovie) {
        boolean updateSuccessful = false;

        // Define all the SQL queries
        String sqlUpdateMovie = "UPDATE movies SET title = ?, release_year = ?, director = ? WHERE id = ?";
        String sqlDeleteGenre = "DELETE FROM movie_genres WHERE movie_id = ?";
        String sqlInsertGenre = "INSERT INTO movie_genres(movie_id, genre_id) VALUES(?, ?)";
        String sqlDeleteActor = "DELETE FROM movie_actors WHERE movie_id = ?";
        String sqlInsertActor = "INSERT INTO movie_actors(movie_id, actor_id) VALUES(?, ?)";

        try {
            // Update the main movie details
            try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdateMovie)) {
                pstmt.setString(1, updatedMovie.getTitle());
                pstmt.setInt(2, updatedMovie.getReleaseYear());
                pstmt.setString(3, updatedMovie.getDirector());
                pstmt.setInt(4, updatedMovie.getId());
                pstmt.executeUpdate();
            }

            // Delete old genre dependencies and add new ones
            try (PreparedStatement pstmtDelete = connection.prepareStatement(sqlDeleteGenre)) {
                pstmtDelete.setInt(1, updatedMovie.getId());
                pstmtDelete.executeUpdate();
            }

            try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsertGenre)) {
                for (Integer genreId : updatedMovie.getGenreIds()) {
                    pstmtInsert.setInt(1, updatedMovie.getId());
                    pstmtInsert.setInt(2, genreId);
                    pstmtInsert.executeUpdate();
                }
            }

            // Delete old actor dependencies and add new ones
            try (PreparedStatement pstmtDelete = connection.prepareStatement(sqlDeleteActor)) {
                pstmtDelete.setInt(1, updatedMovie.getId());
                pstmtDelete.executeUpdate();
            }

            try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsertActor)) {
                for (Integer actorId : updatedMovie.getActorIds()) {
                    pstmtInsert.setInt(1, updatedMovie.getId());
                    pstmtInsert.setInt(2, actorId);
                    pstmtInsert.executeUpdate();
                }
            }

            updateSuccessful = true;  // No exceptions, update was succesful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updateSuccessful;
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


    /**
     * TODO:
     *  - search methods
     *  - sorting methods
     *  - find methods
     */
}
