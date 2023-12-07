package com.moviedb.dao;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Movie;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This class contains unit tests for the MovieDao class using a pre-filled database setup.
 * It operates under the assumption that certain movies, genres, and actors already exist in the database,
 * with known IDs that are used within the tests. These IDs are annotated within the test methods.
 * Each test method is designed to test a single functionality of the MovieDao class, verifying
 * the expected behavior against the known state of the database.
 */
public class MovieDaoFilledDBTest extends FilledDBSetup {
    private MovieDao dao;  // Instance of MovieDao used across all test cases

    /**
     * Additional setup for the filled database for each test.
     * Initializes the connection to the test database for each test.
     */
    @BeforeEach
    public void setUp() {
        dao = new MovieDao(connection);
    }


    @Test
    @DisplayName("Test creating a new movie in the database")
    void testCreate() {
        // Create new test movie
        Movie testMovie = new Movie(
                "Test Movie", 2023, "Test Director",
                "Test Writer", "Test Producer", "Test Cinematographer",
                100000000, "USA", Arrays.asList(1, 2, 5), Arrays.asList(1, 2, 3, 5));

        try {
            int generatedId = dao.create(testMovie);
            assertTrue(generatedId > 0);  // Confirm the movie was added successfully

            Movie fetchedMovie = dao.read(generatedId);
            assertNotNull(fetchedMovie);  // Varmistetaan, että elokuva löytyy tietokannasta
            assertEquals("Test Movie", fetchedMovie.getTitle());
            assertEquals(2023, fetchedMovie.getReleaseYear());
            assertEquals("Test Director", fetchedMovie.getDirector());
            assertEquals("Test Writer", fetchedMovie.getWriter());
            assertEquals("Test Producer", fetchedMovie.getProducer());
            assertEquals("Test Cinematographer", fetchedMovie.getCinematographer());
            assertEquals(100000000, fetchedMovie.getBudget());
            assertEquals("USA", fetchedMovie.getCountry());
            assertEquals(Arrays.asList(1, 2, 5), fetchedMovie.getActorIds());
            assertEquals(Arrays.asList(1, 2, 3, 5), fetchedMovie.getGenreIds());
            assertEquals(generatedId, fetchedMovie.getId());
        } catch (SQLException e) {
            fail("SQLException:" + e.getMessage());
        }
    }



    /** Tests reading for a pre-existing Movie from the database. */
    @Test
    @DisplayName("Test reading a pre-existing movie from the database")
    void testRead() {
        int movieId = 2;  // The Wolf of Wall Street
        try {
            Movie retrievedMovie = dao.read(movieId);

            assertNotNull(retrievedMovie); // Confirm that the movie has been added to the database
            assertEquals(movieId, retrievedMovie.getId());
            assertEquals("The Wolf of Wall Street", retrievedMovie.getTitle());
            assertEquals(2013, retrievedMovie.getReleaseYear());
            assertEquals("Martin Scorsese", retrievedMovie.getDirector());

            // Check that the actors and genres are properly linked
            List<Integer> expectedActorIds = Arrays.asList(5, 6); // Leonardo Di Caprio
            List<Integer> expectedGenreIds = Arrays.asList(3, 5); // Comedy, Drama
            assertEquals(expectedActorIds, retrievedMovie.getActorIds());
            assertEquals(expectedGenreIds, retrievedMovie.getGenreIds());
        } catch (SQLException e) {
            fail("SQLException:" + e.getMessage());
        }
    }


    @Test
    @DisplayName("Test updating of a pre-existing movie from the database")
    void testUpdate() {
        try {
            // Fetch the original movie
            Movie originalMovie = dao.read(3);  // Django Unchained
            int originalMovieId = originalMovie.getId();

            // Define new attributes for the movie
            String newTitle = "Django The Movie";
            int newReleaseYear = 2023;
            String newDirector = "Aki Kaurismäki";
            String newWriter = "New Writer";
            String newProducer = "New Producer";
            String newCinematographer = "New Cinematographer";
            int newBudget = 1000000;
            String newCountry = "Finland";
            List<Integer> newActorIds = Arrays.asList(1, 5); // Robert De Niro, Margot Robbie
            List<Integer> newGenreIds = Arrays.asList(6, 7); // Fantasy, Historical

            // Updated movie object with new attributes
            Movie updatedMovie = new Movie(newTitle, newReleaseYear, newDirector, newWriter, newProducer,
                    newCinematographer, newBudget, newCountry, newActorIds, newGenreIds);
            updatedMovie.setId(originalMovieId); // Link updated object to existing record

            assertTrue(dao.update(updatedMovie)); // Update the movie to database and confirm it

            // Check that the update was correctly implemented
            Movie fetchedUpdatedMovie = dao.read(originalMovieId);
            assertEquals(newTitle, fetchedUpdatedMovie.getTitle());
            assertEquals(newReleaseYear, fetchedUpdatedMovie.getReleaseYear());
            assertEquals(newDirector, fetchedUpdatedMovie.getDirector());
            assertEquals(newWriter, fetchedUpdatedMovie.getWriter());
            assertEquals(newProducer, fetchedUpdatedMovie.getProducer());
            assertEquals(newCinematographer, fetchedUpdatedMovie.getCinematographer());
            assertEquals(newBudget, fetchedUpdatedMovie.getBudget());
            assertEquals(newCountry, fetchedUpdatedMovie.getCountry());
            assertEquals(newActorIds, fetchedUpdatedMovie.getActorIds());
            assertEquals(newGenreIds, fetchedUpdatedMovie.getGenreIds());
        } catch (SQLException e) {
            fail("SQLException:" + e.getMessage());
        }
    }


    @Test
    @DisplayName("Test deletion of pre-existing movies from the database")
    void testDelete() {
        try {
            int movieIdToDelete1 = 1;  // Inception
            int movieIdToDelete2 = 4;  // The Deer Hunter
            dao.delete(movieIdToDelete1);
            dao.delete(movieIdToDelete2);

            // Try to fetch the deleted movies (should return null)
            assertNull(dao.read(movieIdToDelete1));
            assertNull(dao.read(movieIdToDelete2));
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
}
