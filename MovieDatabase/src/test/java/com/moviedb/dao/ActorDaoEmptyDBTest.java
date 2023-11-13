package com.moviedb.dao;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.moviedb.database.EmptyDBSetup;
import com.moviedb.models.Actor;


/**
 * This class contains unit tests for the ActorDao class using an empty database setup.
 * Only genres are existing in the empty database since they are static.
 * Each test method is designed to test a single functionality of the ActorDao class.
 */
public class ActorDaoEmptyDBTest extends EmptyDBSetup {
    private ActorDao dao;  // Instance of ActorDao used across all test cases

    /**
     * Additional setup for the empty database for each test.
     * Initializes the connection to the test database for each test.
     */
    @BeforeEach
    void setUp() {
        dao = new ActorDao(connection);
    }


    /** Tests the creation of a new Actor in the database. */
    @Test
    void createTest() {
        String actorName1 = "Test Actor 1";
        String actorName2 = "Test Actor 2";

        // Create actors in the database and return the generated ID
        int actorId1 = dao.create(new Actor(actorName1));
        int actorId2 = dao.create(new Actor(actorName2));

        // Read the created actors from the database
        Optional<Actor> foundActor1 = dao.read(actorId1);
        Optional<Actor> foundActor2 = dao.read(actorId2);

        // Confirm that the actors are found and compare them to them to the created objects
        assertTrue(foundActor1.isPresent(), "Actor 1 should be present");
        assertTrue(foundActor2.isPresent(), "Actor 2 should be present");
        assertEquals(actorName1, foundActor1.get().getName(), "Actor 1's name should match");
        assertEquals(actorName2, foundActor2.get().getName(), "Actor 2's name should match");

        // Assert the IDs
        assertEquals(actorId1, foundActor1.get().getId(), "Actor 1's id should match");
        assertEquals(actorId2, foundActor2.get().getId(), "Actor 2's id should match");
    }


    /** Tests the reading of a non-existing Actor from the database. */
    @Test
    void readTest() {
        int nonExistentActorId = 99;
        Optional<Actor> fetchedActor = dao.read(nonExistentActorId);

        // Assert that the Optional is empty, indicating no actor found
        assertTrue(fetchedActor.isEmpty(), "Should return null for trying to read a non-existent actor");
    }


    /** Tests updating a non-existent Actor in the database. */
    @Test
    void updateTest() {
        // Attempt to update a non-existent actor
        String updatedName = "Updated Actor";
        int nonExistentActorId = 99;
        Actor actor = new Actor(updatedName);
        actor.setId(nonExistentActorId);

        // Perform the update and assert that it was unsuccessful
        boolean updateSuccessful = dao.update(actor);
        assertFalse(updateSuccessful, "Should return null for trying to update a non-existent actor");

        // Attempt to retrieve the non-existent actor from the database and assert that it's empty
        Optional<Actor> updatedActorOpt = dao.read(nonExistentActorId);
        assertTrue(updatedActorOpt.isEmpty(), "Non-existent actor should not be present after attempted update");
    }


    /** Tests reading all non-existent Actors in the database. */
    @Test
    void readAllTest() {
        // Read all actors from empty database and assert that the list is empty
        List<Actor> actors = dao.readAll();
        assertTrue(actors.isEmpty());
    }


    /** Tests getting a non-existent Actor by ID in the database. */
    @Test
    void getActorByIdTest() {
        // Attempt to retrieve an actor by ID from an empty database and assert the result is empty Optional
        Optional<Actor> result = dao.getActorById(1);
        assertFalse(result.isPresent(), "No actors should be found by id in an empty database");
    }


    /** Tests getting a non-existent Actor by name in the database. */
    @Test
    void getActorByNameTest() {
        Optional<Actor> result = dao.getActorByName("Test Actor");
        assertFalse(result.isPresent(), "No actors should be found by name in an empty database");
    }
}
