module carrentalsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens carrentalsystem to javafx.fxml;
    opens carrentalsystem.controllers to javafx.fxml;

    exports carrentalsystem;
    exports carrentalsystem.controllers;
    exports carrentalsystem.models;
    exports carrentalsystem.dao;
    exports carrentalsystem.database;
}