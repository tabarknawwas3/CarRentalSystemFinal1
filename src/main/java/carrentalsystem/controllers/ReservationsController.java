package carrentalsystem.controllers;
import carrentalsystem.dao.InvoiceDAO;

import carrentalsystem.dao.ReservationDAO;
import carrentalsystem.models.Reservation;
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
import carrentalsystem.models.Invoice;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import carrentalsystem.database.DatabaseConnection;

import carrentalsystem.dao.RentalContractDAO;
import carrentalsystem.models.RentalContract;

public class ReservationsController {

    @FXML private TextField txtSearch;

    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, Integer> colReservationId;
    @FXML private TableColumn<Reservation, LocalDate> colReservationDate;
    @FXML private TableColumn<Reservation, LocalDate> colStartDate;
    @FXML private TableColumn<Reservation, LocalDate> colEndDate;
    @FXML private TableColumn<Reservation, String> colReservationStatus;
    @FXML private TableColumn<Reservation, Integer> colCustomerId;
    @FXML private TableColumn<Reservation, Integer> colCategoryId;
    @FXML private TableColumn<Reservation, Integer> colBranchId;
    @FXML private TableColumn<Reservation, Integer> colCarId;
    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtCustomerId;
    @FXML private TextField txtCategoryId;
    @FXML private TextField txtBranchId;
    @FXML private ComboBox<String> cmbReservationStatus;
    @FXML private DatePicker dpReservationDate;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RentalContractDAO rentalContractDAO = new RentalContractDAO();
    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private Reservation selectedReservation = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        hideForm();
        loadReservations();
        colCarId.setCellValueFactory(new PropertyValueFactory<>("carId"));
    }

    private void setupComboBox() {
        cmbReservationStatus.setItems(FXCollections.observableArrayList(
        "Pending",
        "Confirmed",
        "Cancelled",
        "Converted"
));
    }

    private void setupTable() {
        colReservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        colReservationDate.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colReservationStatus.setCellValueFactory(new PropertyValueFactory<>("reservationStatus"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("branchId"));

        tableReservations.setItems(reservationList);

        tableReservations.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedReservation = newSelection
        );
    }

    private void loadReservations() {
        reservationList.clear();

        List<Reservation> reservations = reservationDAO.getAllReservations();
        reservationList.addAll(reservations);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        reservationList.clear();

        if (keyword.isEmpty()) {
            reservationList.addAll(reservationDAO.getAllReservations());
        } else {
            reservationList.addAll(reservationDAO.searchReservations(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedReservation = null;

        lblFormTitle.setText("Add New Reservation");
        clearForm();

        dpReservationDate.setValue(LocalDate.now());
        cmbReservationStatus.setValue("Pending");

        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedReservation = tableReservations.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Reservation");

        txtCustomerId.setText(String.valueOf(selectedReservation.getCustomerId()));
        txtCategoryId.setText(String.valueOf(selectedReservation.getCategoryId()));
        txtBranchId.setText(String.valueOf(selectedReservation.getBranchId()));
        cmbReservationStatus.setValue(selectedReservation.getReservationStatus());
        dpReservationDate.setValue(selectedReservation.getReservationDate());
        dpStartDate.setValue(selectedReservation.getStartDate());
        dpEndDate.setValue(selectedReservation.getEndDate());

        showForm();
    }
@FXML
private void handleAccept() {
    selectedReservation = tableReservations.getSelectionModel().getSelectedItem();

    if (selectedReservation == null) {
        showAlert(Alert.AlertType.WARNING, "No Selection",
                "Please select a reservation to accept.");
        return;
    }

    String status = selectedReservation.getReservationStatus();

    if (status == null) {
        showAlert(Alert.AlertType.WARNING, "Invalid Reservation",
                "This reservation has no status.");
        return;
    }

    if (status.equalsIgnoreCase("Cancelled")) {
        showAlert(Alert.AlertType.WARNING, "Cannot Accept",
                "Cancelled reservations cannot be converted to contracts.");
        return;
    }

    if (status.equalsIgnoreCase("Converted")) {
        showAlert(Alert.AlertType.WARNING, "Already Converted",
                "This reservation is already converted to a rental contract.");
        return;
    }

    if (selectedReservation.getCarId() <= 0) {
        showAlert(Alert.AlertType.WARNING, "Missing Car",
                "This reservation does not have a selected car. Please make sure Car ID is saved in the reservation.");
        return;
    }

    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Accept Reservation");
    confirm.setHeaderText(null);
    confirm.setContentText("Accept this reservation, create a rental contract, and generate an invoice?");

    if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
        return;
    }

    RentalContract contract = new RentalContract();

    contract.setStartDate(selectedReservation.getStartDate());
    contract.setExpectedReturnDate(selectedReservation.getEndDate());
    contract.setActualReturnDate(null);

    contract.setMileageAtPickup(0);
    contract.setMileageAtReturn(0);
    contract.setContractStatus("Active");

    contract.setCustomerId(selectedReservation.getCustomerId());
    contract.setCarId(selectedReservation.getCarId());

    // Employee ID مؤقتًا 1
    contract.setEmployeeId(1);

    contract.setReservationId(selectedReservation.getReservationId());

    int contractId = rentalContractDAO.addContractAndReturnId(contract);

    if (contractId <= 0) {
        showAlert(Alert.AlertType.ERROR, "Error",
                "Could not create the rental contract. This reservation may already have a contract.");
        return;
    }

    boolean invoiceCreated = createInvoiceForAcceptedReservation(selectedReservation, contractId);

    selectedReservation.setReservationStatus("Converted");
    boolean reservationUpdated = reservationDAO.updateReservation(selectedReservation);

    if (!reservationUpdated) {
        showAlert(Alert.AlertType.WARNING, "Partial Success",
                "Contract and invoice were created, but reservation status was not updated.");
    } else if (!invoiceCreated) {
        showAlert(Alert.AlertType.WARNING, "Partial Success",
                "Contract was created, but invoice could not be generated.");
    } else {
        showAlert(Alert.AlertType.INFORMATION, "Success",
                "Reservation accepted successfully. Contract and invoice were created.");
    }

    loadReservations();
    hideForm();
}
    @FXML
    private void handleDelete() {
        selectedReservation = tableReservations.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this reservation?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = reservationDAO.deleteReservation(selectedReservation.getReservationId());

            if (deleted) {
                loadReservations();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete reservation. It may be linked to a contract or extras.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        Reservation reservation = new Reservation();

        reservation.setCustomerId(Integer.parseInt(txtCustomerId.getText().trim()));
        reservation.setCategoryId(Integer.parseInt(txtCategoryId.getText().trim()));
        reservation.setBranchId(Integer.parseInt(txtBranchId.getText().trim()));
        reservation.setReservationStatus(cmbReservationStatus.getValue());
        reservation.setReservationDate(dpReservationDate.getValue());
        reservation.setStartDate(dpStartDate.getValue());
        reservation.setEndDate(dpEndDate.getValue());

        boolean success;

        if (editMode) {
            reservation.setReservationId(selectedReservation.getReservationId());
            success = reservationDAO.updateReservation(reservation);
        } else {
            success = reservationDAO.addReservation(reservation);
        }

        if (success) {
            loadReservations();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation added successfully.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed. Check customer ID, category ID, branch ID, or database constraints.");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }

    private boolean validateForm() {
        if (txtCustomerId.getText().trim().isEmpty()
                || txtCategoryId.getText().trim().isEmpty()
                || txtBranchId.getText().trim().isEmpty()
                || cmbReservationStatus.getValue() == null
                || dpReservationDate.getValue() == null
                || dpStartDate.getValue() == null
                || dpEndDate.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "All required fields must be filled.");
            return false;
        }

        try {
            Integer.parseInt(txtCustomerId.getText().trim());
            Integer.parseInt(txtCategoryId.getText().trim());
            Integer.parseInt(txtBranchId.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Customer ID, category ID, and branch ID must be numbers.");
            return false;
        }

        if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "End date cannot be before start date.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtCustomerId.clear();
        txtCategoryId.clear();
        txtBranchId.clear();
        cmbReservationStatus.setValue(null);
        dpReservationDate.setValue(null);
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
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
    private boolean createInvoiceForAcceptedReservation(Reservation reservation, int contractId) {
    long days = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());

    if (days <= 0) {
        days = 1;
    }

    double dailyPrice = getCarDailyPrice(reservation.getCarId());
    double rentalCost = dailyPrice * days;

    double extrasPerDay = getReservationExtrasTotalPerDay(reservation.getReservationId());
    double extraCharges = extrasPerDay * days;

    double lateFees = 0.0;
    double discount = 0.0;

    double subtotal = rentalCost + extraCharges + lateFees - discount;
    double tax = subtotal * 0.15;
    double totalAmount = subtotal + tax;

    Invoice invoice = new Invoice();
    invoice.setRentalCost(rentalCost);
    invoice.setExtraCharges(extraCharges);
    invoice.setLateFees(lateFees);
    invoice.setDiscount(discount);
    invoice.setTax(tax);
    invoice.setTotalAmount(totalAmount);
    invoice.setInvoiceStatus("Unpaid");
    invoice.setContractId(contractId);

    return invoiceDAO.addInvoice(invoice);
}
    private double getCarDailyPrice(int carId) {
    String sql = "SELECT daily_price FROM Car WHERE car_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, carId);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("daily_price");
            }
        }

    } catch (SQLException e) {
        System.err.println("Error loading car daily price: " + e.getMessage());
        e.printStackTrace();
    }

    return 0.0;
}
    private double getReservationExtrasTotalPerDay(int reservationId) {
    String sql =
            "SELECT SUM(e.price_per_day * re.quantity) AS extras_total "
            + "FROM Reservation_Extras re "
            + "JOIN Extras e ON re.extra_id = e.extra_id "
            + "WHERE re.reservation_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, reservationId);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("extras_total");
            }
        }

    } catch (SQLException e) {
        System.err.println("Error calculating reservation extras total: " + e.getMessage());
        e.printStackTrace();
    }

    return 0.0;
}
}