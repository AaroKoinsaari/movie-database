package com.moviedb.models;

import java.util.List;


/**
 * A movie object with attributes such as title, release year, director,
 * associated actor IDs, and associated genre IDs.
 */
public class Movie {

    /** The unique identifier of the movie. */
    private int id;

    /** The title of the movie. */
    private String title;

    /** The year when the movie was released. */
    private int releaseYear;

    /** The director of the movie. */
    private String director;

    /**
     * A list of IDs representing actors associated with the movie.
     * Each actor ID corresponds to a unique actor.
     */
    private List<Integer> actorIds;

    /**
     * A list of IDs representing genres associated with the movie.
     * Each genre ID corresponds to a unique genre.
     */
    private List<Integer> genreIds;


    /**
     * Constructs a new {@code Movie} object with the given details.
     *
     * @param id The unique identifier of the movie.
     * @param title The title of the movie.
     * @param releaseYear The release year of the movie.
     * @param director The director of the movie.
     * @param actorIds A list of actor IDs associated with the movie.
     * @param genreIds A list of genre IDs associated with the movie.
     */
    public Movie(int id, String title, int releaseYear, String director,
                 List<Integer> actorIds, List<Integer> genreIds) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.director = director;
        this.actorIds = actorIds;
        this.genreIds = genreIds;
    }


    /**
     * Constructs a new {@code Movie} object with given details (without id).
     *
     * @param title The title of the movie.
     * @param releaseYear The release year of the movie.
     * @param director The director of the movie.
     * @param actorIds A list of actor IDs associated with the movie.
     * @param genreIds A list of genre IDs associated with the movie.
     */
    public Movie(String title, int releaseYear, String director,
                 List<Integer> actorIds, List<Integer> genreIds) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.director = director;
        this.actorIds = actorIds;
        this.genreIds = genreIds;
    }


    /**
     * Returns the ID of the movie.
     *
     * @return The movie's ID.
     */
    public int getId() {
        return id;
    }


    /**
     * Returns the title of the movie.
     *
     * @return The movie's title.
     */
    public String getTitle() {
        return title;
    }


    /**
     * Returns the release year of the movie.
     *
     * @return The movie's release year.
     */
    public int getReleaseYear() {
        return releaseYear;
    }


    /**
     * Returns the director of the movie.
     *
     * @return The movie's director.
     */
    public String getDirector() {
        return director;
    }


    /**
     * Sets the ID of the movie.
     *
     * @param id The ID to be set.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Returns the list of associated genre IDs.
     *
     * @return The list of genre IDs.
     */
    public List<Integer> getGenreIds() {
        return this.genreIds;
    }


    /**
     * Returns the list of associated actor IDs.
     *
     * @return The list of actor IDs.
     */
    public List<Integer> getActorIds() {
        return this.actorIds;
    }
}
