package com.moviedb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

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

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM genres ORDER BY id")) {

            // Retrieve and construct the genres with the IDs assigned in the database
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                expectedGenres.add(new Genre(id, name));
            }
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            fail("Error fetching the expected genres");
        }
    }


    /** Tests the reading of all genres from the database. */
    @Test
    void readAllTest() {
        List<Genre> fetchedGenres = dao.readAll();
        assertEquals(expectedGenres, fetchedGenres);
    }


    /** Tests getting all the genres from the database by their ID. */
    @Test
    void getGenreByIdTest() {
        Genre expectedActionGenre = new Genre(1, "Action");
        Optional<Genre> actualActionGenre = dao.getGenreById(1);
        assertTrue(actualActionGenre.isPresent());
        assertEquals(expectedActionGenre, actualActionGenre.get());

        Genre expectedAdventureGenre = new Genre(2, "Adventure");
        Optional<Genre> actualAdventureGenre = dao.getGenreById(2);
        assertTrue(actualAdventureGenre.isPresent());
        assertEquals(expectedAdventureGenre, actualAdventureGenre.get());

        Genre expectedHorrorGenre = new Genre(8, "Horror");
        Optional<Genre> actualHorrorGenre = dao.getGenreById(8);
        assertTrue(actualHorrorGenre.isPresent());
        assertEquals(expectedHorrorGenre, actualHorrorGenre.get());

        int nonExistentId = 0;
        Optional<Genre> nonExistentGenre = dao.getGenreById(nonExistentId);
        assertFalse(nonExistentGenre.isPresent());
    }


    /** Tests getting all the genres from the database by their name. */
    @Test
    void getGenreByNameTest() {
        Genre expectedActionGenre = new Genre(1, "Action");
        Optional<Genre> actualActionGenre = dao.getGenreByName("Action");
        assertTrue(actualActionGenre.isPresent());
        assertEquals(expectedActionGenre, actualActionGenre.get());

        Genre expectedAdventureGenre = new Genre(2, "Adventure");
        Optional<Genre> actualAdventureGenre = dao.getGenreByName("Adventure");
        assertTrue(actualAdventureGenre.isPresent());
        assertEquals(expectedAdventureGenre, actualAdventureGenre.get());

        Genre expectedHorrorGenre = new Genre(8, "Horror");
        Optional<Genre> actualHorrorGenre = dao.getGenreByName("Horror");
        assertTrue(actualHorrorGenre.isPresent());
        assertEquals(expectedHorrorGenre, actualHorrorGenre.get());

        String nonExistentGenreName = "Test";
        Optional<Genre> nonExistentGenre = dao.getGenreByName(nonExistentGenreName);
        assertFalse(nonExistentGenre.isPresent());
    }
}
