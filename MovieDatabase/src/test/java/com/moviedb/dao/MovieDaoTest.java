package com.moviedb.dao;

import com.moviedb.database.MoviedatabaseTest;
import com.moviedb.models.Movie;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDaoTest extends MoviedatabaseTest {

    @Test
    void create() {
        // Create new test movie
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        List<Integer> actorIds = new ArrayList<>();
        List<Integer> genreIds = new ArrayList<>();
        actorIds.addAll(Arrays.asList(1, 2, 3, 4, 5));
        genreIds.addAll(Arrays.asList(1, 2, 3, 4, 5));
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
    }

    @Test
    void read() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}