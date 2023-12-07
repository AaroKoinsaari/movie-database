package com.moviedb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.moviedb.database.EmptyDBSetup;
import com.moviedb.models.Movie;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This class contains unit tests for the MovieDao class using an empty database setup.
 * Only genres are existing in the empty database since they are static.
 * Each test method is designed to test a single functionality of the MovieDao class.
 */
class MovieDaoEmptyDBTest extends EmptyDBSetup {
    private MovieDao dao;  // Instance of MovieDao used across all test cases

    /**
     * Additional setup for the empty database for each test.
     * Initializes the connection to the test database for each test.
     */
    @BeforeEach
    public void setUp() {
        dao = new MovieDao(connection);
    }


    @Test
    @DisplayName("Test creating a new movie in the database")
    void testCreate() {
        addActorsToDB(10);  // Add 10 test actors to database

        // Create a new test movie
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        String writer = "Test Writer";
        String producer = "Test Producer";
        String cinematographer = "Test Cinematographer";
        int budget = 100;
        String country = "Finland";
        List<Integer> actorIds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> genreIds = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        Movie testMovie = new Movie(title, releaseYear, director, writer, producer,
                cinematographer, budget, country, actorIds, genreIds);

        MovieDao dao = new MovieDao(connection);
        int generatedId = 0;

        // Test the creation of the movie
        try {
            generatedId = dao.create(testMovie);
        } catch (SQLException e) {
            fail("SQLException thrown during movie creation: " + e.getMessage());
        }

        assertTrue(generatedId > 0, "Movie should be created with a valid ID");

        // Test fetching the created movie
        Movie fetchedMovie = null;
        try {
            fetchedMovie = dao.read(generatedId);
        } catch (SQLException e) {
            fail("SQLException thrown during movie fetching: " + e.getMessage());
        }

        assertNotNull(fetchedMovie, "Fetched movie should not be null");
        assertEquals(title, fetchedMovie.getTitle());
        assertEquals(releaseYear, fetchedMovie.getReleaseYear());
        assertEquals(director, fetchedMovie.getDirector());
        assertEquals(writer, fetchedMovie.getWriter());
        assertEquals(producer, fetchedMovie.getProducer());
        assertEquals(cinematographer, fetchedMovie.getCinematographer());
        assertEquals(budget, fetchedMovie.getBudget());
        assertEquals(country, fetchedMovie.getCountry());
        assertEquals(actorIds, fetchedMovie.getActorIds());
        assertEquals(genreIds, fetchedMovie.getGenreIds());
        assertEquals(generatedId, fetchedMovie.getId());
    }


    @Test
    @DisplayName("Test reading a non-existent movie from the database returns null")
    void testRead() {
        int nonExistentMovieId = 99;
        Movie fetchedMovie = null;

        try {
            fetchedMovie = dao.read(nonExistentMovieId);
        } catch (SQLException e) {
            fail("SQLException thrown while reading a non-existent movie: " + e.getMessage());
        }

        assertNull(fetchedMovie, "Should return null for trying to read a non-existent movie");
    }


    @Test
    @DisplayName("Test updating non-existent movie returns false")
    void testUpdate() {
        int nonExistentMovieId = 99;

        // Create a movie object representing a non-existent movie
        String updatedTitle = "Updated Movie";
        int updatedReleaseYear = 2023;
        String updatedDirector = "Updated Director";
        String updatedWriter = "Updated Writer";
        String updatedProducer = "Updated Producer";
        String updatedCinematographer = "Updated Cinematographer";
        int updatedBudget = 150;
        String updatedCountry = "Test Country";
        List<Integer> updatedActorIds = Arrays.asList(3, 4, 5);
        List<Integer> updatedGenreIds = Arrays.asList(2, 3);
        Movie updatedMovie = new Movie(updatedTitle, updatedReleaseYear, updatedDirector, updatedWriter,
                updatedProducer, updatedCinematographer, updatedBudget,
                updatedCountry, updatedActorIds, updatedGenreIds);
        updatedMovie.setId(nonExistentMovieId);

        // Try to update and assert that the update fails
        boolean updateResult = false;
        try {
            updateResult = dao.update(updatedMovie);
        } catch (SQLException e) {
            fail("SQLException thrown during update operation: " + e.getMessage());
        }
        assertFalse(updateResult, "Should return false for trying to update a non-existent movie");
    }


    @Test
    @DisplayName("Test deleting a non-existent movie returns false")
    void testDelete() {
        int nonExistentMovieId = 99;

        // Try to delete a non-existent movie and assert that the deletion returns false
        boolean isDeleted = false;
        try {
            isDeleted = dao.delete(nonExistentMovieId);
        } catch (SQLException e) {
            fail("SQLException thrown during delete operation: " + e.getMessage());
        }

        assertFalse(isDeleted, "Should return false for trying to delete a non-existent movie");
    }
}
