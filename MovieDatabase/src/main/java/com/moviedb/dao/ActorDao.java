package com.moviedb.dao;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import com.moviedb.models.Actor;


/**
 * Data Access Object for the Actor class.
 */
public class ActorDao {

    /** Connection used to execute SQL queries and interact with the database. */
    private Connection connection;

    /** The URL pointing to the SQL database location. */
    private static final String DB_URL = "jdbc:sqlite:database/moviedatabase.db";


    /**
     * Default constructor that initializes the connection to the default SQLite database.
     */
    public ActorDao() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Constructor that accepts a specific database connection.
     *
     * @param connection The specific connection to database.
     */
    public ActorDao(Connection connection) {
        this.connection = connection;
    }


    /**
     * Creates and adds an actor to the SQL database.
     *
     * @param actor The actor to be added.
     * @return The generated ID of the added actor, or -1 if an error occurs.
     */
    public int create(Actor actor) {
        String sql = "INSERT INTO actors(name) VALUES(?)";
        int actorId = -1;  // Default error state

        try (PreparedStatement pstmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, actor.getName());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        actorId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actorId;
    }


    /**
     * Retrieves an actor based on its ID.
     *
     * @param id The unique identifier of the actor to be fetched.
     * @return The actor of found, an empty optional otherwise.
     */
    public Optional<Actor> read(int id) {
        String sql = "SELECT name, id FROM actors where id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                return Optional.of(new Actor(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            System.out.println("Error reading the actor from database");
        }
        return Optional.empty();
    }


    /**
     * Updates the name of an actor in the SQL database.
     *
     * @param updatedActor The movie object containing updated information.
     * @return boolean true if the update was successful, otherwise false.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean update(Actor updatedActor) {
        String sql = "UPDATE actors SET name = ? WHERE id = ?";
        boolean isUpdated = false;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, updatedActor.getName());
            pstmt.setInt(2, updatedActor.getId());
            int affectedRows = pstmt.executeUpdate();
            isUpdated = affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }


    /**
     * Deletes an actor from the SQL database.
     *
     * @param actorId The ID of the actor to be deleted.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean delete(int actorId) throws SQLException {
        if (!isActorLinkedToMovie(actorId)) {
            String sql = "DELETE FROM actors WHERE id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, actorId);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;  // Return true if deletion was successful
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;  // Re-throw exception
            }
        } else {
            // Throw an exception if the actor is linked to one or more movies
            throw new SQLException("Actor is linked to one or more movies.");
        }
    }


    /**
     * Checks if an actor is associated with any movies.
     *
     * @param actorId the ID of the actor to check.
     * @return true if the actor is linked to one or more movies, false otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    private boolean isActorLinkedToMovie(int actorId) {
        String sql = "SELECT COUNT(*) FROM movie_actors WHERE actor_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, actorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;  // true if the actor is associated with any movie
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Reads all actors from the SQL database and returns them as a list.
     *
     * @return A list of all actors.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<Actor> readAll() {
        List<Actor> actors = new ArrayList<>();
        String sql = "SELECT id, name FROM actors";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Insert the values of name and id from every row to the ArrayList
                actors.add(new Actor(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actors;
    }


    /**
     * Retrieves an actor from the SQL database based on its ID.
     *
     * @param id The ID of the actor to retrieve.
     * @return The actor if found, otherwise an empty optional.
     * @throws SQLException If there's an error during the database operation.
     */
    public Optional<Actor> getActorById(int id) {
        String sql = "SELECT id FROM actors WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            // Wrap the result in Optional if found
            if (rs.next()) {
                return Optional.of(new Actor(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    /**
     * Retrieves a actor by its name.
     *
     * @param name The name of the actor to retrieve.
     * @return The actor if found, otherwise an empty optional.
     * @throws SQLException If there's an error during the database operation.
     */
    public Optional<Actor> getActorByName(String name) {
        String sql = "SELECT name FROM actors WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            // Wrap the result in Opitonal if found
            if (rs.next()) {
                return Optional.of(new Actor(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
