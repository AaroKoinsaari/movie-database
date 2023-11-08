package com.moviedb.dao;

import com.moviedb.database.EmptyDBSetup;
import com.moviedb.models.Actor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActorDaoTestEmptyDBTest extends EmptyDBSetup {

    private ActorDao dao;

    @Test
    void createTest() {
        Actor actor1 = new Actor(1, "Test Actor 1");
        Actor actor2 = new Actor(2, "Test Actor 2");

        dao = new ActorDao(connection);
        int actorId1 = dao.create(actor1);  // id: 1
        int actorId2 = dao.create(actor2);  // id: 2

        Optional<Actor> foundActor1 = dao.read(actorId1);
        Optional<Actor> foundActor2 = dao.read(actorId2);

        assertEquals(actor1, foundActor1.orElse(null));
        assertEquals(actor2, foundActor2.orElse(null));
    }
}
