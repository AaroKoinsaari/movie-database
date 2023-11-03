package com.moviedb.dao;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Movie;

public class MovieDaoFilledDBTest extends FilledDBSetup {

    @Test
    void create() {
        // Create new test movie
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        List<Integer> actorIds = new ArrayList<>();
        List<Integer> genreIds = new ArrayList<>();
        actorIds.addAll(Arrays.asList(1, 2, 5));  // RDN, MS, MR
        genreIds.addAll(Arrays.asList(1, 2, 3, 5));  // Action, adventure, comedy, drama
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
    void read() {
        MovieDao dao = new MovieDao(connection);
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


    @Test
    void update() {
        // Fetch the original movie
        MovieDao dao = new MovieDao(connection);
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
}
