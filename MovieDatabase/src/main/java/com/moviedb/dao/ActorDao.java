package com.moviedb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import com.moviedb.models.Actor;


/**
 * Data Access Object for the Actor class.
 * This class serves as an intermediary between the application logic and the database layer,
 * handling all database operations related to actors.
 * <p>
 * The ActorDao class ensures consistent and database-independent access and manipulation of actor data.
 * It encapsulates SQL queries, providing a clean separation from direct database interactions,
 * which enhances the maintainability and scalability of the codebase.
 */
public class ActorDao extends BaseDao {

    // Define SQL queries
    private static final String SQL_INSERT_ACTOR = "INSERT INTO actors(name) VALUES(?)";
    private static final String SQL_READ_ACTOR = "SELECT name, id FROM actors WHERE id = ?";
    private static final String SQL_UPDATE_ACTOR = "UPDATE actors SET name = ? WHERE id = ?";
    private static final String SQL_READ_ALL_ACTORS = "SELECT id, name FROM actors";
    private static final String SQL_GET_ACTOR_BY_ID = "SELECT id, name FROM actors WHERE id = ?";
    private static final String SQL_GET_ACTOR_BY_NAME = "SELECT name, id FROM actors WHERE name = ?";


    /**
     * Default constructor that initializes the connection to the default SQLite database.
     */
    public ActorDao() {
        super();
    }


    /**
     * Constructor that accepts a specific database connection.
     *
     * @param connection The specific connection to database.
     */
    public ActorDao(Connection connection) {
        super(connection);
    }


    /**
     * Creates and adds an actor to the SQL database.
     *
     * @param actor The actor to be added.
     * @return The generated ID of the added actor, or -1 if an error occurs.
     * @throws SQLException If there's an error during the database operation.
     */
    public int create(Actor actor) throws SQLException {
        int generatedActorId;  // Default error state

        try {
            connection.setAutoCommit(false);  // Start transaction

            try (PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT_ACTOR,
                    Statement.RETURN_GENERATED_KEYS)) {  // ID for the added actor in the generated key
                pstmt.setString(1, actor.getName());
                pstmt.executeUpdate();

                generatedActorId = fetchGeneratedId(pstmt);

                // Ensure to have a valid actor ID before proceeding
                if (generatedActorId <= 0) {
                    throw new SQLException("Failed to retrieve generated actor ID");
                }

                connection.commit();
            }
        } catch (SQLException e) {
            try {
                connection.rollback();  // Attempt to rollback on failure
                logger.log(Level.INFO, "Transaction rolled back due to SQLException", e);
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
            }
            throw e;  // Re-throw exception to be handled by the caller
        } finally {
            try {
                connection.setAutoCommit(true);  // Restore default behavior
            } catch (SQLException autoCommitEx) {
                logger.log(Level.SEVERE, "Error resetting auto-commit behavior", autoCommitEx);
            }
        }

        return generatedActorId;
    }


    /**
     * Retrieves an actor based on its ID.
     *
     * @param id The unique identifier of the actor to be fetched.
     * @return The actor of found, an empty optional otherwise.
     * @throws SQLException If there's an error during the database operation.
     */
    public Optional<Actor> read(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_READ_ACTOR)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(convertResultSetToActor(rs));
            }
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
    public boolean update(Actor updatedActor) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE_ACTOR)) {
            pstmt.setString(1, updatedActor.getName());
            pstmt.setInt(2, updatedActor.getId());
            return pstmt.executeUpdate() > 0;
        }
    }


    /**
     * Reads all actors from the SQL database and returns them as a list.
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
    public Optional<Actor> getActorById(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ACTOR_BY_ID)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(convertResultSetToActor(rs));
            }
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

            // Wrap the result in Opitonal if found
            if (rs.next()) {
                return Optional.of(convertResultSetToActor(rs));
            }
        }
        return Optional.empty();
    }


    /**
     * Creates an Actor object from a ResultSet.
     *
     * @param rs The ResultSet from which actor data is extracted.
     * @return An Actor object populated with data from the ResultSet.
     * @throws SQLException If there's an error during data extraction.
     */
    private Actor convertResultSetToActor(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Actor(id, name);
    }
}
