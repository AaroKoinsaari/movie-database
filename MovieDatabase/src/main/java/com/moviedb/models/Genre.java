package com.moviedb.models;

import java.util.Objects;


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


    /**
     * Compares the genre with the specified object for equality.
     * The equality of two genres is determined by the equality of their id and name fields.
     *
     * @param o the object to be compared for equality with the genre.
     * @return true if the specified object is equal to the genre, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;  // Same instance
        }
        if (o == null || getClass() != o.getClass()) {
            return false;  // Different type or null
        }

        Genre genre = (Genre) o;  // Cast the object to genre

        return id == genre.id && Objects.equals(name, genre.name);
    }


    /**
     * Returns the hash code value for the genre.
     * The hash code of a genre is computed by its id and name fields.
     *
     * @return the hash code value for the genre.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
