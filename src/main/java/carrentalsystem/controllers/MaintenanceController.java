package carrentalsystem.controllers;

import carrentalsystem.dao.MaintenanceDAO;
import carrentalsystem.models.MaintenanceRecord;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

public class MaintenanceController {

    @FXML private TextField txtSearch;

    @FXML private TableView<MaintenanceRecord> tableMaintenance;
    @FXML private TableColumn<MaintenanceRecord, Integer> colMaintenanceId;
    @FXML private TableColumn<MaintenanceRecord, String> colMaintenanceType;
    @FXML private TableColumn<MaintenanceRecord, String> colDescription;
    @FXML private TableColumn<MaintenanceRecord, Double> colMaintenanceCost;
    @FXML private TableColumn<MaintenanceRecord, LocalDate> colDateIn;
    @FXML private TableColumn<MaintenanceRecord, LocalDate> colDateOut;
    @FXML private TableColumn<MaintenanceRecord, Integer> colCarId;
    @FXML private TableColumn<MaintenanceRecord, Integer> colEmployeeId;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtMaintenanceType;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtMaintenanceCost;
    @FXML private DatePicker dpDateIn;
    @FXML private DatePicker dpDateOut;
    @FXML private TextField txtCarId;
    @FXML private TextField txtEmployeeId;

    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private final ObservableList<MaintenanceRecord> maintenanceList = FXCollections.observableArrayList();

    private MaintenanceRecord selectedRecord = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupTable();
        hideForm();
        loadMaintenanceRecords();
    }

    private void setupTable() {
        colMaintenanceId.setCellValueFactory(new PropertyValueFactory<>("maintenanceId"));
        colMaintenanceType.setCellValueFactory(new PropertyValueFactory<>("maintenanceType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colMaintenanceCost.setCellValueFactory(new PropertyValueFactory<>("maintenanceCost"));
        colDateIn.setCellValueFactory(new PropertyValueFactory<>("maintenanceDateIn"));
        colDateOut.setCellValueFactory(new PropertyValueFactory<>("maintenanceDateOut"));
        colCarId.setCellValueFactory(new PropertyValueFactory<>("carId"));
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        tableMaintenance.setItems(maintenanceList);

        tableMaintenance.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedRecord = newSelection
        );
    }

    private void loadMaintenanceRecords() {
        maintenanceList.clear();

        List<MaintenanceRecord> records = maintenanceDAO.getAllMaintenanceRecords();
        maintenanceList.addAll(records);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        maintenanceList.clear();

        if (keyword.isEmpty()) {
            maintenanceList.addAll(maintenanceDAO.getAllMaintenanceRecords());
        } else {
            maintenanceList.addAll(maintenanceDAO.searchMaintenanceRecords(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedRecord = null;

        lblFormTitle.setText("Add New Maintenance Record");
        clearForm();
        dpDateIn.setValue(LocalDate.now());
        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedRecord = tableMaintenance.getSelectionModel().getSelectedItem();

        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a maintenance record to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Maintenance Record");

        txtMaintenanceType.setText(selectedRecord.getMaintenanceType());
        txtDescription.setText(selectedRecord.getDescription());
        txtMaintenanceCost.setText(String.valueOf(selectedRecord.getMaintenanceCost()));
        dpDateIn.setValue(selectedRecord.getMaintenanceDateIn());
        dpDateOut.setValue(selectedRecord.getMaintenanceDateOut());
        txtCarId.setText(String.valueOf(selectedRecord.getCarId()));
        txtEmployeeId.setText(String.valueOf(selectedRecord.getEmployeeId()));

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedRecord = tableMaintenance.getSelectionModel().getSelectedItem();

        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a maintenance record to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this maintenance record?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = maintenanceDAO.deleteMaintenanceRecord(selectedRecord.getMaintenanceId());

            if (deleted) {
                loadMaintenanceRecords();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Maintenance record deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete maintenance record.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        MaintenanceRecord record = new MaintenanceRecord();

        record.setMaintenanceType(txtMaintenanceType.getText().trim());
        record.setDescription(txtDescription.getText().trim());
        record.setMaintenanceCost(parseDouble(txtMaintenanceCost.getText()));
        record.setMaintenanceDateIn(dpDateIn.getValue());
        record.setMaintenanceDateOut(dpDateOut.getValue());
        record.setCarId(Integer.parseInt(txtCarId.getText().trim()));

        if (txtEmployeeId.getText().trim().isEmpty()) {
            record.setEmployeeId(0);
        } else {
            record.setEmployeeId(Integer.parseInt(txtEmployeeId.getText().trim()));
        }

        boolean success;

        if (editMode) {
            record.setMaintenanceId(selectedRecord.getMaintenanceId());
            success = maintenanceDAO.updateMaintenanceRecord(record);
        } else {
            success = maintenanceDAO.addMaintenanceRecord(record);
        }

        if (success) {
            loadMaintenanceRecords();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Maintenance record updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Maintenance record added successfully.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed. Check car ID and employee ID constraints.");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }

    private boolean validateForm() {
        if (txtMaintenanceType.getText().trim().isEmpty()
                || dpDateIn.getValue() == null
                || txtCarId.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Maintenance type, date in, and car ID are required.");
            return false;
        }

        try {
            parseDouble(txtMaintenanceCost.getText());
            Integer.parseInt(txtCarId.getText().trim());

            if (!txtEmployeeId.getText().trim().isEmpty()) {
                Integer.parseInt(txtEmployeeId.getText().trim());
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Cost, Car ID, and Employee ID must be numbers.");
            return false;
        }

        return true;
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(value.trim());
    }

    private void clearForm() {
        txtMaintenanceType.clear();
        txtDescription.clear();
        txtMaintenanceCost.clear();
        dpDateIn.setValue(null);
        dpDateOut.setValue(null);
        txtCarId.clear();
        txtEmployeeId.clear();
    }

    private void showForm() {
    formPanel.setVisible(true);
    formPanel.setManaged(true);

    tableMaintenance.setMaxHeight(260);
    tableMaintenance.setPrefHeight(260);

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

    tableMaintenance.setMaxHeight(Double.MAX_VALUE);
    tableMaintenance.setPrefHeight(-1);

    Platform.runLater(() -> {
        tableMaintenance.applyCss();
        tableMaintenance.requestLayout();
        tableMaintenance.getParent().requestLayout();
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