package carrentalsystem.controllers;

import carrentalsystem.dao.RentalContractDAO;
import carrentalsystem.models.RentalContract;
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
import javafx.application.Platform;

public class ContractsController {

    @FXML private TextField txtSearch;

    @FXML private TableView<RentalContract> tableContracts;
    @FXML private TableColumn<RentalContract, Integer> colId;
    @FXML private TableColumn<RentalContract, LocalDate> colStartDate;
    @FXML private TableColumn<RentalContract, LocalDate> colExpectedReturnDate;
    @FXML private TableColumn<RentalContract, LocalDate> colActualReturnDate;
    @FXML private TableColumn<RentalContract, Integer> colMileagePickup;
    @FXML private TableColumn<RentalContract, Integer> colMileageReturn;
    @FXML private TableColumn<RentalContract, String> colStatus;
    @FXML private TableColumn<RentalContract, String> colCustomer;
    @FXML private TableColumn<RentalContract, String> colCar;
    @FXML private TableColumn<RentalContract, String> colEmployee;
    @FXML private TableColumn<RentalContract, Integer> colReservationId;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpExpectedReturnDate;
    @FXML private DatePicker dpActualReturnDate;
    @FXML private TextField txtMileagePickup;
    @FXML private TextField txtMileageReturn;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private TextField txtCustomerId;
    @FXML private TextField txtCarId;
    @FXML private TextField txtEmployeeId;
    @FXML private TextField txtReservationId;

    private final RentalContractDAO contractDAO = new RentalContractDAO();
    private final ObservableList<RentalContract> contractList = FXCollections.observableArrayList();

