package com.moviedb.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.moviedb.dao.MovieDao;
import org.junit.jupiter.api.BeforeEach;

import com.moviedb.models.Actor;
import com.moviedb.models.Genre;
import com.moviedb.models.Movie;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.nimbus.State;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Sets up a temporary in-memory H2 database for testing, replicating the
 * structure of the production SQLite database with pre-existing test data.
 * This ensures that tests can run against a database schema that is created
 * and destroyed with each test, without persisting data or requiring SQLite-specific setup.
 */
public abstract class FilledDBSetup extends EmptyDBSetup {

    /**
     * Prepares the in-memory database with the necessary tables and filled data before each test.
     */
    @BeforeEach
    @Override
    public void setUpDB() {
        super.setUpDB();  // Fill the database first as empty

        // Insert some actors to database for testing
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO actors (name) VALUES ('Robert De Niro')");      // 1
            stmt.execute("INSERT INTO actors (name) VALUES ('Meryl Streep')");        // 2
            stmt.execute("INSERT INTO actors (name) VALUES ('Jamie Foxx')");          // 3
            stmt.execute("INSERT INTO actors (name) VALUES ('Christoph Waltz')");     // 4
            stmt.execute("INSERT INTO actors (name) VALUES ('Margot Robbie')");       // 5
            stmt.execute("INSERT INTO actors (name) VALUES ('Leonardo Di Caprio')");  // 6
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            fail("Error creating database structure with actors");
        }

        // Insert some test movies
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO movies (title, release_year, director) " +  // 1
                    "VALUES ('Inception', 2010, 'Christopher Nolan')");
            stmt.execute("INSERT INTO movies (title, release_year, director) " +  // 2
                    "VALUES ('The Wolf of Wall Street', 2013, 'Martin Scorsese')");
            stmt.execute("INSERT INTO movies (title, release_year, director) " +  // 3
                    "VALUES ('Django Unchained', 2012, 'Quentin Tarantino')");
            stmt.execute("INSERT INTO movies (title, release_year, director) " +  // 4
                    "VALUES ('The Deer Hunter', 1978, 'Michael Cimino')");
            stmt.execute("INSERT INTO movies (title, release_year, director) " +  // 5
                    "VALUES ('Taxi Driver', 1976, 'Martin Scorsese')");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            fail("Error while inserting test movies to database");
        }

        // TODO: join tables
    }
}
