package com.moviedb.dao;

import com.moviedb.database.EmptyDBSetup;
import com.moviedb.models.Movie;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDaoEmptyDBTest extends EmptyDBSetup {

    @Test
    void create() {
        // Create new test movie
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        List<Integer> actorIds = new ArrayList<>();
        List<Integer> genreIds = new ArrayList<>();
        actorIds.addAll(Arrays.asList(1, 2, 3, 4, 5));
        genreIds.addAll(Arrays.asList(1, 2, 3, 4));
        Movie testMovie = new Movie(title, releaseYear, director, actorIds, genreIds);

        MovieDao dao = new MovieDao();
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
    void read() {
        // Create new test movie
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        List<Integer> actorIds = new ArrayList<>();
        List<Integer> genreIds = new ArrayList<>();
        actorIds.addAll(Arrays.asList(1, 2, 3));
        genreIds.addAll(Arrays.asList(1, 2));
        Movie testMovie = new Movie(title, releaseYear, director, actorIds, genreIds);

        MovieDao dao = new MovieDao();
        int generatedId = dao.create(testMovie);

        // Read the movie from the database
        Movie fetchedMovie = dao.read(generatedId);

        // Confirm that the fetched movie information is correct
        assertEquals(title, fetchedMovie.getTitle());
        assertEquals(releaseYear, fetchedMovie.getReleaseYear());
        assertEquals(director, fetchedMovie.getDirector());
        assertEquals(actorIds, fetchedMovie.getActorIds());
        assertEquals(genreIds, fetchedMovie.getGenreIds());
        assertEquals(generatedId, fetchedMovie.getId());
    }


    @Test
    void update() {
        // Create new test movie
        String originalTitle = "Original Movie";
        int originalReleaseYear = 2020;
        String originalDirector = "Original Director";
        List<Integer> originalActorIds = Arrays.asList(1, 2, 3);
        List<Integer> originalGenreIds = Arrays.asList(1, 2);
        Movie originalMovie = new Movie(originalTitle, originalReleaseYear, originalDirector, originalActorIds, originalGenreIds);

        MovieDao dao = new MovieDao();
        int movieId = dao.create(originalMovie);

        // Update the test movie information
        String updatedTitle = "Updated Movie";
        int updatedReleaseYear = 2023;
        String updatedDirector = "Updated Director";
        List<Integer> updatedActorIds = Arrays.asList(3, 4, 5);
        List<Integer> updatedGenreIds = Arrays.asList(2, 3);
        Movie updatedMovie = new Movie(updatedTitle, updatedReleaseYear, updatedDirector, updatedActorIds, updatedGenreIds);
        updatedMovie.setId(movieId);

        boolean updateResult = dao.update(updatedMovie);
        assertTrue(updateResult);

        // Read the updated movie from the database
        Movie fetchedUpdatedMovie = dao.read(movieId);

        // Confirm that the fetched movie information has been updated
        assertEquals(updatedTitle, fetchedUpdatedMovie.getTitle());
        assertEquals(updatedReleaseYear, fetchedUpdatedMovie.getReleaseYear());
        assertEquals(updatedDirector, fetchedUpdatedMovie.getDirector());
        assertEquals(updatedActorIds, fetchedUpdatedMovie.getActorIds());
        assertEquals(updatedGenreIds, fetchedUpdatedMovie.getGenreIds());
    }


    @Test
    void delete() {
        // Create new movie to the database
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        List<Integer> actorIds = new ArrayList<>();
        List<Integer> genreIds = new ArrayList<>();
        actorIds.addAll(Arrays.asList(1, 2, 3));
        genreIds.addAll(Arrays.asList(1, 2));
        Movie testMovie = new Movie(title, releaseYear, director, actorIds, genreIds);

        MovieDao dao = new MovieDao();
        int generatedId = dao.create(testMovie);

        // Confirm that the movie is in the database
        Movie fetchedMovie = dao.read(generatedId);
        assertNotNull(fetchedMovie);

        // Try to delete the movie
        boolean isDeleted = dao.delete(generatedId);
        assertTrue(isDeleted);

        // Confirm that the deleted movie is no longer in the database
        Movie deletedMovie = dao.read(generatedId);
        assertNull(deletedMovie);
    }
}
