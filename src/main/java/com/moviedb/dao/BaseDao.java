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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Base class for Data Access Objects (DAOs) in the application.
 * Common functionalities for managing database connections and logging.
 * <p>
 * Allows derived DAO classes to either create their own database
 * connection or use an existing one.
 */
public abstract class BaseDao {

    protected static final String DB_URL = "jdbc:sqlite:";  // Default database URL for SQLite database connections.

    protected Connection connection;  // Database connection used by DAO implementations.
    protected Logger logger;  // Logger for logging errors and information.


    /**
     * Default constructor that initializes the connection to the default SQLite database.
     */
    public BaseDao() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
            this.logger = Logger.getLogger(getClass().getName());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating database connection. SQLState: " + e.getSQLState() +
                    ", Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
        }
    }


    /**
     * Constructor that accepts a specific database connection.
     *
     * @param connection The database connection to be used by the DAO.
     */
    public BaseDao(Connection connection) {
        this.connection = connection;
        this.logger = Logger.getLogger(getClass().getName());
    }


    /**
     * Retrieves the generated ID for the last inserted row.
     * <p>
     * Handles ID retrieval for different databases. It uses "SELECT last_insert_rowid()"
     * for SQLite because of its specific behavior with auto-increment keys. For other databases like H2,
     * it uses the standard JDBC getGeneratedKeys method. This is needed to resolve
     * compatibility issues between production (SQLite) and testing environments (H2), so we can ensure
     * consistent behavior across these different database systems.
     *
     * @param pstmt The PreparedStatement from which the generated key is retrieved.
     * @return The generated ID of the last inserted row, or -1 if an error occurs.
     * @throws SQLException If there's an error during the database operation.
     */
    protected int fetchGeneratedId(PreparedStatement pstmt) throws SQLException {
        int generatedId = -1;
        String databaseProductName = connection.getMetaData().getDatabaseProductName();

        if (databaseProductName.equals("SQLite")) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    generatedId = rs.getInt(1); // Retrieve the generated ID
                }
            }
        } else {  // For H2 db
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        }
        return generatedId;
    }
}
