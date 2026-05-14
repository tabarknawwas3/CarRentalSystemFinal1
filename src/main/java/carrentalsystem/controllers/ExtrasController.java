package carrentalsystem.controllers;

import carrentalsystem.dao.ExtraDAO;
import carrentalsystem.models.Extra;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ExtrasController {

    @FXML private TextField txtSearch;

    @FXML private TableView<Extra> tableExtras;
    @FXML private TableColumn<Extra, Integer> colExtraId;
    @FXML private TableColumn<Extra, String> colExtraName;
    @FXML private TableColumn<Extra, String> colDescription;
    @FXML private TableColumn<Extra, Double> colPricePerDay;
    @FXML private TableColumn<Extra, Boolean> colAvailable;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtExtraName;
    @FXML private TextField txtPricePerDay;
    @FXML private TextArea txtDescription;
    @FXML private CheckBox chkAvailable;

    private final ExtraDAO extraDAO = new ExtraDAO();
    private final ObservableList<Extra> extraList = FXCollections.observableArrayList();

    private Extra selectedExtra = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupTable();
        hideForm();
        loadExtras();
    }

    private void setupTable() {
        colExtraId.setCellValueFactory(new PropertyValueFactory<>("extraId"));
        colExtraName.setCellValueFactory(new PropertyValueFactory<>("extraName"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));

        tableExtras.setItems(extraList);

        tableExtras.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedExtra = newSelection
        );
    }

    private void loadExtras() {
        extraList.clear();

        List<Extra> extras = extraDAO.getAllExtras();
        extraList.addAll(extras);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        extraList.clear();

        if (keyword.isEmpty()) {
            extraList.addAll(extraDAO.getAllExtras());
        } else {
            extraList.addAll(extraDAO.searchExtras(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedExtra = null;

        lblFormTitle.setText("Add New Extra");
        clearForm();
        chkAvailable.setSelected(true);

        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedExtra = tableExtras.getSelectionModel().getSelectedItem();

        if (selectedExtra == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an extra to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Extra");

        txtExtraName.setText(selectedExtra.getExtraName());
        txtDescription.setText(selectedExtra.getDescription());
        txtPricePerDay.setText(String.valueOf(selectedExtra.getPricePerDay()));
        chkAvailable.setSelected(selectedExtra.isAvailable());

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedExtra = tableExtras.getSelectionModel().getSelectedItem();

        if (selectedExtra == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an extra to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this extra?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = extraDAO.deleteExtra(selectedExtra.getExtraId());

            if (deleted) {
                loadExtras();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Extra deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete extra. It may be used in reservations.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        Extra extra = new Extra();

        extra.setExtraName(txtExtraName.getText().trim());
        extra.setDescription(txtDescription.getText().trim());
        extra.setPricePerDay(Double.parseDouble(txtPricePerDay.getText().trim()));
        extra.setAvailable(chkAvailable.isSelected());

        boolean success;

        if (editMode) {
            extra.setExtraId(selectedExtra.getExtraId());
            success = extraDAO.updateExtra(extra);
        } else {
            success = extraDAO.addExtra(extra);
        }

        if (success) {
            loadExtras();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Extra updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Extra added successfully.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed. Check database constraints.");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }

    private boolean validateForm() {
        if (txtExtraName.getText().trim().isEmpty()
                || txtPricePerDay.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Extra name and price per day are required.");
            return false;
        }

        try {
            double price = Double.parseDouble(txtPricePerDay.getText().trim());

            if (price < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Price per day cannot be negative.");
                return false;
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Price per day must be a valid number.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtExtraName.clear();
        txtDescription.clear();
        txtPricePerDay.clear();
        chkAvailable.setSelected(true);
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