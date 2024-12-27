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


public class ActorDao extends BaseDao {

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
            connection.setAutoCommit(false);

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
                connection.rollback();
                logger.log(Level.INFO, "Transaction rolled back due to SQLException", e);
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Error during transaction rollback", rollbackEx);
            }
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException autoCommitEx) {
                logger.log(Level.SEVERE, "Error resetting auto-commit behavior", autoCommitEx);
            }
        }

        return generatedActorId;
    }


    /**
     * Retrieves an actor based on its ID.
     *
     * @param id The unique ID of the actor to be fetched.
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
