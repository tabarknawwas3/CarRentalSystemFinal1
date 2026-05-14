package carrentalsystem.controllers;

import carrentalsystem.dao.CarStatusDAO;
import carrentalsystem.models.CarStatus;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class CarStatusController {

    @FXML private TextField txtSearch;

    @FXML private TableView<CarStatus> tableCarStatus;
    @FXML private TableColumn<CarStatus, Integer> colStatusId;
    @FXML private TableColumn<CarStatus, Integer> colCarId;
    @FXML private TableColumn<CarStatus, String> colCarInfo;
    @FXML private TableColumn<CarStatus, String> colStatus;
    @FXML private TableColumn<CarStatus, LocalDate> colStartDate;
    @FXML private TableColumn<CarStatus, LocalDate> colEndDate;
    @FXML private TableColumn<CarStatus, Integer> colEmployeeId;
    @FXML private TableColumn<CarStatus, String> colEmployeeName;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtCarId;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private TextField txtEmployeeId;

    private final CarStatusDAO carStatusDAO = new CarStatusDAO();
    private final ObservableList<CarStatus> statusList = FXCollections.observableArrayList();

    private CarStatus selectedStatus = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        hideForm();
        loadCarStatuses();
    }

    private void setupComboBox() {
        cmbStatus.setItems(FXCollections.observableArrayList(
                "Available",
                "Reserved",
                "Rented",
                "Maintenance"
        ));
    }

    private void setupTable() {
        colStatusId.setCellValueFactory(new PropertyValueFactory<>("statusId"));
        colCarId.setCellValueFactory(new PropertyValueFactory<>("carId"));
        colCarInfo.setCellValueFactory(new PropertyValueFactory<>("carInfo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        tableCarStatus.setItems(statusList);

        tableCarStatus.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedStatus = newSelection
        );
    }

    private void loadCarStatuses() {
        statusList.clear();

        List<CarStatus> statuses = carStatusDAO.getAllCarStatuses();
        statusList.addAll(statuses);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        statusList.clear();

        if (keyword.isEmpty()) {
            statusList.addAll(carStatusDAO.getAllCarStatuses());
        } else {
            statusList.addAll(carStatusDAO.searchCarStatuses(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedStatus = null;

        lblFormTitle.setText("Add New Car Status");
        clearForm();
        dpStartDate.setValue(LocalDate.now());

        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedStatus = tableCarStatus.getSelectionModel().getSelectedItem();

        if (selectedStatus == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a car status to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Car Status");

        txtCarId.setText(String.valueOf(selectedStatus.getCarId()));
        cmbStatus.setValue(selectedStatus.getStatus());
        dpStartDate.setValue(selectedStatus.getStartDate());
        dpEndDate.setValue(selectedStatus.getEndDate());

        if (selectedStatus.getEmployeeId() > 0) {
            txtEmployeeId.setText(String.valueOf(selectedStatus.getEmployeeId()));
        } else {
            txtEmployeeId.clear();
        }

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedStatus = tableCarStatus.getSelectionModel().getSelectedItem();

        if (selectedStatus == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a car status to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this car status?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = carStatusDAO.deleteCarStatus(selectedStatus.getStatusId());

            if (deleted) {
                loadCarStatuses();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Car status deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete car status.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        CarStatus status = new CarStatus();

        status.setCarId(Integer.parseInt(txtCarId.getText().trim()));
        status.setStatus(cmbStatus.getValue());
        status.setStartDate(dpStartDate.getValue());
        status.setEndDate(dpEndDate.getValue());

        if (txtEmployeeId.getText().trim().isEmpty()) {
            status.setEmployeeId(0);
        } else {
            status.setEmployeeId(Integer.parseInt(txtEmployeeId.getText().trim()));
        }

        boolean success;

        if (editMode) {
            status.setStatusId(selectedStatus.getStatusId());
            success = carStatusDAO.updateCarStatus(status);
        } else {
            success = carStatusDAO.addCarStatus(status);
        }

        if (success) {
            loadCarStatuses();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Car status updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Car status added successfully.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed. Check car ID or employee ID.");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }

    private boolean validateForm() {
        if (txtCarId.getText().trim().isEmpty()
                || cmbStatus.getValue() == null
                || dpStartDate.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Car ID, status, and start date are required.");
            return false;
        }

        try {
            Integer.parseInt(txtCarId.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Car ID must be a number.");
            return false;
        }

        if (!txtEmployeeId.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(txtEmployeeId.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Responsible employee ID must be a number.");
                return false;
            }
        }

        if (dpEndDate.getValue() != null && dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "End date cannot be before start date.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtCarId.clear();
        cmbStatus.setValue(null);
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        txtEmployeeId.clear();
    }

    private void showForm() {
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    private void hideForm() {
        formPanel.setVisible(false);
        formPanel.setManaged(false);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}