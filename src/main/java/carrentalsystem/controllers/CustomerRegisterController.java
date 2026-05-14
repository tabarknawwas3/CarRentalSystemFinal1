package carrentalsystem.controllers;

import carrentalsystem.dao.CustomerDAO;
import carrentalsystem.models.Customer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;

public class CustomerRegisterController {

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private ComboBox<String> cmbGender;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private TextField txtNationalId;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        cmbGender.setItems(FXCollections.observableArrayList("Male", "Female"));
        hideMessage();
    }

    @FXML
    private void handleRegister() {
        if (!validateForm()) {
            return;
        }

        Customer customer = new Customer();
        customer.setFirstName(txtFirstName.getText().trim());
        customer.setLastName(txtLastName.getText().trim());
        customer.setDateOfBirth(dpDateOfBirth.getValue());
        customer.setGender(cmbGender.getValue());
        customer.setPhoneNumber(txtPhone.getText().trim());
        customer.setEmail(txtEmail.getText().trim());
        customer.setAddress(txtAddress.getText().trim());
        customer.setNationalIdOrPassport(txtNationalId.getText().trim());
        customer.setUsername(txtUsername.getText().trim());
        customer.setPassword(txtPassword.getText().trim());

        boolean success = customerDAO.registerCustomer(customer);

        if (success) {
            showSuccess("Account created successfully. You can login now.");
            clearForm();
        } else {
            showError("Could not create account. Username, email, or national ID may already exist.");
        }
    }

    private boolean validateForm() {
        if (txtFirstName.getText().trim().isEmpty()
                || txtLastName.getText().trim().isEmpty()
                || cmbGender.getValue() == null
                || txtUsername.getText().trim().isEmpty()
                || txtPassword.getText().trim().isEmpty()) {

            showError("Please fill all required fields.");
            return false;
        }

        if (txtPassword.getText().trim().length() < 4) {
            showError("Password must be at least 4 characters.");
            return false;
        }

        return true;
    }

    @FXML
    private void backToLogin() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/carrentalsystem/Login.fxml")
            );

            Stage stage = (Stage) txtFirstName.getScene().getWindow();
            stage.setTitle("DriveEase — Car Rental System");
            stage.setScene(new Scene(root, 1100, 680));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtFirstName.clear();
        txtLastName.clear();
        dpDateOfBirth.setValue(null);
        cmbGender.setValue(null);
        txtPhone.clear();
        txtEmail.clear();
        txtAddress.clear();
        txtNationalId.clear();
        txtUsername.clear();
        txtPassword.clear();
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle(
                "-fx-text-fill: #ff6b6b;"
                + "-fx-background-color: #2d1515;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 9 12;"
        );
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle(
                "-fx-text-fill: #63e6be;"
                + "-fx-background-color: #10261f;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 9 12;"
        );
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);
    }

    private void hideMessage() {
        lblMessage.setText("");
        lblMessage.setVisible(false);
        lblMessage.setManaged(false);
    }
}