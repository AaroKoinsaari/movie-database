package com.moviedb.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Genre;



/**
 * This class contains unit tests for the GenreDao class using a pre-filled database setup.
 * It operates under the assumption that genres already exist in the database,
 * with known IDs that are used within the tests.
 * Each test method is designed to test a single functionality of the GenreDao class,
 * verifying the expected behavior against the known state of the database.
 */
public class GenreDaoTest extends FilledDBSetup {

    private GenreDao dao;  // Instance of GenreDao used across all test cases

    private List<Genre> expectedGenres;  // Instance of the predefined genres in the setup


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
