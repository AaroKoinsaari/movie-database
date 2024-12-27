/*
 * Copyright (c) 2023-2024 Aaro Koinsaari
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.moviedb.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * This class contains unit tests for the MovieDao class using a pre-filled H2 database setup.
 */
public class MovieDaoFilledDBTest extends FilledDBSetup {

    private MovieDao dao;


    /**
     * Additional setup for the filled database for each test.
     * Initializes the connection to the test database for each test.
     */
    @BeforeEach
    public void setUp() {
        dao = new MovieDao(connection);
    }


    @Test
    @DisplayName("Create a new movie and verify it in the database")
    void testCreate() {
        // Create new test movie with additional parameters
        String title = "Test Movie";
        int releaseYear = 2023;
        String director = "Test Director";
        String writer = "Test Writer";
        String producer = "Test Producer";
        String cinematographer = "Test Cinematographer";
        int budget = 100;
        String country = "Finland";
        List<Integer> actorIds = new ArrayList<>(Arrays.asList(1, 2, 5));  // RDN, MS, MR
        List<Integer> genreIds = new ArrayList<>(Arrays.asList(1, 2, 3, 5));  // Action, Adventure, Comedy, Drama
        Movie testMovie = new Movie(title, releaseYear, director, writer, producer, cinematographer, budget, country, actorIds, genreIds);

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
    @DisplayName("Read a pre-existing movie from the database and verify its details")
    void testRead() {
        int movieId = 2;  // The Wolf of Wall Street

        Movie retrievedMovie = assertDoesNotThrow(() -> dao.read(movieId),
                "Reading the movie should not throw SQLException");

        assertNotNull(retrievedMovie, "Retrieved movie should not be null");
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
    @DisplayName("Update a pre-existing movie in the database and verify the changes")
    void testUpdate() {
        // Fetch the original movie
        Movie originalMovie = assertDoesNotThrow(() -> dao.read(3),
                "Reading the original movie should not throw SQLException");

        int originalMovieId = originalMovie.getId();

        // Define new attributes for the movie
        String newTitle = "Django The Movie";
        int newReleaseYear = 2023;
        String newDirector = "Aki Kaurismäki";
        String newWriter = "Updated Writer";
        String newProducer = "Updated Producer";
        String newCinematographer = "Updated Cinematographer";
        int newBudget = 150000;
        String newCountry = "Finland";
        List<Integer> newActorIds = Arrays.asList(1, 5); // Robert De Niro, Margot Robbie
        List<Integer> newGenreIds = Arrays.asList(6, 7); // Fantasy, Historical

        // Updated movie object with new attributes
        Movie updatedMovie = new Movie(newTitle, newReleaseYear, newDirector, newWriter, newProducer,
                newCinematographer, newBudget, newCountry, newActorIds, newGenreIds);
        updatedMovie.setId(originalMovieId); // Link updated object to existing record

        assertDoesNotThrow(() -> assertTrue(dao.update(updatedMovie)),
                "Updating the movie should not throw SQLException");

        // Check that the update was correctly implemented
        Movie fetchedUpdatedMovie = assertDoesNotThrow(() -> dao.read(originalMovieId),
                "Reading the updated movie should not throw SQLException");

        assertEquals(newTitle, fetchedUpdatedMovie.getTitle());
        assertEquals(newReleaseYear, fetchedUpdatedMovie.getReleaseYear());
        assertEquals(newDirector, fetchedUpdatedMovie.getDirector());
        assertEquals(newActorIds, fetchedUpdatedMovie.getActorIds());
        assertEquals(newGenreIds, fetchedUpdatedMovie.getGenreIds());
    }


    @Test
    @DisplayName("Delete movies from the database and verify they are removed")
    void testDelete() {
        int movieIdToDelete1 = 1;  // Inception
        int movieIdToDelete2 = 4;  // The Deer Hunter

        assertDoesNotThrow(() -> dao.delete(movieIdToDelete1),
                "Deleting movie 1 should not throw SQLException");
        assertDoesNotThrow(() -> dao.delete(movieIdToDelete2),
                "Deleting movie 2 should not throw SQLException");

        // Try to fetch deleted movies (should return null)
        Movie deletedMovie1 = assertDoesNotThrow(() -> dao.read(movieIdToDelete1),
                "Reading deleted movie 1 should not throw SQLException");
        Movie deletedMovie2 = assertDoesNotThrow(() -> dao.read(movieIdToDelete2),
                "Reading deleted movie 2 should not throw SQLException");

        assertNull(deletedMovie1, "Deleted movie 1 should not be found in the database");
        assertNull(deletedMovie2, "Deleted movie 2 should not be found in the database");
    }
}
