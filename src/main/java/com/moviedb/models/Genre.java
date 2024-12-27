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

package com.moviedb.models;

import java.util.Objects;


/**
 * A genre object with id and genre name attributes.
 */
public class Genre {

    private int id;
    private String name;


    /**
     * Constructs a new {@code Genre} object with given id and name.
     *
     * @param id   The unique identifier of the genre.
     * @param name The name of the genre.
     */
    public Genre(int id, String name) {
        this.id = id;
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
     * The equality of two genres means the equality of their id and name fields.
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
     * The hash code of a genre is calculated by its id and name fields.
     *
     * @return the hash code value for the genre.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }


    /**
     * Returns a string representation of the genre.
     *
     * @return A string containing the name of the genre.
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
