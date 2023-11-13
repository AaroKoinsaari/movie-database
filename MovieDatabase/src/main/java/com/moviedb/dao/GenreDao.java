package com.moviedb.dao;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import com.moviedb.models.Genre;


/**
 * Data Access Object for the Genre class.
 */
public class GenreDao {

    /** Connection used to execute SQL queries and interact with the database. */
    private Connection connection;

    /** The URL pointing to the SQL database location. */
    private static final String DB_URL = "jdbc:sqlite:database/moviedatabase.db";


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
     * Reads all genres from the SQL database and returns them as a list.
     *
     * @return A list of all genres.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<Genre> readAll() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT id, name FROM genres";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Insert the values of name and id from every row to the ArrayList
                genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }


    /**
     * Retrieves a genre from the SQL database based on its ID.
     *
     * @param id The ID of the genre to retrieve.
     * @return The genre if found, otherwise an empty optional.
     * @throws SQLException If there's an error during the database operation.
     */
    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            // Wrap the result in Optional if found
            if (rs.next()) {
                return Optional.of(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    public Optional<Genre> getGenreByName(String name) {
        String sql = "SELECT id, name FROM genres WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            // Wrap the result in Opitonal if found
            if (rs.next()) {
                return Optional.of(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
