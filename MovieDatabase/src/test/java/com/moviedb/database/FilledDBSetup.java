package com.moviedb.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import com.moviedb.models.Actor;
import com.moviedb.models.Genre;
import com.moviedb.models.Movie;


public abstract class FilledDBSetup extends EmptyDBSetup {

    @BeforeEach
    @Override
    public void setUpDB() {
        super.setUpDB();  // Fill the database first as empty

        List<Actor> actors = createActors();
        List<Genre> genres = createGenres();
        createMovies(actors, genres);
    }

    private List<Movie> createMovies(List<Actor> actors, List<Genre> genres) {
        List<Movie> movies = new ArrayList<>();

        movies.add(new Movie("The Godfather", 1972, "Francis Ford Coppola",
                Arrays.asList(actors.get(1).getId()), // Robert De Niro
                Arrays.asList(genres.get(0).getId(), genres.get(3).getId()) // Drama, Crime
        ));

        movies.add(new Movie("Titanic", 1997, "James Cameron",
                Arrays.asList(actors.get(2).getId()), // Leonardo DiCaprio
                Arrays.asList(genres.get(0).getId(), genres.get(8).getId()) // Drama, Adventure
        ));

        movies.add(new Movie("The Revenant", 2015, "Alejandro González Iñárritu",
                Arrays.asList(actors.get(2).getId()), // Leonardo DiCaprio
                Arrays.asList(genres.get(0).getId(), genres.get(8).getId()) // Drama, Adventure
        ));

        movies.add(new Movie("Blue Jasmine", 2013, "Woody Allen",
                Arrays.asList(actors.get(3).getId()), // Cate Blanchett
                Arrays.asList(genres.get(0).getId()) // Drama
        ));

        return movies;
    }



    private List<Actor> createActors() {
        return Arrays.asList(
                new Actor("Meryl Streep"),
                new Actor("Robert De Niro"),
                new Actor("Leonardo DiCaprio"),
                new Actor("Cate Blanchett")
        );
    }


    private List<Genre> createGenres() {
        return Arrays.asList(
                new Genre("Drama"),
                new Genre("Action"),
                new Genre("Comedy"),
                new Genre("Crime"),
                new Genre("History"),
                new Genre("Sci-Fi"),
                new Genre("Horror"),
                new Genre("Fantasy"),
                new Genre("Adventure"),
                new Genre("Mystery")
        );
    }
}
