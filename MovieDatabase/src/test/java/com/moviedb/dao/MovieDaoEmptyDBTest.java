package com.moviedb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.moviedb.database.EmptyDBSetup;
import com.moviedb.models.Movie;


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
    @DisplayName("Test creating and fetching a movie in the database")
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
        int generatedId = assertDoesNotThrow(() -> dao.create(testMovie),
                "Creation of the movie should not throw SQLException");

        assertTrue(generatedId > 0, "The generated ID should be greater than 0 to confirm database insertion");

        // Fetch the created movie and test that the values are correct
        Movie fetchedMovie = assertDoesNotThrow(() -> dao.read(generatedId),
                "Reading the movie should not throw SQLException");

        assertNotNull(fetchedMovie, "Fetched movie should not be null");
        assertEquals(title, fetchedMovie.getTitle());
        assertEquals(releaseYear, fetchedMovie.getReleaseYear());
        assertEquals(director, fetchedMovie.getDirector());
        assertEquals(actorIds, fetchedMovie.getActorIds());
        assertEquals(genreIds, fetchedMovie.getGenreIds());
        assertEquals(generatedId, fetchedMovie.getId());
    }


    @Test
    @DisplayName("Test reading a non-existent movie from the database")
    void testRead() {
        int nonExistentMovieId = 99;

        Movie fetchedMovie = assertDoesNotThrow(() -> dao.read(nonExistentMovieId),
                "Reading a non-existent movie should not throw SQLException");

        assertNull(fetchedMovie, "Should return null for trying to read a non-existent movie");
    }


    @Test
    @DisplayName("Test updating a non-existent movie in the database")
    void testUpdate() {
        int nonExistentMovieId = 99;

        // Create non-existent movie
        String updatedTitle = "Updated Movie";
        Movie updatedMovie = getUpdatedMovie(updatedTitle);
        updatedMovie.setId(nonExistentMovieId);

        // Try update and assert that the update fails
        boolean updateResult = assertDoesNotThrow(() -> dao.update(updatedMovie),
                "Updating a non-existent movie should not throw SQLException");

        assertFalse(updateResult, "Should return false for trying to update a non-existent movie");
    }


    private static Movie getUpdatedMovie(String updatedTitle) {
        int updatedReleaseYear = 2023;
        String updatedDirector = "Updated Director";
        String updaterWriter = "Updated Writer";
        String updatedProducer = "Updated Producer";
        String updatedCinematographer = "Updated Cinematographer";
        int updatedBudget = 10000;
        String updatedCountry = "Switzerland";
        List<Integer> updatedActorIds = Arrays.asList(3, 4, 5);
        List<Integer> updatedGenreIds = Arrays.asList(2, 3);
        return new Movie(updatedTitle, updatedReleaseYear, updatedDirector, updaterWriter,
                updatedProducer, updatedCinematographer, updatedBudget, updatedCountry, updatedActorIds, updatedGenreIds);
    }


    @Test
    @DisplayName("Test deleting a non-existent movie from the database")
    void testDelete() {
        int nonExistentMovieId = 99;

        // Try deleting non-existent movie and assert that the deletion fails
        boolean isDeleted = assertDoesNotThrow(() -> dao.delete(nonExistentMovieId),
                "Deleting a non-existent movie should not throw SQLException");

        assertFalse(isDeleted, "Should return false for trying to delete a non-existent movie");
    }
}
