/*
 * Copyright (c) 2023-2024 Aaro Koinsaari
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.moviedb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moviedb.models.Genre;


public class GenreDao extends BaseDao {

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
    public Optional<Genre> getGenreByName(String name) throws SQLException {
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
