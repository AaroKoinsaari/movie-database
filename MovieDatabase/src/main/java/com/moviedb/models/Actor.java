package com.moviedb.models;

import java.util.Objects;

/**
 * An actor object with id and actor name attributes.
 */
public class Actor {

    /** The unique identifier of the actor. */
    private int id;

    /** The name of the actor. */
    private String name;


    /**
     * Constructs a new {@code Actor} object with given id and name.
     *
     * @param id The unique identifier of the actor.
     * @param name The name of the actor.
     */
    public Actor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructs a new {@code Actor} object with given name (ID is handled in the database).
     *
     * @param name The name of the actor.
     */
    public Actor(String name) {
        this.name = name;
    }


    /**
     * Returns the ID of the actor.
     *
     * @return ID of the actor.
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the ID of the actor.
     *
     * @param id The ID to be set.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Returns the name of the actor.
     *
     * @return The name of the actor.
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name of the actor.
     *
     * @param name The name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Compares the actor with the specified object for equality.
     * The equality of two actors is determined by the equality of their id and name fields.
     *
     * @param o the object to be compared for equality with the actor.
     * @return true if the specified object is equal to the actor, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;  // Same instance
        }
        if (o == null || getClass() != o.getClass()) {
            return false;  // Different type or null
        }

        Actor actor = (Actor) o;  // Cast the object to Actor

        return id == actor.id && Objects.equals(name, actor.name);
    }


    /**
     * Returns the hash code value for the actor.
     * The hash code of an actor is computed by its id and name fields.
     *
     * @return the hash code value for the actor.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
