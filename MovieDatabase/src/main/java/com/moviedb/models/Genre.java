package com.moviedb.models;

/**
 * A genre object with id and genre name attributes.
 */
public class Genre {

    /** The unique identifier of the genre. */
    private int id;

    /** The name of the genre. */
    private String name;


    /**
     * Constructs a new {@code Genre} object with given id and name.
     *
     * @param id The unique identifier of the genre.
     * @param name The name of the genre.
     */
    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }


    /**
     * Constructs a new {@code Genre} object with given name (ID is handled in the database).
     *
     * @param name The name of the genre.
     */
    public Genre(String name) {
        this.name = name;
    }


    /**
     * Returns the ID of the genre.
     *
     * @return ID of the genre.
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the ID of the genre.
     *
     * @param id The ID to be set.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Returns the name of the genre.
     *
     * @return The name of the genre.
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name of the genre.
     *
     * @param name The name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
