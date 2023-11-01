package com.moviedb.dao;


import java.util.ArrayList;
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

        // Add some existing actors and genres from the test db
        List<Integer> actorIds = new ArrayList<>();
        List<Integer> genreIds = new ArrayList<>();
        actorIds.add(2);  // Leonardo Di Caprio
        actorIds.add(3);  // Cate Blanchett
        genreIds.add(3);  // Crime
        genreIds.add(9);  // Mystery

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
    }
}
