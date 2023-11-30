package com.moviedb.controllers;

import com.moviedb.dao.ActorDao;
import com.moviedb.dao.GenreDao;
import com.moviedb.dao.MovieDao;
import com.moviedb.models.Actor;
import com.moviedb.models.Genre;
import com.moviedb.models.Movie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MovieDialogViewController implements Initializable {


    private Connection connection;
    private MovieDao movieDao;

    private ActorDao actorDao;

    private GenreDao genreDao;

    private Movie currentMovie;

    private List<Actor> selectedActors = new ArrayList<>();

    private ListView activeListView;

    @FXML
    private ListView<Actor> actorsListView;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private TextField directorTextField;

    @FXML
    private ListView<Genre> genresListView;

    @FXML
    private TextField releaseYearTextField;

    @FXML
    private TextField titleTextField;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actorsListView.setOnMouseClicked(event -> activeListView = actorsListView);
        genresListView.setOnMouseClicked(event -> activeListView = genresListView);
    }


    protected void setConnection(Connection connection) {
        this.connection = connection;
        this.movieDao = new MovieDao(connection);
        this.actorDao = new ActorDao(connection);
        this.genreDao = new GenreDao(connection);
    }


    @FXML
    void handleAdd(ActionEvent event) {
        if (activeListView == actorsListView) {
            System.out.println("Actors lista aktivoitu");
            openActorDialog(event);
        } else if (activeListView == genresListView) {
            System.out.println("Genres lista aktivoitu");
            openGenreDialog(event);
        }
    }


    private void openActorDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage ownerStage = (Stage) source.getScene().getWindow();

        try {
            ViewManager.openActorDialog(currentMovie, connection, ownerStage, this);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message: " + e.getMessage());
        }
    }


    protected void addActorToList(Actor actor) {
        boolean isAlreadyListed = selectedActors.stream()
                .anyMatch(existingActor -> existingActor.getId() == actor.getId());

        if (!isAlreadyListed) {
            selectedActors.add(actor);
            actorsListView.getItems().setAll(selectedActors);
        }
    }


    private void openGenreDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage ownerStage = (Stage) source.getScene().getWindow();

        try {
            List<Integer> selectedGenreIds = getSelectedGenres();
            ViewManager.openGenreDialog(currentMovie, connection, ownerStage, this, selectedGenreIds);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message: " + e.getMessage());
        }
    }



    protected void addGenresToList(List<Integer> genreIdList) {
        List<Genre> genresToShow = new ArrayList<>();

        for (Integer genreId : genreIdList) {
            Optional<Genre> genreOpt = genreDao.getGenreById(genreId);
            genreOpt.ifPresent(genresToShow::add);
        }

        genresListView.getItems().setAll(genresToShow);
    }


    @FXML
    void handleDelete(ActionEvent event) {
        if (activeListView != null) {
            if (activeListView.equals(actorsListView)) {
                deleteSelectedItem(actorsListView);
            } else if (activeListView.equals(genresListView)) {
                deleteSelectedItem(genresListView);
            }
        }
    }


    private <T> void deleteSelectedItem(ListView<T> listView) {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            listView.getItems().remove(selectedIndex);
        }
    }


    @FXML
    void handleSave(ActionEvent event) {
        System.out.println("Save button clicked!");

        try {
            String updatedMovieTitle = titleTextField.getText();
            int updatedReleaseYear = Integer.parseInt(releaseYearTextField.getText());
            String updatedDirector = directorTextField.getText();

            List<Integer> updatedActorIds = new ArrayList<>();
            for (Actor actor : actorsListView.getItems()) {
                updatedActorIds.add(actor.getId());
            }

            List<Integer> updatedGenreIds = new ArrayList<>();
            for (Genre genre : genresListView.getItems()) {
                updatedGenreIds.add(genre.getId());
            }

            int newMovieId = movieDao.create(new Movie(updatedMovieTitle, updatedReleaseYear,
                    updatedDirector, updatedActorIds, updatedGenreIds));

            if (newMovieId > 0) {
                closeView();
            }

            // TODO: in case of error

        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
        }
    }


    @FXML
    void handleCancel(ActionEvent event) {
        closeView();
    }


    private void closeView() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    public List<Integer> getSelectedGenres() {
        List<Integer> selectedGenreIds = new ArrayList<>();

        for (Genre genre : genresListView.getItems()) {
            selectedGenreIds.add(genre.getId());
        }

        return selectedGenreIds;
    }


}
