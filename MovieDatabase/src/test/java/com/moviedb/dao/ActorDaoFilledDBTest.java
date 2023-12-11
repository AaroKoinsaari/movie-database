package com.moviedb.dao;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Actor;


/**
 * This class contains unit tests for the ActorDao class using a pre-filled H2 database setup.
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


    @Test
    @DisplayName("Create a new actor in the database and verify")
    void testCreate() {
        String name = "New Actor";
        Actor newActor = new Actor(name);

        // Insert new actor to database and fetch it
        int actorId = assertDoesNotThrow(() -> dao.create(newActor),
                "Creating a new actor should not throw SQLException");

        Optional<Actor> fetchedActor = assertDoesNotThrow(() -> dao.read(actorId),
                "Reading the created actor should not throw SQLException");

        assertTrue(fetchedActor.isPresent(), "Actor should be present");
        fetchedActor.ifPresent(actor -> {
            assertEquals(name, actor.getName(), "Actor's name should match");
            assertEquals(actorId, actor.getId(), "Actor's ID should match");
        });
    }


    @Test
    @DisplayName("Read an existing actor in the database")
    void testReadExistingActor() {
        Optional<Actor> fetchedExistingActor = assertDoesNotThrow(() -> dao.read(6),
                "Reading an existing actor should not throw SQLException");
        assertTrue(fetchedExistingActor.isPresent(), "Actor should be present");
        fetchedExistingActor.ifPresent(actor -> {
            assertEquals("Leonardo Di Caprio", actor.getName(), "Actor name should match");
            assertEquals(6, actor.getId(), "Actor ID should match");
        });
    }


    @Test
    @DisplayName("Read a non-existing actor from the database")
    void testReadNonExistingActor() {
        Optional<Actor> fetchedNonExistentActor = assertDoesNotThrow(() -> dao.read(99),
                "Reading a non-existent actor should not throw SQLException");
        assertTrue(fetchedNonExistentActor.isEmpty(), "Actor should not be present");
    }


    @Test
    @DisplayName("Read a newly inserted actor from the database")
    void testReadNewInsertedActor() {
        String name = "Test Actor 1";
        Actor testActor = new Actor(name);
        int actorId = assertDoesNotThrow(() -> dao.create(testActor),
                "Creating new actor should not throw SQLException");

        Optional<Actor> foundActor = assertDoesNotThrow(() -> dao.read(actorId),
                "Reading the newly created actor should not throw SQLException");

        assertTrue(foundActor.isPresent(), "Actor should be present");
        foundActor.ifPresent(actor -> {
            assertEquals(name, actor.getName(), "Actor name should match");
            assertEquals(actorId, actor.getId(), "Actor ID should match");
        });
    }


    @Test
    @DisplayName("Update an existing actor in the database")
    void testUpdate() {
        int actorId = 2;  // Meryl Streep
        String updatedName = "Test Name";

        Optional<Actor> actorOptional = assertDoesNotThrow(() -> dao.read(actorId),
                "Reading an existing actor should not throw SQLException");
        assertTrue(actorOptional.isPresent(), "Actor should be in the database");

        Actor actorToUpdate = actorOptional.get();
        actorToUpdate.setName(updatedName);

        boolean updateSuccessful = assertDoesNotThrow(() -> dao.update(actorToUpdate),
                "Updating the actor should not throw SQLException");
        assertTrue(updateSuccessful, "Actor update should be successful");

        Optional<Actor> updatedOptionalActor = assertDoesNotThrow(() -> dao.read(actorId),
                "Reading the updated actor should not throw SQLException");
        assertTrue(updatedOptionalActor.isPresent(), "Updated actor should still be in the database");
        Actor updatedActor = updatedOptionalActor.get();
        assertEquals(updatedName, updatedActor.getName(), "Actor's name should be updated in the database");
    }
}
