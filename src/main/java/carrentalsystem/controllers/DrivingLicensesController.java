package carrentalsystem.controllers;

import carrentalsystem.dao.DrivingLicenseDAO;
import carrentalsystem.models.DrivingLicense;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

public class DrivingLicensesController {

    @FXML private TextField txtSearch;

    @FXML private TableView<DrivingLicense> tableLicenses;
    @FXML private TableColumn<DrivingLicense, Integer> colLicenseId;
    @FXML private TableColumn<DrivingLicense, String> colLicenseNumber;
    @FXML private TableColumn<DrivingLicense, LocalDate> colIssueDate;
    @FXML private TableColumn<DrivingLicense, LocalDate> colExpiryDate;
    @FXML private TableColumn<DrivingLicense, String> colCountryOfIssue;
    @FXML private TableColumn<DrivingLicense, Integer> colCustomerId;
    @FXML private TableColumn<DrivingLicense, String> colCustomerName;
    @FXML private TableColumn<DrivingLicense, LocalDate> colValidity;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtLicenseNumber;
    @FXML private DatePicker dpIssueDate;
    @FXML private DatePicker dpExpiryDate;
    @FXML private TextField txtCountryOfIssue;
    @FXML private TextField txtCustomerId;

    private final DrivingLicenseDAO licenseDAO = new DrivingLicenseDAO();
    private final ObservableList<DrivingLicense> licenseList = FXCollections.observableArrayList();

    private DrivingLicense selectedLicense = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupTable();
        hideForm();
        loadLicenses();
    }

    private void setupTable() {
        colLicenseId.setCellValueFactory(new PropertyValueFactory<>("licenseId"));
        colLicenseNumber.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colCountryOfIssue.setCellValueFactory(new PropertyValueFactory<>("countryOfIssue"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        colValidity.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colValidity.setCellFactory(column -> new TableCell<DrivingLicense, LocalDate>() {
            @Override
            protected void updateItem(LocalDate expiryDate, boolean empty) {
                super.updateItem(expiryDate, empty);

                if (empty || expiryDate == null) {
                    setText("");
                    setStyle("");
                    return;
                }

                if (expiryDate.isBefore(LocalDate.now())) {
                    setText("Expired");
                    setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
                } else {
                    setText("Valid");
                    setStyle("-fx-text-fill: #63e6be; -fx-font-weight: bold;");
                }
            }
        });

        tableLicenses.setItems(licenseList);

        tableLicenses.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedLicense = newSelection
        );
    }

    private void loadLicenses() {
        licenseList.clear();

        List<DrivingLicense> licenses = licenseDAO.getAllLicenses();
        licenseList.addAll(licenses);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        licenseList.clear();

        if (keyword.isEmpty()) {
            licenseList.addAll(licenseDAO.getAllLicenses());
        } else {
            licenseList.addAll(licenseDAO.searchLicenses(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedLicense = null;

        lblFormTitle.setText("Add New Driving License");
        clearForm();
        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedLicense = tableLicenses.getSelectionModel().getSelectedItem();

        if (selectedLicense == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a license to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Driving License");

        txtLicenseNumber.setText(selectedLicense.getLicenseNumber());
        dpIssueDate.setValue(selectedLicense.getIssueDate());
        dpExpiryDate.setValue(selectedLicense.getExpiryDate());
        txtCountryOfIssue.setText(selectedLicense.getCountryOfIssue());
        txtCustomerId.setText(String.valueOf(selectedLicense.getCustomerId()));

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedLicense = tableLicenses.getSelectionModel().getSelectedItem();

        if (selectedLicense == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a license to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this driving license?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = licenseDAO.deleteLicense(selectedLicense.getLicenseId());

            if (deleted) {
                loadLicenses();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Driving license deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete driving license.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        DrivingLicense license = new DrivingLicense();

        license.setLicenseNumber(txtLicenseNumber.getText().trim());
        license.setIssueDate(dpIssueDate.getValue());
        license.setExpiryDate(dpExpiryDate.getValue());
        license.setCountryOfIssue(txtCountryOfIssue.getText().trim());
        license.setCustomerId(Integer.parseInt(txtCustomerId.getText().trim()));

        boolean success;

        if (editMode) {
            license.setLicenseId(selectedLicense.getLicenseId());
            success = licenseDAO.updateLicense(license);
        } else {
            success = licenseDAO.addLicense(license);
        }

        if (success) {
            loadLicenses();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Driving license updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Driving license added successfully.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed. Check customer ID or license number.");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }

    private boolean validateForm() {
        if (txtLicenseNumber.getText().trim().isEmpty()
                || dpIssueDate.getValue() == null
                || dpExpiryDate.getValue() == null
                || txtCountryOfIssue.getText().trim().isEmpty()
                || txtCustomerId.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required.");
            return false;
        }

        try {
            Integer.parseInt(txtCustomerId.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Customer ID must be a number.");
            return false;
        }

        if (dpExpiryDate.getValue().isBefore(dpIssueDate.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Expiry date cannot be before issue date.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtLicenseNumber.clear();
        dpIssueDate.setValue(null);
        dpExpiryDate.setValue(null);
        txtCountryOfIssue.clear();
        txtCustomerId.clear();
    }

    private void showForm() {
    formPanel.setVisible(true);
    formPanel.setManaged(true);

    tableLicenses.setMaxHeight(260);
    tableLicenses.setPrefHeight(260);

    Platform.runLater(() -> {
        formPanel.applyCss();
        formPanel.requestLayout();
        formPanel.getParent().requestLayout();
        formPanel.requestFocus();
    });
}

private void hideForm() {
    formPanel.setVisible(false);
    formPanel.setManaged(false);

    tableLicenses.setMaxHeight(Double.MAX_VALUE);
    tableLicenses.setPrefHeight(-1);

    Platform.runLater(() -> {
        tableLicenses.applyCss();
        tableLicenses.requestLayout();
        tableLicenses.getParent().requestLayout();
    });
}

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}