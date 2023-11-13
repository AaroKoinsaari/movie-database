package com.moviedb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.ListChooser;

import com.moviedb.dao.MovieDao;

public class MainViewController {

    private String databaseName;

    private Connection connection;

    private MovieDao movieDao;

    @FXML
    private Button addButton;

    @FXML
    private Button addMovieButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ListChooser<String> moviesListChooser;

    @FXML
    private Button saveButton;

    @FXML
    private ComboBoxChooser<?> searchComboBox;

    @FXML
    private TextField searchTextField;

    @FXML
    void handleAdd(ActionEvent event) {

    }

    @FXML
    void handleAddMovie(ActionEvent event) {

    }

    @FXML
    void handleDelete(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {

    }

    public void initializeDatabase(String dbName) {
        this.databaseName = dbName;
        openDatabaseConnection();
        this.movieDao = new MovieDao(connection);
        loadMoviesFromDB();
    }


    private void openDatabaseConnection() {
        String dbPath = "../database/" + databaseName + ".db";

        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    private void loadMoviesFromDB() {
        try {
            List<String> movieTitles = movieDao.getAllMovieTitles();
            moviesListChooser.clear();
            for (String title : movieTitles) {
                moviesListChooser.add(title);  // Add the title of the movie to ListChooser component
            }
        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }
}
