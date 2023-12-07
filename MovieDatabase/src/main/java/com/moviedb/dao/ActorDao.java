package com.moviedb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.moviedb.models.Actor;


/**
 * Data Access Object for the Actor class.
 * This class provides an abstraction layer between the application and the underlying database.
 * It handles all database operations related to actors, including creating, reading, updating, and deleting actor records.
 *
 * The ActorDao class ensures that actor data is accessed and manipulated in a consistent and database-agnostic manner.
 * It encapsulates all SQL queries and shields the rest of the application from direct database interactions,
 * promoting cleaner separation of concerns and making the codebase more maintainable.
 */
public class ActorDao {

    // Connection used to execute SQL queries and interact with the database.
    private final Connection connection;

    // The URL pointing to the SQL database location.
    private static final String DB_URL = "jdbc:sqlite:database/moviedatabase.db";

    // Logger for exceptions
    private static final Logger logger = Logger.getLogger(ActorDao.class.getName());


    // Define SQL queries
    private static final String SQL_LAST_INSERT_ID = "SELECT last_insert_rowid()";
    private static final String SQL_INSERT_ACTOR = "INSERT INTO actors(name) VALUES(?)";
    private static final String SQL_READ_ACTOR = "SELECT name, id FROM actors WHERE id = ?";
    private static final String SQL_READ_ALL_ACTORS = "SELECT id, name FROM actors";
    private static final String SQL_UPDATE_ACTOR = "UPDATE actors SET name = ? WHERE id = ?";
    private static final String SQL_DELETE_ACTOR = "DELETE FROM actors WHERE id = ?";
    private static final String SQL_CHECK_ACTOR_LINK = "SELECT COUNT(*) FROM movie_actors WHERE actor_id = ?";
    private static final String SQL_GET_ACTOR_BY_ID = "SELECT id, name FROM actors WHERE id = ?";
    private static final String SQL_GET_ACTOR_BY_NAME = "SELECT id, name FROM actors WHERE name = ?";


    /**
     * Default constructor that initializes the connection to the default SQLite database.
     */
    public ActorDao() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to the database", e);
            throw new RuntimeException(e);
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
     * Creates and adds an actor to the database.
     *
     * @param actor The actor to be added.
     * @return The generated ID of the added actor, or -1 if an error occurs.
     */
    public int create(Actor actor) throws SQLException {
        int generatedActorId = -1;
        try {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT_ACTOR, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, actor.getName());
                pstmt.executeUpdate();

                // Retrieve the generated key (actor ID)
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(SQL_LAST_INSERT_ID)) {
                    if (rs.next()) {
                        generatedActorId = rs.getInt(1);
                    }
                }
            }

            // Check if a valid ID was generated
            if (generatedActorId <= 0) {
                throw new SQLException("Failed to create an actor");
            }

            connection.commit();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException during actor creation", e);
            try {
                connection.rollback();  // Try rollback on failure
                logger.log(Level.INFO, "Transaction rolled back due to SQLException");
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
            }
            throw e;  // Re-throw the exception to be handled by the caller
        } finally {
            try {
                connection.setAutoCommit(true);  // Reset auto-commit behavior
            } catch (SQLException autoCommitEx) {
                logger.log(Level.SEVERE, "Error resetting auto-commit", autoCommitEx);
            }
        }
        return generatedActorId;
    }



    /**
     * Retrieves an actor based on its ID.
     *
     * @param id The unique identifier of the actor to be fetched.
     * @return The actor of found, an empty optional otherwise.
     */
    public Optional<Actor> read(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_READ_ACTOR)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    return Optional.of(new Actor(id, name));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching actor with ID: " + id, e);
        }
        return Optional.empty();
    }


    /**
     * Reads all actors from the database and returns them as a list.
     *
     * @return A list of all actors.
     * @throws SQLException If there's an error during the database operation.
     */
    public List<Actor> readAll() throws SQLException {
        List<Actor> actors = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(SQL_READ_ALL_ACTORS);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                actors.add(new Actor(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error reading all actors", e);
            throw e; // Re-throw exception
        }
        return actors;
    }


    /**
     * Retrieves an actor from the database based on its ID.
     *
     * @param id The ID of the actor to retrieve.
     * @return The actor if found, otherwise an empty optional.
     * @throws SQLException If there's an error during the database operation.
     */
    public Optional<Actor> getActorById(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ACTOR_BY_ID)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Actor(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching actor by ID: " + id, e);
            throw e;
        }
        return Optional.empty();
    }


    /**
     * Retrieves an actor by its name.
     *
     * @param name The name of the actor to retrieve.
     * @return The actor if found, otherwise an empty optional.
     * @throws SQLException If there's an error during the database operation.
     */
    public Optional<Actor> getActorByName(String name) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ACTOR_BY_NAME)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Actor(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching actor by name: " + name, e);
            throw e;
        }
        return Optional.empty();
    }


    /**
     * Updates the name of an actor in the database.
     *
     * @param updatedActor The movie object containing updated information.
     * @return boolean true if the update was successful, otherwise false.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean update(Actor updatedActor) {
        boolean isUpdated = false;

        try {
            connection.setAutoCommit(false);  // Start transaction

            try (PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE_ACTOR)) {
                pstmt.setString(1, updatedActor.getName());
                pstmt.setInt(2, updatedActor.getId());
                int affectedRows = pstmt.executeUpdate();
                isUpdated = affectedRows > 0;
            }

            connection.commit();
            isUpdated = true;
        } catch (SQLException e) {
            try {
                connection.rollback();  // Try to rollback on error
                logger.log(Level.SEVERE, "Error updating movie with ID: " + updatedActor.getId(), e);
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);  // Reset auto-commit behavior
            } catch (SQLException autoCommitEx) {
                logger.log(Level.SEVERE, "Error resetting auto-commit", autoCommitEx);
            }
        }
        return isUpdated;
    }


    /**
     * Deletes an actor from the database.
     *
     * @param actorId The ID of the actor to be deleted.
     * @throws SQLException If there's an error during the database operation.
     */
    public boolean delete(int actorId) throws SQLException {
        if (!isActorLinkedToMovie(actorId)) {
            boolean isDeleted = false;

            try {
                connection.setAutoCommit(false); // Start transaction

                try (PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE_ACTOR)) {
                    pstmt.setInt(1, actorId);
                    int affectedRows = pstmt.executeUpdate();
                    isDeleted = affectedRows > 0;
                }

                connection.commit(); // Commit the transaction
            } catch (SQLException e) {
                try {
                    connection.rollback(); // Rollback on error
                    logger.log(Level.SEVERE, "Transaction rolled back due to an error", e);
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
                }
                logger.log(Level.SEVERE, "Error deleting actor with ID: " + actorId, e);
                throw e; // Re-throw exception
            } finally {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit behavior
                } catch (SQLException autoCommitEx) {
                    logger.log(Level.SEVERE, "Error resetting auto-commit", autoCommitEx);
                }
            }
            return isDeleted;
        } else {
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
    private boolean isActorLinkedToMovie(int actorId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_CHECK_ACTOR_LINK)) {
            pstmt.setInt(1, actorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;  // true if the actor is associated with any movie
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if actor is linked to movies", e);
            throw e; // Re-throw exception
        }
        return false;
    }
}
