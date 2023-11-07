package com.moviedb.dao;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GenreDaoTest extends FilledDBSetup {
    private GenreDao dao;
    private List<Genre> expectedGenres;

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
            e.printStackTrace();
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            fail("Error fetching the expected genres");
        }
    }


    @Test
    void readAllTest() {
        List<Genre> fetchedGenres = dao.readAll();
        assertEquals(expectedGenres, fetchedGenres);
    }


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
