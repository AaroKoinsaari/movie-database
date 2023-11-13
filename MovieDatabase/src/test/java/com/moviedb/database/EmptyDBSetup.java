package com.moviedb.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * This class sets up a temporary in-memory H2 database that replicates the
 * structure of the production SQLite database for testing purposes.
 * It defines the database schema and populates genres, which works the
 * static reference data. The database setup's lifecycle is tied to
 * individual tests, ensuring isolation and no side effects across test cases.
 */
public abstract class EmptyDBSetup {

    protected Connection connection;

    /**
     * Prepares the in-memory database with the necessary tables and static data before each test.
     */
    @BeforeEach
    public void setUpDB() {
        try {
            // Use connection to H2 database for testing
            connection = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");

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

                // Insert static genres
                stmt.execute("INSERT INTO genres (name) VALUES ('Action')");        // 1
                stmt.execute("INSERT INTO genres (name) VALUES ('Adventure')");     // 2
                stmt.execute("INSERT INTO genres (name) VALUES ('Comedy')");        // 3
                stmt.execute("INSERT INTO genres (name) VALUES ('Crime')");         // 4
                stmt.execute("INSERT INTO genres (name) VALUES ('Drama')");         // 5
                stmt.execute("INSERT INTO genres (name) VALUES ('Fantasy')");       // 6
                stmt.execute("INSERT INTO genres (name) VALUES ('Historical')");    // 7
                stmt.execute("INSERT INTO genres (name) VALUES ('Horror')");        // 8
                stmt.execute("INSERT INTO genres (name) VALUES ('Mystery')");       // 9
                stmt.execute("INSERT INTO genres (name) VALUES ('Romance')");       // 10
            }
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            fail("Error creating the empty database structure");
        }
    }


    /**
     * Cleans up the database connection after each test to ensure a fresh start for the next one.
     */
    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    /**
     * Adds a specified number of test actors to the actors table in the database.
     *
     * @param numberOfActors The number of test actors to create.
     */
    protected void addActorsToDB(int numberOfActors) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO actors (name) VALUES (?)")) {
            for (int i = 1; i <= numberOfActors; i++) {
                ps.setString(1, "Actor " + i);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            fail("Error adding actors to database");
        }
    }
}
