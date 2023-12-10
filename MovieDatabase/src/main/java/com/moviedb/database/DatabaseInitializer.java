package com.moviedb.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Handles the initialization of the database for the movie database application.
 * This class contains methods to create the necessary tables if they do not already exist.
 * It ensures that the database is set up correctly with the required schema before the application is used.
 */
public class DatabaseInitializer {

    // Logger for logging errors
    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());


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
            logger.log(Level.SEVERE, "Error fetching all the movies from database. SQLState: " + e.getSQLState() +
                    ", Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
        }
    }


    /**
     * Creates the 'actors' table in the database if it doesn't exist.
     * This table stores actor information with fields for ID and name,
     * also creating index on 'name' column for better search efficiency
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the table.
     */
    private static void createActorsTable(Connection connection) throws SQLException {
        String createTablesql = "CREATE TABLE IF NOT EXISTS actors (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)";

        // Create indexing for names column
        String createIndexSql = "CREATE INDEX IF NOT EXISTS idx_actor_name ON actors (name)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTablesql);
            stmt.execute(createIndexSql);
        }
    }


    /**
     * Creates the 'genres' table in the database if it doesn't exist.
     * This table stores genre information with fields for ID and name. ID is set with
     * the AUTOINCREMENT method in the order in which they're presented, starting from 1.
     * If the table is created for the first time or is empty, it is populated with a predefined set of genres.
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the table or inserting genres.
     */
    private static void createGenresTable(Connection connection) throws SQLException {
        // Create genres table if it doesn't exist yet
        String createTableSql = "CREATE TABLE IF NOT EXISTS genres (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);
        }

        // Check if the table is empty
        String checkTableSql = "SELECT COUNT(*) FROM genres";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkTableSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String[] genres = {"Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Documentary",
                        "Drama", "Family", "Fantasy", "Film Noir", "History", "Horror", "Musical", "Mystery", "Romance",
                        "Sci-Fi", "Sport", "Thriller", "War", "Western"};
                for (String genre : genres) {
                    String insertSQL = "INSERT INTO genres (name) VALUES ('" + genre + "')";
                    stmt.executeUpdate(insertSQL);
                }
            }
        }
    }


    /**
     * Creates the 'movies' table in the database if it doesn't exist.
     * This table stores movie information along with a unique ID for each movie.
     *
     * @param connection The connection to the database.
     * @throws SQLException if there is an error creating the table.
     */
    private static void createMoviesTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS movies (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "release_year INTEGER, " +
                "director TEXT," +
                "writer TEXT," +
                "producer TEXT," +
                "cinematographer TEXT," +
                "budget INTEGER," +
                "country TEXT)";
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
