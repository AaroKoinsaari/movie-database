package com.moviedb.dao;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Actor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorDaoFilledDBTest extends FilledDBSetup {
    private ActorDao dao;

    @BeforeEach
    void setUp() {
        dao = new ActorDao(connection);
    }

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


    @Test
    void readTest() {
        // Assert that an existing actor exists in the database
        Optional<Actor> fetchedExistingActor = dao.read(6);  // Leonardo Di Caprio
        assertTrue(fetchedExistingActor.isPresent(), "Actor should be present");
        fetchedExistingActor.ifPresent(actor -> {
            assertEquals("Leonardo Di Caprio", actor.getName(), "Actor name should match");
            assertEquals(6, actor.getId(), "Actor ID should match");
        });

        // Assert that non-existing actor is not in the database
        Optional<Actor> fetchedNonExistentActor = dao.read(99);
        assertTrue(fetchedNonExistentActor.isEmpty(), "Actor should not be present");

        // Assert that new inserted actor can be read
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
}
