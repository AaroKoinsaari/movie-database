package com.moviedb.models;

import java.util.List;


/**
 * A movie object with attributes such as title, release year, director,
 * associated actor IDs, and associated genre IDs.
 */
public class Movie {

    /** Main details of the movie */
    private int id;
    private String title;
    private int releaseYear;
    private String director;
    private String writer;
    private String producer;
    private String cinematographer;
    private int budget;
    private String country;

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
     * Constructs a new {@code Movie} object with specified details.
     *
     * @param id The unique identifier of the movie.
     * @param title The title of the movie.
     * @param releaseYear The release year of the movie.
     * @param director The director of the movie.
     * @param writer The writer of the movie script.
     * @param producer The producer of the movie.
     * @param cinematographer The cinematographer of the movie.
     * @param budget The budget of the movie production.
     * @param country The country where the movie was produced.
     * @param actorIds A list of IDs representing actors associated with the movie.
     * @param genreIds A list of IDs representing genres associated with the movie.
     */
    public Movie(int id, String title, int releaseYear, String director,
                 String writer, String producer, String cinematographer,
                 int budget, String country,
                 List<Integer> actorIds, List<Integer> genreIds) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.director = director;
        this.writer = writer;
        this.producer = producer;
        this.cinematographer = cinematographer;
        this.budget = budget;
        this.country = country;
        this.actorIds = actorIds;
        this.genreIds = genreIds;
    }


    /**
     * Constructs a new {@code Movie} object with the given details, excluding the unique ID.
     * This constructor is typically used for creating new movie entries where the ID is assigned
     * by the database auto-increment feature.
     *
     * @param title The title of the movie.
     * @param releaseYear The year when the movie was released.
     * @param director The director of the movie.
     * @param writer The writer of the movie script.
     * @param producer The producer of the movie.
     * @param cinematographer The cinematographer of the movie.
     * @param budget The budget of the movie production.
     * @param country The country where the movie was produced.
     * @param actorIds A list of IDs representing actors associated with the movie.
     * @param genreIds A list of IDs representing genres associated with the movie.
     */
    public Movie(String title, int releaseYear, String director,
                 String writer, String producer, String cinematographer,
                 int budget, String country,
                 List<Integer> actorIds, List<Integer> genreIds) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.director = director;
        this.writer = writer;
        this.producer = producer;
        this.cinematographer = cinematographer;
        this.budget = budget;
        this.country = country;
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
     * Sets the ID of the movie.
     *
     * @param id The ID to be set.
     */
    public void setId(int id) {
        this.id = id;
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
     * Returns the writer of the movie's script.
     *
     * @return The movie's writer.
     */
    public String getWriter() {
        return writer;
    }


    /**
     * Returns the producer of the movie.
     *
     * @return The movie's producer.
     */
    public String getProducer() {
        return producer;
    }


    /**
     * Returns the cinematographer of the movie.
     *
     * @return The movie's cinematographer.
     */
    public String getCinematographer() {
        return cinematographer;
    }


    /**
     * Returns the budget of the movie production.
     *
     * @return The movie's budget.
     */
    public int getBudget() {
        return budget;
    }


    /**
     * Returns the country where the movie was produced.
     *
     * @return The movie's country of production.
     */
    public String getCountry() {
        return country;
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


    /**
     * Provides a string representation of the movie object.
     * Currently, it returns the movie's title.
     *
     * @return A string representing the movie, specifically its title.
     */
    @Override
    public String toString() {
        return this.getTitle();
    }
}
