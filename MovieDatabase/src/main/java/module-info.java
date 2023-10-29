module com.moviedb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.xerial.sqlitejdbc;
    requires fxgui;

    opens com.moviedb.app to javafx.fxml;
    opens com.moviedb.controllers to javafx.fxml;
    exports com.moviedb.app;
    exports com.moviedb.controllers;
}