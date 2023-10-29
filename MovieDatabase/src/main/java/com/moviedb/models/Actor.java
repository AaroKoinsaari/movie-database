package com.moviedb.models;

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
     * @param id The unique identifier of the actor.
     * @param name The name of the actor.
     */
    public Actor(int id, String name) {
        this.id = id;
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
     * @return The name of the actor.
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name of the actor.
     * @param name The name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
