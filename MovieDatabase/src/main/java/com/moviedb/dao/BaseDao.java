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
 * Provides common functionalities for managing database connections and logging.
 * <p>
 * This abstract class allows derived DAO classes to either create their own database
 * connection or utilize an existing one.
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
     * This method handles ID retrieval for different databases. It uses "SELECT last_insert_rowid()"
     * for SQLite due to its specific behavior with auto-increment keys. For other databases like H2,
     * it utilizes the standard JDBC getGeneratedKeys method. This approach is necessary to resolve
     * compatibility issues between production (SQLite) and testing environments (H2), ensuring
     * consistent behavior across different database systems.
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
                    generatedId = rs.getInt(1); // Retrieve the generated key (ID)
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
