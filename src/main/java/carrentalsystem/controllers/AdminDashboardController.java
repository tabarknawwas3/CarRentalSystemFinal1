package carrentalsystem.controllers;

import carrentalsystem.models.Admin;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminDashboardController {

    @FXML private StackPane contentArea;

    @FXML private Button btnDashboard;
    @FXML private Button btnCustomers;
    @FXML private Button btnDrivingLicenses;
    @FXML private Button btnBranches;
    @FXML private Button btnCategories;
    @FXML private Button btnCars;
    @FXML private Button btnCarStatus;
    @FXML private Button btnReservations;
    @FXML private Button btnContracts;
    @FXML private Button btnInvoices;
    @FXML private Button btnMaintenance;
    @FXML private Button btnEmployees;
    @FXML private Button btnExtras;
    @FXML private Button btnReviews;
    @FXML private Button btnReports;

    @FXML private Label lblAdminName;
    @FXML private Label lblAdminRole;

    private Admin currentAdmin;

    private final String normalButtonStyle =
            "-fx-background-color: transparent;"
            + "-fx-text-fill: #a8b2d1;"
            + "-fx-font-size: 12px;"
            + "-fx-alignment: CENTER_LEFT;"
            + "-fx-padding: 6 12;"
            + "-fx-background-radius: 8;"
            + "-fx-border-width: 0;"
            + "-fx-cursor: hand;";

    private final String activeButtonStyle =
            "-fx-background-color: #24376f;"
            + "-fx-text-fill: #e6f1ff;"
            + "-fx-font-weight: bold;"
            + "-fx-font-size: 12px;"
            + "-fx-alignment: CENTER_LEFT;"
            + "-fx-padding: 6 12;"
            + "-fx-background-radius: 8;"
            + "-fx-border-width: 0;"
            + "-fx-cursor: hand;";

    @FXML
    public void initialize() {
        if (lblAdminName != null) {
            lblAdminName.setText("Admin");
        }

        if (lblAdminRole != null) {
            lblAdminRole.setText("Administrator");
        }

        loadPage("DashboardHome.fxml");
        setActiveButton(btnDashboard);
    }

    public void initData(Admin admin) {
        this.currentAdmin = admin;

        if (lblAdminRole != null) {
            lblAdminRole.setText("Administrator");
        }

        if (lblAdminName != null) {
            if (admin != null) {
                String fullName = "";

                try {
                    fullName = admin.getFullName();
                } catch (Exception e) {
                    fullName = "";
                }

                if (fullName == null || fullName.trim().isEmpty()) {
                    String firstName = admin.getFirstName() == null ? "" : admin.getFirstName();
                    String lastName = admin.getLastName() == null ? "" : admin.getLastName();
                    fullName = (firstName + " " + lastName).trim();
                }

                if (fullName == null || fullName.trim().isEmpty()) {
                    fullName = admin.getUsername();
                }

                if (fullName == null || fullName.trim().isEmpty()) {
                    fullName = "Admin";
                }

                lblAdminName.setText(fullName);

            } else {
                lblAdminName.setText("Admin");
            }
        }
    }

    private void loadPage(String fxmlFile) {
        try {
            Parent page = FXMLLoader.load(
                    getClass().getResource("/carrentalsystem/" + fxmlFile)
            );

            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();

            Label errorLabel = new Label(
                    "Error loading page: " + fxmlFile + "\n\n" + e.getMessage()
            );

            errorLabel.setStyle(
                    "-fx-text-fill: #ff6b6b;"
                    + "-fx-font-size: 16px;"
                    + "-fx-padding: 30;"
            );

            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorLabel);
        }
    }

    private void setActiveButton(Button activeButton) {
        Button[] buttons = {
            btnDashboard,
            btnCustomers,
            btnDrivingLicenses,
            btnBranches,
            btnCategories,
            btnCars,
            btnCarStatus,
            btnReservations,
            btnContracts,
            btnInvoices,
            btnMaintenance,
            btnEmployees,
            btnExtras,
            btnReviews,
            btnReports
        };

        for (Button button : buttons) {
            if (button != null) {
                button.setStyle(normalButtonStyle);
                button.setMinHeight(30);
                button.setPrefHeight(30);
                button.setMaxHeight(30);
            }
        }

        if (activeButton != null) {
            activeButton.setStyle(activeButtonStyle);
            activeButton.setMinHeight(30);
            activeButton.setPrefHeight(30);
            activeButton.setMaxHeight(30);
        }
    }

    @FXML
    private void showDashboard() {
        loadPage("DashboardHome.fxml");
        setActiveButton(btnDashboard);
    }

    @FXML
    private void showCustomers() {
        loadPage("Customers.fxml");
        setActiveButton(btnCustomers);
    }

    @FXML
    private void showDrivingLicenses() {
        loadPage("DrivingLicenses.fxml");
        setActiveButton(btnDrivingLicenses);
    }

    @FXML
    private void showBranches() {
        loadPage("Branches.fxml");
        setActiveButton(btnBranches);
    }

    @FXML
    private void showCategories() {
        loadPage("CarCategories.fxml");
        setActiveButton(btnCategories);
    }

    @FXML
    private void showCars() {
        loadPage("Cars.fxml");
        setActiveButton(btnCars);
    }

    @FXML
    private void showCarStatus() {
        loadPage("CarStatus.fxml");
        setActiveButton(btnCarStatus);
    }

    @FXML
    private void showReservations() {
        loadPage("Reservations.fxml");
        setActiveButton(btnReservations);
    }

    @FXML
    private void showContracts() {
        loadPage("Contracts.fxml");
        setActiveButton(btnContracts);
    }

    @FXML
    private void showInvoices() {
        loadPage("Invoices.fxml");
        setActiveButton(btnInvoices);
    }

    @FXML
    private void showMaintenance() {
        loadPage("Maintenance.fxml");
        setActiveButton(btnMaintenance);
    }

    @FXML
    private void showEmployees() {
        loadPage("Employees.fxml");
        setActiveButton(btnEmployees);
    }

    @FXML
    private void showExtras() {
        loadPage("Extras.fxml");
        setActiveButton(btnExtras);
    }

    @FXML
    private void showReviews() {
        loadPage("Reviews.fxml");
        setActiveButton(btnReviews);
    }

    @FXML
    private void showReports() {
        loadPage("Reports.fxml");
        setActiveButton(btnReports);
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/carrentalsystem/Login.fxml")
            );

            Stage stage = (Stage) contentArea.getScene().getWindow();

            stage.setMaximized(false);
            stage.setTitle("DriveEase — Car Rental System");
            stage.setScene(new Scene(root, 1100, 680));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}