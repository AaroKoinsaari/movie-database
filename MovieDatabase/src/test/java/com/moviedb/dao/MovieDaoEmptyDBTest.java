package com.moviedb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.moviedb.database.EmptyDBSetup;
import com.moviedb.models.Movie;

/**
 * This class contains unit tests for the MovieDao class using an empty database setup.
 * Only genres are existing in the empty database since they are static.
 * Each test method is designed to test a single functionality of the MovieDao class.
 */
class MovieDaoEmptyDBTest extends EmptyDBSetup {

    private MovieDao dao;

    /**
     * Additional setup for the empty database for each test.
     */
    @BeforeEach
    public void setUp() {
        dao = new MovieDao(connection);
        addActorsToDB(10);  // Add 10 test actors to database
    }


    @Test
    void createTest() {
        // Create new test movie
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        List<Integer> actorIds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> genreIds = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        Movie testMovie = new Movie(title, releaseYear, director, actorIds, genreIds);

        MovieDao dao = new MovieDao(connection);
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


    @Test
    void readTest() {
        int nonExistentMovieId = 99;
        Movie fetchedMovie = dao.read(nonExistentMovieId);
        assertNull(fetchedMovie, "Should return null for trying to read a non-existent movie");
    }


    @Test
    void updateTest() {
        int nonExistentMovieId = 99;

        // Create non-existent movie
        String updatedTitle = "Updated Movie";
        int updatedReleaseYear = 2023;
        String updatedDirector = "Updated Director";
        List<Integer> updatedActorIds = Arrays.asList(3, 4, 5);
        List<Integer> updatedGenreIds = Arrays.asList(2, 3);
        Movie updatedMovie = new Movie(updatedTitle, updatedReleaseYear, updatedDirector, updatedActorIds, updatedGenreIds);
        updatedMovie.setId(nonExistentMovieId);

        // Try update and assert that the update fails
        boolean updateResult = dao.update(updatedMovie);
        assertFalse(updateResult, "Should return false for trying to update a non-existent movie");
    }


    @Test
    void deleteTest() {
        int nonExistentMovieId = 99;

        // Try deleting non-existent movie and assert that the deletion fails
        boolean isDeleted = dao.delete(nonExistentMovieId);
        assertFalse(isDeleted, "Should return false for trying to delete a non-existent movie");
    }
}
