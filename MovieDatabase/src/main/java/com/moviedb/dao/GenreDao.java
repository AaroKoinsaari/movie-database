package com.moviedb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moviedb.models.Genre;


/**
 * Data Access Object for the Genre class.
 * This class acts as a bridge between the application's business logic and the underlying database,
 * managing all database operations concerning genres.
 * <p>
 * The GenreDao class provides a uniform approach to handling genre data, regardless of the database technology used.
 * It abstracts SQL queries away from the rest of the application, enabling cleaner code separation and improved code maintainability.
 */
public class GenreDao extends BaseDao {

    // Define SQL queries
    private static final String SQL_READ_ALL_GENRES = "SELECT id, name FROM genres";
    private static final String SQL_GET_GENRE_BY_ID = "SELECT id, name FROM genres WHERE id = ?";
    private static final String SQL_GET_GENRE_BY_NAME = "SELECT id, name FROM genres WHERE name = ?";


    /**
     * Default constructor that initializes the connection to the default SQLite database.
     */
    public GenreDao() {
        super();
    }


    /**
     * Constructor that accepts a specific database connection.
     *
     * @param connection The specific connection to database.
     */
    public GenreDao(Connection connection) {
        super(connection);
    }


    /**
     * Reads all genres from the SQL database and returns them as a list.
     *
     * @return A list of all genres.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<Genre> readAll() throws SQLException {
        List<Genre> genres = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(SQL_READ_ALL_GENRES);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Insert the values of name and id from every row to the ArrayList
                genres.add(convertResultSetToGenre(rs));
            }
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

            // Wrap the result in Optional if found
            if (rs.next()) {
                return Optional.of(convertResultSetToGenre(rs));
            }
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
    public Optional<Genre> getGenreByName(String name) throws SQLException{
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_GET_GENRE_BY_NAME)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            // Wrap the result in Opitonal if found
            if (rs.next()) {
                return Optional.of(convertResultSetToGenre(rs));
            }
        }
        return Optional.empty();
    }


    /**
     * Creates a Genre object from a ResultSet.
     *
     * @param rs The ResultSet from which genre data is extracted.
     * @return A Genre object populated with data from the ResultSet.
     * @throws SQLException If there's an error during data extraction.
     */
    private Genre convertResultSetToGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }

}
