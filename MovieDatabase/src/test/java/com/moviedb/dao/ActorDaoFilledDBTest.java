package com.moviedb.dao;

import java.sql.SQLException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Actor;

/**
 * This class contains unit tests for the ActorDao class using a pre-filled database setup.
 * It operates under the assumption that certain movies, genres, and actors already exist in the database,
 * with known IDs that are used within the tests. These IDs are annotated within the test methods.
 * Each test method is designed to test a single functionality of the ActorDao class, verifying
 * the expected behavior against the known state of the database.
 */
public class ActorDaoFilledDBTest extends FilledDBSetup {
    private ActorDao dao;  // Instance of ActorDao used across all test cases

    /**
     * Additional setup for the filled database for each test.
     * Initializes the connection to the test database for each test.
     */
    @BeforeEach
    void setUp() {
        dao = new ActorDao(connection);
    }


    /** Tests the creation of a new Actor in the database. */
    @Test
    void createTest() {
        String name = "New Actor";
        Actor newActor = new Actor(name);

        // Insert new actor to database and fetch it
        int actorId = dao.create(newActor);
        Optional<Actor> fetchedActor = dao.read(actorId);

        // Assert that the retrieved Optional contains the actor
        assertTrue(fetchedActor.isPresent(), "Actor should be present");

        // If the actor is found, assert that the actor's properties match those of the test actor
        fetchedActor.ifPresent(actor -> {
            assertEquals(name, actor.getName(), "Actor's name should match");
            assertEquals(actorId, actor.getId(), "Actor's ID should match");
        });
    }


    /** Tests the reading of an existing Actor in the database. */
    @Test
    void readExistingActorTest() {
        Optional<Actor> fetchedExistingActor = dao.read(6);  // Leonardo Di Caprio
        assertTrue(fetchedExistingActor.isPresent(), "Actor should be present");
        fetchedExistingActor.ifPresent(actor -> {
            assertEquals("Leonardo Di Caprio", actor.getName(), "Actor name should match");
            assertEquals(6, actor.getId(), "Actor ID should match");
        });
    }


    /** Tests the reading of a non-existing Actor in the database. */
    @Test
    void readNonExistingActorTest() {
        Optional<Actor> fetchedNonExistentActor = dao.read(99);
        assertTrue(fetchedNonExistentActor.isEmpty(), "Actor should not be present");
    }


    /** Tests the reading of a new Actor in the database. */
    @Test
    void readNewInsertedActorTest() {
        String name = "Test Actor 1";
        Actor testActor = new Actor(name);
        int actorId = dao.create(testActor);
        Optional<Actor> foundActor = dao.read(actorId);

        assertTrue(foundActor.isPresent(), "Actor should be present");
        foundActor.ifPresent(actor -> {
            assertEquals(name, actor.getName(), "Actor name should match");
            assertEquals(actorId, actor.getId(), "Actor ID should match");
        });
    }


    /** Tests updating of an existing Actor in the database. */
    @Test
    void updateTest() {
        int actorId = 2;  // Meryl Streep
        String updatedName = "Test Name";

        // Fetch the actor from database
        Optional<Actor> actorOptional = dao.read(actorId);
        assertTrue(actorOptional.isPresent(), "Actor should be in the database");

        // Update the fetched actor
        Actor actorToUpdate = actorOptional.get();
        actorToUpdate.setName(updatedName);

        // Assert that the update was successful
        boolean updateSuccessful = dao.update(actorToUpdate);
        assertTrue(updateSuccessful, "Actor update should be successful");

        // Confirm the the update worked correctly
        Optional<Actor> updatedOptionalActor = dao.read(actorId);
        assertTrue(updatedOptionalActor.isPresent(), "Updated actor should still be in the database");
        Actor updatedActor = updatedOptionalActor.get();
        assertEquals(updatedName, updatedActor.getName(), "Actor's name should be updated in the database");
    }


    /** Tests the deletion of an Actor that is not linked to any movie in the database. */
    @Test
    void deleteNonLinkedActorTest() throws SQLException {
        int newActorId = dao.create(new Actor("Test Actor"));
        assertTrue(dao.delete(newActorId));
    }


    /** Tests the deletion of an Actor that is linked to one or more movies in the database. */
    @Test
    void deleteLinkedActorTest() {
        int actorId = 5;  //  Margot Robbie

        // Assert that SQLException is thrown when trying to delete actor that is linked to a movie
        assertThrows(SQLException.class, () -> dao.delete(actorId), "Should throw an exception, but didn't");
    }


    /**
     * Tests the deletion of an Actor that is linked to one or more movies in the database
     * after deleting the movie in which the actor was linked.
     * @throws SQLException If there's an error during the database operation.
     */
    @Test
    void deleteLinkedActorAfterDeletingMovieTest() throws SQLException {
        // Delete movie where actor is linked
        MovieDao movieDao = new MovieDao(connection);
        movieDao.delete(3);  // Django Unchained

        // Delete the actor and assert that deletion was successful
        int actorId = 3;  // Jamie Foxx
        assertTrue(dao.delete(actorId));
    }
}
