package com.moviedb.database;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.fail;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class MoviedatabaseTest {

    private Connection connection;

    @BeforeEach
    public void setUpDB() {
        try {
            // Use connection to H2 database for testing
            connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");

            try (Statement stmt = connection.createStatement()) {
                // Create actors table
                stmt.execute("CREATE TABLE actors ("
                        + "id INTEGER AUTO_INCREMENT,"
                        + "name VARCHAR(255) NOT NULL,"
                        + "PRIMARY KEY(id))");

                // Create genres table
                stmt.execute("CREATE TABLE genres ("
                        + "id INTEGER AUTO_INCREMENT,"
                        + "name VARCHAR(255) NOT NULL,"
                        + "PRIMARY KEY(id))");

                // Create movies table
                stmt.execute("CREATE TABLE movies ("
                        + "id INTEGER AUTO_INCREMENT,"
                        + "title VARCHAR(255) NOT NULL,"
                        + "release_year INTEGER,"
                        + "director VARCHAR(255),"
                        + "PRIMARY KEY(id))");

                // Create movie_actors table
                stmt.execute("CREATE TABLE movie_actors ("
                        + "movie_id INTEGER,"
                        + "actor_id INTEGER,"
                        + "PRIMARY KEY(movie_id, actor_id),"
                        + "FOREIGN KEY(movie_id) REFERENCES movies(id) ON DELETE CASCADE,"
                        + "FOREIGN KEY(actor_id) REFERENCES actors(id) ON DELETE CASCADE)");

                // Create movie_genres table
                stmt.execute("CREATE TABLE movie_genres ("
                        + "movie_id INTEGER,"
                        + "genre_id INTEGER,"
                        + "PRIMARY KEY(movie_id, genre_id),"
                        + "FOREIGN KEY(movie_id) REFERENCES movies(id) ON DELETE CASCADE,"
                        + "FOREIGN KEY(genre_id) REFERENCES genres(id) ON DELETE CASCADE)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error creating database structure");
        }
    }
}
