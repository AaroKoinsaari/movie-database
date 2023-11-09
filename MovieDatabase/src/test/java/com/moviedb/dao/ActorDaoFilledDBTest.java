package com.moviedb.dao;

import com.moviedb.database.FilledDBSetup;
import com.moviedb.models.Actor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorDaoFilledDBTest extends FilledDBSetup {
    private ActorDao dao;

    @Test
    void createTest() {
        String name = "New Actor";
        Actor newActor = new Actor(name);

        // Insert new actor to database and fetch it
        dao = new ActorDao(connection);
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
        dao = new ActorDao(connection);

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



}
