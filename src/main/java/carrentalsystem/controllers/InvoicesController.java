package carrentalsystem.controllers;

import carrentalsystem.dao.InvoiceDAO;
import carrentalsystem.models.Invoice;
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

public class InvoicesController {

    @FXML private TextField txtSearch;

    @FXML private TableView<Invoice> tableInvoices;
    @FXML private TableColumn<Invoice, Integer> colInvoiceId;
    @FXML private TableColumn<Invoice, LocalDate> colIssueDate;
    @FXML private TableColumn<Invoice, Double> colRentalCost;
    @FXML private TableColumn<Invoice, Double> colExtraCharges;
    @FXML private TableColumn<Invoice, Double> colLateFees;
    @FXML private TableColumn<Invoice, Double> colDiscount;
    @FXML private TableColumn<Invoice, Double> colTax;
    @FXML private TableColumn<Invoice, Double> colTotalAmount;
    @FXML private TableColumn<Invoice, String> colInvoiceStatus;
    @FXML private TableColumn<Invoice, Integer> colContractId;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private DatePicker dpIssueDate;
    @FXML private TextField txtRentalCost;
    @FXML private TextField txtExtraCharges;
    @FXML private TextField txtLateFees;
    @FXML private TextField txtDiscount;
    @FXML private TextField txtTax;
    @FXML private TextField txtTotalAmount;
    @FXML private ComboBox<String> cmbInvoiceStatus;
    @FXML private TextField txtContractId;

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

    private Invoice selectedInvoice = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        hideForm();
        loadInvoices();
    }

    private void setupComboBox() {
        cmbInvoiceStatus.setItems(FXCollections.observableArrayList(
                "Unpaid",
                "PartiallyPaid",
                "Paid"
        ));
    }

    private void setupTable() {
        colInvoiceId.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colRentalCost.setCellValueFactory(new PropertyValueFactory<>("rentalCost"));
        colExtraCharges.setCellValueFactory(new PropertyValueFactory<>("extraCharges"));
        colLateFees.setCellValueFactory(new PropertyValueFactory<>("lateFees"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTax.setCellValueFactory(new PropertyValueFactory<>("tax"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colInvoiceStatus.setCellValueFactory(new PropertyValueFactory<>("invoiceStatus"));
        colContractId.setCellValueFactory(new PropertyValueFactory<>("contractId"));

        tableInvoices.setItems(invoiceList);

        tableInvoices.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedInvoice = newSelection
        );
    }

    private void loadInvoices() {
        invoiceList.clear();

        List<Invoice> invoices = invoiceDAO.getAllInvoices();
        invoiceList.addAll(invoices);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        invoiceList.clear();

        if (keyword.isEmpty()) {
            invoiceList.addAll(invoiceDAO.getAllInvoices());
        } else {
            invoiceList.addAll(invoiceDAO.searchInvoices(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedInvoice = null;

        lblFormTitle.setText("Add New Invoice");
        clearForm();
        dpIssueDate.setValue(LocalDate.now());
        calculateTotal();
        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedInvoice = tableInvoices.getSelectionModel().getSelectedItem();

        if (selectedInvoice == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an invoice to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Invoice");

        dpIssueDate.setValue(selectedInvoice.getIssueDate());
        txtRentalCost.setText(String.valueOf(selectedInvoice.getRentalCost()));
        txtExtraCharges.setText(String.valueOf(selectedInvoice.getExtraCharges()));
        txtLateFees.setText(String.valueOf(selectedInvoice.getLateFees()));
        txtDiscount.setText(String.valueOf(selectedInvoice.getDiscount()));
        txtTax.setText(String.valueOf(selectedInvoice.getTax()));
        txtTotalAmount.setText(String.valueOf(selectedInvoice.getTotalAmount()));
        cmbInvoiceStatus.setValue(selectedInvoice.getInvoiceStatus());
        txtContractId.setText(String.valueOf(selectedInvoice.getContractId()));

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedInvoice = tableInvoices.getSelectionModel().getSelectedItem();

        if (selectedInvoice == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an invoice to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this invoice?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = invoiceDAO.deleteInvoice(selectedInvoice.getInvoiceId());

            if (deleted) {
                loadInvoices();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete invoice.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        Invoice invoice = new Invoice();

        invoice.setIssueDate(dpIssueDate.getValue());
        invoice.setRentalCost(parseDouble(txtRentalCost.getText()));
        invoice.setExtraCharges(parseDouble(txtExtraCharges.getText()));
        invoice.setLateFees(parseDouble(txtLateFees.getText()));
        invoice.setDiscount(parseDouble(txtDiscount.getText()));
        invoice.setTax(parseDouble(txtTax.getText()));
        invoice.setTotalAmount(parseDouble(txtTotalAmount.getText()));
        invoice.setInvoiceStatus(cmbInvoiceStatus.getValue());
        invoice.setContractId(Integer.parseInt(txtContractId.getText().trim()));

        boolean success;

        if (editMode) {
            invoice.setInvoiceId(selectedInvoice.getInvoiceId());
            success = invoiceDAO.updateInvoice(invoice);
        } else {
            success = invoiceDAO.addInvoice(invoice);
        }

        if (success) {
            loadInvoices();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice added successfully.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed. Check contract ID constraints.");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }

    @FXML
    private void handleCalculateTotal() {
        calculateTotal();
    }

    private void calculateTotal() {
        double rentalCost = parseDouble(txtRentalCost.getText());
        double extraCharges = parseDouble(txtExtraCharges.getText());
        double lateFees = parseDouble(txtLateFees.getText());
        double discount = parseDouble(txtDiscount.getText());
        double tax = parseDouble(txtTax.getText());

        double total = rentalCost + extraCharges + lateFees + tax - discount;

        if (total < 0) {
            total = 0;
        }

        txtTotalAmount.setText(String.format("%.2f", total));
    }

    private boolean validateForm() {
        if (dpIssueDate.getValue() == null
                || cmbInvoiceStatus.getValue() == null
                || txtContractId.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Issue date, status, and contract ID are required.");
            return false;
        }

        try {
            parseDouble(txtRentalCost.getText());
            parseDouble(txtExtraCharges.getText());
            parseDouble(txtLateFees.getText());
            parseDouble(txtDiscount.getText());
            parseDouble(txtTax.getText());
            parseDouble(txtTotalAmount.getText());
            Integer.parseInt(txtContractId.getText().trim());

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Amounts and Contract ID must be numbers.");
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
        dpIssueDate.setValue(null);
        txtRentalCost.clear();
        txtExtraCharges.clear();
        txtLateFees.clear();
        txtDiscount.clear();
        txtTax.clear();
        txtTotalAmount.clear();
        cmbInvoiceStatus.setValue(null);
        txtContractId.clear();
    }

    private void showForm() {
    formPanel.setVisible(true);
    formPanel.setManaged(true);

    tableInvoices.setMaxHeight(260);
    tableInvoices.setPrefHeight(260);

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

    tableInvoices.setMaxHeight(Double.MAX_VALUE);
    tableInvoices.setPrefHeight(-1);

    Platform.runLater(() -> {
        tableInvoices.applyCss();
        tableInvoices.requestLayout();
        tableInvoices.getParent().requestLayout();
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