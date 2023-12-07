package com.moviedb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.moviedb.models.Genre;

/**
 * Data Access Object for the Genre class.
 * This class provides an abstraction layer between the application and the underlying database.
 * It handles all database operations related to genres, including creating, reading, updating, and deleting genre records.
 *
 * The GenreDao class ensures that genre data is accessed and manipulated in a consistent and database-agnostic manner.
 * It encapsulates all SQL queries and shields the rest of the application from direct database interactions,
 * promoting cleaner separation of concerns and making the codebase more maintainable.
 */
public class GenreDao {

    // Connection used to execute SQL queries and interact with the database.
    private Connection connection;

    // The URL pointing to the SQL database location.
    private static final String DB_URL = "jdbc:sqlite:database/moviedatabase.db";

    // Logger for exceptions
    private static final Logger logger = Logger.getLogger(MovieDao.class.getName());

    // Define the SQL queries
    private static final String SQL_READ_ALL_GENRES = "SELECT id, name FROM genres";
    private static final String SQL_GET_GENRE_BY_ID = "SELECT id, name FROM genres WHERE id = ?";
    private static final String SQL_GET_GENRE_BY_NAME = "SELECT id, name FROM genres WHERE name = ?";


    /**
     * Default constructor that initializes the connection to the default SQLite database.
     */
    public GenreDao() {
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
    public GenreDao(Connection connection) {
        this.connection = connection;
    }


    /**
     * Reads all genres from the database and returns them as a list.
     *
     * @return A list of all genres.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<Genre> readAll() throws SQLException {
        List<Genre> genres = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_READ_ALL_GENRES);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error reading all genres", e);
            throw e;
        }
        return genres;
    }


    /**
     * Retrieves a genre from the database based on its ID.
     *
     * @param id The ID of the genre to retrieve.
     * @return The genre if found, otherwise an empty optional.
     * @throws SQLException If there's an error during the database operation.
     */
    public Optional<Genre> getGenreById(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_GET_GENRE_BY_ID)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching genre by ID: " + id, e);
            throw e;
        }
        return Optional.empty();
    }


    /**
     * Retrieves a genre by its name.
     *
     * @param name The name of the genre to retrieve.
     * @return The genre if found, otherwise an empty optional.
     * @throws SQLException If there's an error during the database operation.
     */
    public Optional<Genre> getGenreByName(String name) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_GET_GENRE_BY_NAME)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching genre by name: " + name, e);
            throw e;
        }
        return Optional.empty();
    }
}
