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

package com.moviedb.database;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


/**
 * This class sets up a temporary in-memory H2 database that replicates the
 * structure of the production SQLite database for testing purposes.
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
                        + "writer VARCHAR(255),"
                        + "producer VARCHAR(255),"
                        + "cinematographer VARCHAR(255),"
                        + "budget INTEGER,"
                        + "country VARCHAR(255),"
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
            fail("Error creating the empty database structure");
        }
    }


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
            fail("Error adding actors to database");
        }
    }
}
