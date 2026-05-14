package carrentalsystem.controllers;

import carrentalsystem.dao.AuthDAO;
import carrentalsystem.models.Admin;
import carrentalsystem.models.Customer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.application.Platform;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML private Button btnAdmin;
    @FXML private Button btnCustomer;
    @FXML private Button btnCreateAccount;

    @FXML private ImageView imgLoginCar;

    private final AuthDAO authDAO = new AuthDAO();

    private String selectedRole = "Admin";

    @FXML
    public void initialize() {
        makeLoginImageCircle();
        switchToAdmin();
        hideError();
    }

    private void makeLoginImageCircle() {
        if (imgLoginCar != null) {
            Circle clip = new Circle(56, 56, 56);
            imgLoginCar.setClip(clip);
        }
    }

    @FXML
    private void switchToAdmin() {
        selectedRole = "Admin";

        if (btnAdmin != null) {
            btnAdmin.setStyle(
                    "-fx-background-color: #3d5af1;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-font-size: 13px;"
                    + "-fx-background-radius: 9;"
                    + "-fx-border-width: 0;"
                    + "-fx-cursor: hand;"
            );
        }

        if (btnCustomer != null) {
            btnCustomer.setStyle(
                    "-fx-background-color: transparent;"
                    + "-fx-text-fill: #8892b0;"
                    + "-fx-font-size: 13px;"
                    + "-fx-border-width: 0;"
                    + "-fx-cursor: hand;"
            );
        }

        hideError();
    }

    @FXML
    private void switchToCustomer() {
        selectedRole = "Customer";

        if (btnCustomer != null) {
            btnCustomer.setStyle(
                    "-fx-background-color: #3d5af1;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-font-size: 13px;"
                    + "-fx-background-radius: 9;"
                    + "-fx-border-width: 0;"
                    + "-fx-cursor: hand;"
            );
        }

        if (btnAdmin != null) {
            btnAdmin.setStyle(
                    "-fx-background-color: transparent;"
                    + "-fx-text-fill: #8892b0;"
                    + "-fx-font-size: 13px;"
                    + "-fx-border-width: 0;"
                    + "-fx-cursor: hand;"
            );
        }

        hideError();
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        if ("Admin".equals(selectedRole)) {
            Admin admin = authDAO.loginAdmin(username, password);

            if (admin != null) {
                openAdminDashboard(admin);
            } else {
                showError("Invalid admin username or password.");
            }

        } else {
            Customer customer = authDAO.loginCustomer(username, password);

            if (customer != null) {
                openCustomerDashboard(customer);
            } else {
                showError("Invalid customer username or password.");
            }
        }
    }

    private void openAdminDashboard(Admin admin) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/carrentalsystem/AdminDashboard.fxml")
            );

            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.initData(admin);

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setTitle("DriveEase — Admin Dashboard");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Admin Dashboard.");
        }
    }

    private void openCustomerDashboard(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/carrentalsystem/CustomerDashboard.fxml")
            );

            Parent root = loader.load();

            CustomerDashboardController controller = loader.getController();
            controller.initData(customer);

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setTitle("DriveEase — Customer Dashboard");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Customer Dashboard.");
        }
    }

    @FXML
    private void openCustomerRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/carrentalsystem/CustomerRegister.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setMaximized(false);
            stage.setTitle("DriveEase — Create Customer Account");
            stage.setScene(new Scene(root, 900, 650));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open registration page.");
        }
    }

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
            lblError.setManaged(true);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Error", message);
        }
    }

    private void hideError() {
        if (lblError != null) {
            lblError.setText("");
            lblError.setVisible(false);
            lblError.setManaged(false);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}