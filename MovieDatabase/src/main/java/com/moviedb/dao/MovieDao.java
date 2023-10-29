package com.moviedb.dao;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import com.moviedb.models.Movie;


/**
 * Data Access Object for the Movie class.
 */
public class MovieDao {

    /** Filepath to the csv file. */
    private final String path;

    /**
     * Default constructor.
     * Initializes the path to the default CSV file.
     */
    public MovieDao() {
        // TODO: check if filepath exist
        this("../../resources/data/movies.csv");
    }


    /**
     * Constructor that accepts a specific path.
     *
     * @param path The path to the CSV file.
     */
    public MovieDao(String path) {
        this.path = path;
    }


    /**
     * Creates and adds a movie to the CSV file.
     *
     * @param movie The movie to be added.
     */
    public void create(Movie movie) {
        List<Movie> movies;

        try {
            movies = readAll();
            movie.setId(getNextId(movies));
            movies.add(movie);
            writeAll(movies);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Reads a specific movie based on its ID.
     *
     * @param id The ID of the movie.
     * @return The movie if found, otherwise null.
     */
    public Movie read(int id) throws IOException {
        for (Movie movie : readAll()) {
            if(movie.getId() == id) {
                return movie;
            }
        }
        return null;
    }


    /**
     * Updates the information of a specific movie.
     *
     * @param updatedMovie The updated movie information.
     */
    // TODO: ensure that the updated movie is added back to the list after removal in the update method.
    public void update(Movie updatedMovie) throws IOException {
        List<Movie> movies = readAll();
        movies.removeIf(movie -> movie.getId() == updatedMovie.getId());
        writeAll(movies);
    }


    /**
     * Deletes a movie with a given ID from the CSV file.
     *
     * @param id The ID of the movie to be deleted.
     * @throws IOException if there's an error accessing the file.
     */
    public void delete(int id) throws IOException {
        List<Movie> movies = readAll();
        movies.removeIf(movie -> movie.getId() == id);
        writeAll(movies);
    }


    /**
     * Returns the next available ID for a new movie.
     * The ID is based on the highest existing ID + 1.
     *
     * @param movies The list of all movies.
     * @return The next available ID.
     */
    private int getNextId(List<Movie> movies) {
        int max = 0;
        for (Movie movie : movies) {
            if (movie.getId() > max) {
                max = movie.getId();
            }
        }
        return max + 1;
    }


    /**
     * Reads all movies from the CSV file and returns them as a list.
     *
     * @return A list of all movies.
     * @throws IOException if there's an error accessing the file.
     */
    // TODO: improve exception handling
    public List<Movie> readAll() throws IOException {
        List<Movie> movies = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                movies.add(parseMovie(parts));
            }
        }
        return movies;
    }


    /**
     * Parses a movie's details from a given array of strings.
     *
     * @param parts The array containing movie details in string format.
     * @return A new movie instance.
     */
    // TODO: improve exception handling
    private Movie parseMovie(String[] parts) {
        int id = Integer.parseInt(parts[0]);
        String title = parts[1];
        int releaseYear = Integer.parseInt(parts[2]);
        String director = parts[3];
        List<Integer> actorIds = parseIds(parts[4]);
        List<Integer> genreIds = parseIds(parts[5]);

        return new Movie(id, title, releaseYear, director, actorIds, genreIds);
    }


    /**
     * Parses a list of IDs from a string.
     *
     * @param idStrings The string containing IDs separated by commas.
     * @return A list of integer IDs.
     */
    // TODO: improve exception handling
    private List<Integer> parseIds(String idStrings) {
        return Arrays.stream(idStrings.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }


    /**
     * Writes all movies to the CSV file.
     *
     * @param movies The list of movies to write to the file.
     * @throws IOException if there's an error accessing the file.
     */
    // TODO: improve exception handling
    private void writeAll(List<Movie> movies) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (Movie movie : movies) {
                bw.write(movie.toCSVLine());
                bw.newLine();
            }
        }
    }
}
