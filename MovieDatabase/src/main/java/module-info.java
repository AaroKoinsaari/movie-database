module com.moviedb.moviedatabase {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;

    opens com.moviedb.moviedatabase to javafx.fxml;
    exports com.moviedb.moviedatabase;
}