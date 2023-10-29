package com.moviedb.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import com.moviedb.models.Genre;

/**
 * Data Access Object for the Genre class.
 */
public class GenreDao {

    /** Filepath to the csv file. */
    private final String path;

    /**
     * Default constructor.
     * Initializes the path to the default CSV file.
     */
    public GenreDao() {
        // TODO: check if filepath exist
        this("../../resources/data/genres.csv");
    }


    /**
     * Constructor that accepts a specific path.
     *
     * @param path The path to the CSV file.
     */
    public GenreDao(String path) {
        // TODO: check if filepath exist
        this.path = path;
    }


    /**
     * Reads all genres from the CSV file and returns them as a list.
     *
     * @return A list of all genres.
     * @throws IOException If there's an error reading the file.
     */
    public List<Genre> readAll() throws IOException {
        List<Genre> genres = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                genres.add(new Genre(id, name));
            }
        }
        return genres;
    }


    /**
     * Retrieves a genre by its ID.
     *
     * @param id The ID of the genre to retrieve.
     * @return The genre if found, otherwise an empty optional.
     * @throws IOException If there's an error reading the file.
     */
    public Optional<Genre> getGenreById(int id) throws IOException {
        List<Genre> genres = readAll();
        return genres.stream()
                .filter(g -> g.getId() == id)
                .findFirst();
    }


    /**
     * Retrieves a genre by its name.
     *
     * @param name The name of the genre to retrieve.
     * @return The genre if found, otherwise an empty optional.
     * @throws IOException If there's an error reading the file.
     */
    public Optional<Genre> getGenreByName(String name) throws IOException {
        List<Genre> genres = readAll();
        return genres.stream()
                .filter(g -> g.getName().equalsIgnoreCase(name))
                .findFirst();
    }

}

