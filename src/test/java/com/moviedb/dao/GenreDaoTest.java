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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * This class contains unit tests for the GenreDao class using a pre-filled H2 database setup.
 */
public class GenreDaoTest extends FilledDBSetup {

    private GenreDao dao;
    private List<Genre> expectedGenres;


    /**
     * Additional setup for the database for each test.
     * Initializes the connection to the test database for each test and collects
     * all the predefined genres from the database to a list.
     */
    @BeforeEach
    void setUp() {
        dao = new GenreDao(connection);
        expectedGenres = new ArrayList<>();

        // Retrieve and construct the genres with the IDs assigned in the database
        assertDoesNotThrow(() -> {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM genres ORDER BY id")) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    expectedGenres.add(new Genre(id, name));
                }
            }
        }, "Fetching genres in setup should not throw SQLException");
    }


    @Test
    @DisplayName("Read all genres from the database")
    void testReadAll() {
        List<Genre> fetchedGenres = assertDoesNotThrow(() -> dao.readAll(),
                "Reading all genres should not throw SQLException");
        assertEquals(expectedGenres, fetchedGenres);
    }


    @Test
    @DisplayName("Get genres by ID from the database")
    void testGetGenreById() {
        // Testing retrieval of existing genres by their ID
        Optional<Genre> actualActionGenre = assertDoesNotThrow(() -> dao.getGenreById(1),
                "Reading genre 'Action' by ID should not throw SQLException");
        assertTrue(actualActionGenre.isPresent(), "Genre 'Action' should be present");
        assertEquals(new Genre(1, "Action"), actualActionGenre.get());

        Optional<Genre> actualAdventureGenre = assertDoesNotThrow(() -> dao.getGenreById(2),
                "Reading genre 'Adventure' by ID should not throw SQLException");
        assertTrue(actualAdventureGenre.isPresent(), "Genre 'Adventure' should be present");
        assertEquals(new Genre(2, "Adventure"), actualAdventureGenre.get());

        Optional<Genre> actualHorrorGenre = assertDoesNotThrow(() -> dao.getGenreById(8),
                "Reading genre 'Horror' by ID should not throw SQLException");
        assertTrue(actualHorrorGenre.isPresent(), "Genre 'Horror' should be present");
        assertEquals(new Genre(8, "Horror"), actualHorrorGenre.get());

        // Testing retrieval of a non-existent genre
        int nonExistentId = 0;
        Optional<Genre> nonExistentGenre = assertDoesNotThrow(() -> dao.getGenreById(nonExistentId),
                "Reading non-existent genre by ID should not throw SQLException");
        assertFalse(nonExistentGenre.isPresent(), "Non-existent genre should not be present");
    }


    @Test
    @DisplayName("Get genres by name from the database")
    void testGetGenreByName() {
        // Testing retrieval of existing genres by their name
        Optional<Genre> actualActionGenre = assertDoesNotThrow(() -> dao.getGenreByName("Action"),
                "Reading genre 'Action' by name should not throw SQLException");
        assertTrue(actualActionGenre.isPresent(), "Genre 'Action' should be present");
        assertEquals(new Genre(1, "Action"), actualActionGenre.get());

        Optional<Genre> actualAdventureGenre = assertDoesNotThrow(() -> dao.getGenreByName("Adventure"),
                "Reading genre 'Adventure' by name should not throw SQLException");
        assertTrue(actualAdventureGenre.isPresent(), "Genre 'Adventure' should be present");
        assertEquals(new Genre(2, "Adventure"), actualAdventureGenre.get());

        Optional<Genre> actualHorrorGenre = assertDoesNotThrow(() -> dao.getGenreByName("Horror"),
                "Reading genre 'Horror' by name should not throw SQLException");
        assertTrue(actualHorrorGenre.isPresent(), "Genre 'Horror' should be present");
        assertEquals(new Genre(8, "Horror"), actualHorrorGenre.get());

        // Testing retrieval of a non-existent genre
        String nonExistentGenreName = "Test";
        Optional<Genre> nonExistentGenre = assertDoesNotThrow(() -> dao.getGenreByName(nonExistentGenreName),
                "Reading non-existent genre by name should not throw SQLException");
        assertFalse(nonExistentGenre.isPresent(), "Non-existent genre 'Test' should not be present");
    }
}