    private RentalContract selectedContract = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        hideForm();
        loadContracts();
    }

    private void setupComboBox() {
        cmbStatus.setItems(FXCollections.observableArrayList(
                "Active",
                "Completed",
                "Cancelled"
        ));
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("contractId"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colExpectedReturnDate.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        colActualReturnDate.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
        colMileagePickup.setCellValueFactory(new PropertyValueFactory<>("mileageAtPickup"));
        colMileageReturn.setCellValueFactory(new PropertyValueFactory<>("mileageAtReturn"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("contractStatus"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCar.setCellValueFactory(new PropertyValueFactory<>("carInfo"));
        colEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colReservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        tableContracts.setItems(contractList);

        tableContracts.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedContract = newSelection
        );
    }

    private void loadContracts() {
        contractList.clear();

        List<RentalContract> contracts = contractDAO.getAllContracts();
        contractList.addAll(contracts);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        contractList.clear();

        if (keyword.isEmpty()) {
            contractList.addAll(contractDAO.getAllContracts());
        } else {
            contractList.addAll(contractDAO.searchContracts(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedContract = null;

        lblFormTitle.setText("Add New Contract");
        clearForm();

        dpStartDate.setValue(LocalDate.now());
        cmbStatus.setValue("Active");

        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedContract = tableContracts.getSelectionModel().getSelectedItem();

        if (selectedContract == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a contract to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Contract");

        dpStartDate.setValue(selectedContract.getStartDate());
        dpExpectedReturnDate.setValue(selectedContract.getExpectedReturnDate());
        dpActualReturnDate.setValue(selectedContract.getActualReturnDate());
        txtMileagePickup.setText(String.valueOf(selectedContract.getMileageAtPickup()));

        if (selectedContract.getMileageAtReturn() > 0) {
            txtMileageReturn.setText(String.valueOf(selectedContract.getMileageAtReturn()));
        } else {
            txtMileageReturn.clear();
        }

        cmbStatus.setValue(selectedContract.getContractStatus());
        txtCustomerId.setText(String.valueOf(selectedContract.getCustomerId()));
        txtCarId.setText(String.valueOf(selectedContract.getCarId()));
        txtEmployeeId.setText(String.valueOf(selectedContract.getEmployeeId()));

        if (selectedContract.getReservationId() > 0) {
            txtReservationId.setText(String.valueOf(selectedContract.getReservationId()));
        } else {
            txtReservationId.clear();
        }

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedContract = tableContracts.getSelectionModel().getSelectedItem();

        if (selectedContract == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a contract to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this contract?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = contractDAO.deleteContract(selectedContract.getContractId());

            if (deleted) {
                loadContracts();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Contract deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete contract. It may be used in invoices or reviews.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        RentalContract contract = new RentalContract();

        contract.setStartDate(dpStartDate.getValue());
        contract.setExpectedReturnDate(dpExpectedReturnDate.getValue());
        contract.setActualReturnDate(dpActualReturnDate.getValue());
        contract.setMileageAtPickup(Integer.parseInt(txtMileagePickup.getText().trim()));

        if (txtMileageReturn.getText().trim().isEmpty()) {
            contract.setMileageAtReturn(0);
        } else {
            contract.setMileageAtReturn(Integer.parseInt(txtMileageReturn.getText().trim()));
        }

        contract.setContractStatus(cmbStatus.getValue());
        contract.setCustomerId(Integer.parseInt(txtCustomerId.getText().trim()));
        contract.setCarId(Integer.parseInt(txtCarId.getText().trim()));
        contract.setEmployeeId(Integer.parseInt(txtEmployeeId.getText().trim()));

        if (txtReservationId.getText().trim().isEmpty()) {
            contract.setReservationId(0);
        } else {
            contract.setReservationId(Integer.parseInt(txtReservationId.getText().trim()));
        }

        boolean success;

        if (editMode) {
            contract.setContractId(selectedContract.getContractId());
            success = contractDAO.updateContract(contract);
        } else {
            success = contractDAO.addContract(contract);
        }

        if (success) {
            loadContracts();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Contract updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Contract added successfully.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed. Check customer ID, car ID, employee ID, or reservation ID.");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }

    private boolean validateForm() {
        if (dpStartDate.getValue() == null
                || dpExpectedReturnDate.getValue() == null
                || cmbStatus.getValue() == null
                || txtMileagePickup.getText().trim().isEmpty()
                || txtCustomerId.getText().trim().isEmpty()
                || txtCarId.getText().trim().isEmpty()
                || txtEmployeeId.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields.");
            return false;
        }

        if (dpExpectedReturnDate.getValue().isBefore(dpStartDate.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Expected return date cannot be before start date.");
            return false;
        }

        if (dpActualReturnDate.getValue() != null
                && dpActualReturnDate.getValue().isBefore(dpStartDate.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Actual return date cannot be before start date.");
            return false;
        }

        try {
            Integer.parseInt(txtMileagePickup.getText().trim());

            if (!txtMileageReturn.getText().trim().isEmpty()) {
                Integer.parseInt(txtMileageReturn.getText().trim());
            }

            Integer.parseInt(txtCustomerId.getText().trim());
            Integer.parseInt(txtCarId.getText().trim());
            Integer.parseInt(txtEmployeeId.getText().trim());

            if (!txtReservationId.getText().trim().isEmpty()) {
                Integer.parseInt(txtReservationId.getText().trim());
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Mileage and IDs must be numbers.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        dpStartDate.setValue(null);
        dpExpectedReturnDate.setValue(null);
        dpActualReturnDate.setValue(null);
        txtMileagePickup.clear();
        txtMileageReturn.clear();
        cmbStatus.setValue(null);
        txtCustomerId.clear();
        txtCarId.clear();
        txtEmployeeId.clear();
        txtReservationId.clear();
    }

    private void showForm() {
    formPanel.setVisible(true);
    formPanel.setManaged(true);

    tableContracts.setMaxHeight(260);
    tableContracts.setPrefHeight(260);

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

    tableContracts.setMaxHeight(Double.MAX_VALUE);
    tableContracts.setPrefHeight(-1);

    Platform.runLater(() -> {
        tableContracts.applyCss();
        tableContracts.requestLayout();
        tableContracts.getParent().requestLayout();
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