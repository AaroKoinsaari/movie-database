package com.moviedb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Movie;


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


    /** Tests the creation of a new movie in the database. */
    @Test
    void createTest() {
        // Create new test movie
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        List<Integer> actorIds = new ArrayList<>(Arrays.asList(1, 2, 5));  // RDN, MS, MR
        List<Integer> genreIds = new ArrayList<>(Arrays.asList(1, 2, 3, 5));  // Action, adventure, comedy, drama
        Movie testMovie = new Movie(title, releaseYear, director, actorIds, genreIds);

        int generatedId = dao.create(testMovie);

        assertTrue(generatedId > 0);  // Confirm that the movie has been added to the database

        // Fetch the created movie and test that the values are correct
        Movie fetchedMovie = dao.read(generatedId);
        assertEquals(title, fetchedMovie.getTitle());
        assertEquals(releaseYear, fetchedMovie.getReleaseYear());
        assertEquals(director, fetchedMovie.getDirector());
        assertEquals(actorIds, fetchedMovie.getActorIds());
        assertEquals(genreIds, fetchedMovie.getGenreIds());
        assertEquals(generatedId, fetchedMovie.getId());
    }


    /** Tests reading for a pre-existing Movie from the database. */
    @Test
    void readTest() {
        int movieId = 2;  // The Wolf of Wall Street
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
    }


    /** Tests updating of a pre-existing Movie from the database. */
    @Test
    void updateTest() {
        // Fetch the original movie
        Movie originalMovie = dao.read(3);  // Django Unchained

        int originalMovieId = originalMovie.getId();

        // Define new attributes for the movie
        String newTitle = "Django The Movie";
        int newReleaseYear = 2023;
        String newDirector = "Aki Kaurismäki";
        List<Integer> newActorIds = Arrays.asList(1, 5); // Robert De Niro, Margot Robbie
        List<Integer> newGenreIds = Arrays.asList(6, 7); // Fantasy, Historical

        // Updated movie object with new attributes
        Movie updatedMovie = new Movie(newTitle, newReleaseYear, newDirector, newActorIds, newGenreIds);
        updatedMovie.setId(originalMovieId); // Link updated object to existing record

        assertTrue(dao.update(updatedMovie)); // Update the movie to database and confirm it

        // Check that the update was correctly implemented
        Movie fetchedUpdatedMovie = dao.read(originalMovieId);
        assertEquals(newTitle, fetchedUpdatedMovie.getTitle());
        assertEquals(newReleaseYear, fetchedUpdatedMovie.getReleaseYear());
        assertEquals(newDirector, fetchedUpdatedMovie.getDirector());
        assertEquals(newActorIds, fetchedUpdatedMovie.getActorIds());
        assertEquals(newGenreIds, fetchedUpdatedMovie.getGenreIds());
    }


    /** Tests deletion of pre-existing Movies from the database. */
    @Test
    void deleteTest() {
        int movieIdToDelete1 = 1;  // Inception
        int movieIdToDelete2 = 4;  // The Deer Hunter
        dao.delete(movieIdToDelete1);
        dao.delete(movieIdToDelete2);

        // Try to fetch deleted movies (should return null)
        Movie deletedMovie1 = dao.read(movieIdToDelete1);
        Movie deletedMovie2 = dao.read(movieIdToDelete2);

        assertNull(deletedMovie1);
        assertNull(deletedMovie2);
    }
}
