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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import com.moviedb.database.EmptyDBSetup;
import com.moviedb.models.Actor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * This class contains unit tests for the ActorDao class using an empty H2 database setup.
 * Only genres are existing in the empty database since they are static.
 */
public class ActorDaoEmptyDBTest extends EmptyDBSetup {

    private ActorDao dao;


    /**
     * Additional setup for the empty database for each test.
     * Initializes the connection to the test database for each test.
     */
    @BeforeEach
    void setUp() {
        dao = new ActorDao(connection);
    }


    @Test
    @DisplayName("Create new actors in the database and verify them")
    void testCreate() {
        String actorName1 = "Test Actor 1";
        String actorName2 = "Test Actor 2";

        // Create actors in the database and return the generated ID
        int actorId1 = assertDoesNotThrow(() -> dao.create(new Actor(actorName1)),
                "Creating actor 1 should not throw SQLException");
        int actorId2 = assertDoesNotThrow(() -> dao.create(new Actor(actorName2)),
                "Creating actor 2 should not throw SQLException");

        // Read the created actors from the database
        Optional<Actor> foundActor1 = assertDoesNotThrow(() -> dao.read(actorId1),
                "Reading actor 1 should not throw SQLException");
        Optional<Actor> foundActor2 = assertDoesNotThrow(() -> dao.read(actorId2),
                "Reading actor 2 should not throw SQLException");

        // Confirm that the actors are found and compare them to the created objects
        assertTrue(foundActor1.isPresent(), "Actor 1 should be present");
        assertTrue(foundActor2.isPresent(), "Actor 2 should be present");
        assertEquals(actorName1, foundActor1.get().getName(), "Actor 1's name should match");
        assertEquals(actorName2, foundActor2.get().getName(), "Actor 2's name should match");

        // Assert the IDs
        assertEquals(actorId1, foundActor1.get().getId(), "Actor 1's id should match");
        assertEquals(actorId2, foundActor2.get().getId(), "Actor 2's id should match");
    }


    @Test
    @DisplayName("Read a non-existent actor from the database")
    void testRead() {
        int nonExistentActorId = 99;

        Optional<Actor> fetchedActor = assertDoesNotThrow(() -> dao.read(nonExistentActorId),
                "Reading a non-existent actor should not throw SQLException");

        // Assert that the Optional is empty, indicating no actor found
        assertTrue(fetchedActor.isEmpty(), "Should return empty for a non-existent actor");
    }


    @Test
    @DisplayName("Update a non-existent actor in the database")
    void testUpdate() {
        // Attempt to update a non-existent actor
        String updatedName = "Updated Actor";
        int nonExistentActorId = 99;
        Actor actor = new Actor(updatedName);
        actor.setId(nonExistentActorId);

        // Perform the update and assert that it was unsuccessful
        boolean updateSuccessful = assertDoesNotThrow(() -> dao.update(actor),
                "Updating a non-existent actor should not throw SQLException");
        assertFalse(updateSuccessful, "Should return false for trying to update a non-existent actor");

        // Attempt to retrieve the non-existent actor from the database and assert that it's empty
        Optional<Actor> updatedActorOpt = assertDoesNotThrow(() -> dao.read(nonExistentActorId),
                "Reading a non-existent actor after update attempt should not throw SQLException");
        assertTrue(updatedActorOpt.isEmpty(), "Non-existent actor should not be present after attempted update");
    }


    @Test
    @DisplayName("Read all actors from empty database")
    void testReadAll() {
        List<Actor> actors = assertDoesNotThrow(() -> dao.readAll(),
                "Reading all actors from empty database should not throw SQLException");
        assertTrue(actors.isEmpty(), "Actor list should be empty in an empty database");
    }


    @Test
    @DisplayName("Get a non-existent actor by ID from the database")
    void testGetActorById() {
        Optional<Actor> result = assertDoesNotThrow(() -> dao.getActorById(1),
                "Getting a non-existent actor by ID should not throw SQLException");
        assertFalse(result.isPresent(), "No actors should be found by ID in an empty database");
    }


    @Test
    @DisplayName("Get a non-existent actor by name from the database")
    void testGetActorByName() {
        Optional<Actor> result = assertDoesNotThrow(() -> dao.getActorByName("Test Actor"),
                "Getting a non-existent actor by name should not throw SQLException");
        assertFalse(result.isPresent(), "No actors should be found by name in an empty database");
    }
}
