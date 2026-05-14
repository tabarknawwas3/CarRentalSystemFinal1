package carrentalsystem.controllers;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import javafx.application.Platform;

public class DashboardHomeController {

    @FXML private Label lblTotalCustomers;
    @FXML private Label lblTotalCars;
    @FXML private Label lblTotalReservations;
    @FXML private Label lblTotalContracts;

    @FXML private TableView<Customer> tableCustomers;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colFirstName;
    @FXML private TableColumn<Customer, String> colLastName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colGender;

    @FXML
    public void initialize() {
        loadStats();
        loadCustomers();
    }

    private void loadStats() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Customer");
            if (rs.next()) lblTotalCustomers.setText(String.valueOf(rs.getInt(1)));

            rs = st.executeQuery("SELECT COUNT(*) FROM Car");
            if (rs.next()) lblTotalCars.setText(String.valueOf(rs.getInt(1)));

            rs = st.executeQuery("SELECT COUNT(*) FROM Reservation");
            if (rs.next()) lblTotalReservations.setText(String.valueOf(rs.getInt(1)));

            rs = st.executeQuery("SELECT COUNT(*) FROM Rental_Contract");
            if (rs.next()) lblTotalContracts.setText(String.valueOf(rs.getInt(1)));

        } catch (Exception e) {
            System.err.println("Stats error: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM Customer ORDER BY customer_id");

            ObservableList<Customer> list = FXCollections.observableArrayList();
            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setPhoneNumber(rs.getString("phone_number"));
                c.setEmail(rs.getString("email"));
                c.setGender(rs.getString("gender"));
                list.add(c);
            }

            colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

            tableCustomers.setItems(list);

        } catch (Exception e) {
            System.err.println("Customers error: " + e.getMessage());
        }
    }
}