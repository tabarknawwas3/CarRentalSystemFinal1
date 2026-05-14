package carrentalsystem.controllers;

import carrentalsystem.dao.CustomerDAO;
import carrentalsystem.models.Customer;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomersController {

    @FXML private TextField txtSearch;

    @FXML private TableView<Customer> tableCustomers;
    @FXML private TableColumn<Customer, Integer> colCustomerId;
    @FXML private TableColumn<Customer, String> colFirstName;
    @FXML private TableColumn<Customer, String> colLastName;
    @FXML private TableColumn<Customer, LocalDate> colDateOfBirth;
    @FXML private TableColumn<Customer, String> colGender;
    @FXML private TableColumn<Customer, String> colPhoneNumber;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colAddress;
    @FXML private TableColumn<Customer, String> colNationalIdOrPassport;
    @FXML private TableColumn<Customer, LocalDate> colRegistrationDate;
    @FXML private TableColumn<Customer, String> colUsername;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadCustomers();
    }

    private void setupTable() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colNationalIdOrPassport.setCellValueFactory(new PropertyValueFactory<>("nationalIdOrPassport"));
        colRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        tableCustomers.setItems(customerList);
    }

    private void loadCustomers() {
        customerList.clear();

        List<Customer> customers = customerDAO.getAllCustomers();
        customerList.addAll(customers);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        customerList.clear();

        if (keyword.isEmpty()) {
            customerList.addAll(customerDAO.getAllCustomers());
        } else {
            customerList.addAll(customerDAO.searchCustomers(keyword));
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadCustomers();
    }
}