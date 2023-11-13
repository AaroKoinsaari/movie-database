package com.moviedb.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Handles the initialization of the database for the movie database application.
 * This class contains methods to create the necessary tables if they do not already exist.
 * It ensures that the database is set up correctly with the required schema before the application is used.
 */
public class DatabaseInitializer {

    /**
     * Initializes the database by creating all necessary tables.
     * It sequentially calls methods to create each table in the database,
     * handling SQLExceptions that might occur during the process.
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the tables.
     */
    public static void initialize(Connection connection) throws SQLException {
        try {
            createActorsTable(connection);
            createGenresTable(connection);
            createMoviesTable(connection);
            createMovieActorsTable(connection);
            createMovieGenresTable(connection);
        } catch (SQLException e) {
            e.printStackTrace();  // TODO: Improve
        }
    }


    /**
     * Creates the 'actors' table in the database if it doesn't exist.
     * This table stores actor information with fields for ID and name.
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the table.
     */
    private static void createActorsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS actors (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }


    /**
     * Creates the 'genres' table in the database if it doesn't exist.
     * This table stores genre information with fields for ID and name.
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the table.
     */
    private static void createGenresTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS genres (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }


    /**
     * Creates the 'movies' table in the database if it doesn't exist.
     * This table stores movie information including title, release year, and director,
     * along with a unique ID for each movie.
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the table.
     */
    private static void createMoviesTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS movies (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "release_year INTEGER, " +
                "director TEXT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }


    /**
     * Creates the 'movie_actors' table in the database if it doesn't exist.
     * This table is a junction table that links movies and actors,
     * representing the many-to-many relationship between them.
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the table.
     */
    private static void createMovieActorsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS movie_actors (" +
                "movie_id INTEGER, " +
                "actor_id INTEGER, " +
                "PRIMARY KEY(movie_id, actor_id), " +
                "FOREIGN KEY(movie_id) REFERENCES movies(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(actor_id) REFERENCES actors(id) ON DELETE CASCADE)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }


    /**
     * Creates the 'movie_genres' table in the database if it doesn't exist.
     * This table is a junction table that links movies and genres,
     * representing the many-to-many relationship between them.
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the table.
     */
    private static void createMovieGenresTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS movie_genres (" +
                "movie_id INTEGER, " +
                "genre_id INTEGER, " +
                "PRIMARY KEY(movie_id, genre_id), " +
                "FOREIGN KEY(movie_id) REFERENCES movies(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(genre_id) REFERENCES genres(id) ON DELETE CASCADE)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
