package carrentalsystem.controllers;

import carrentalsystem.dao.BranchDAO;
import carrentalsystem.models.Branch;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class BranchesController {

    @FXML private TextField txtSearch;

    @FXML private TableView<Branch> tableBranches;
    @FXML private TableColumn<Branch, Integer> colBranchId;
    @FXML private TableColumn<Branch, String> colBranchName;
    @FXML private TableColumn<Branch, String> colCity;
    @FXML private TableColumn<Branch, String> colAddress;
    @FXML private TableColumn<Branch, String> colPhoneNumber;
    @FXML private TableColumn<Branch, String> colManagerName;
    @FXML private TableColumn<Branch, String> colOpenHour;
    @FXML private TableColumn<Branch, String> colCloseHour;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtBranchName;
    @FXML private TextField txtCity;
    @FXML private TextField txtAddress;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtManagerName;
    @FXML private TextField txtOpenHour;
    @FXML private TextField txtCloseHour;

    private final BranchDAO branchDAO = new BranchDAO();
    private final ObservableList<Branch> branchList = FXCollections.observableArrayList();

    private Branch selectedBranch = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupTable();
        hideForm();
        loadBranches();
    }

    private void setupTable() {
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        colBranchName.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colManagerName.setCellValueFactory(new PropertyValueFactory<>("managerName"));
        colOpenHour.setCellValueFactory(new PropertyValueFactory<>("openHour"));
        colCloseHour.setCellValueFactory(new PropertyValueFactory<>("closeHour"));

        tableBranches.setItems(branchList);

        tableBranches.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedBranch = newSelection
        );
    }

    private void loadBranches() {
        branchList.clear();

        List<Branch> branches = branchDAO.getAllBranches();
        branchList.addAll(branches);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        branchList.clear();

        if (keyword.isEmpty()) {
            branchList.addAll(branchDAO.getAllBranches());
        } else {
            branchList.addAll(branchDAO.searchBranches(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedBranch = null;

        lblFormTitle.setText("Add New Branch");
        clearForm();
        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedBranch = tableBranches.getSelectionModel().getSelectedItem();

        if (selectedBranch == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a branch to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Branch");

        txtBranchName.setText(selectedBranch.getBranchName());
        txtCity.setText(selectedBranch.getCity());
        txtAddress.setText(selectedBranch.getAddress());
        txtPhoneNumber.setText(selectedBranch.getPhoneNumber());
        txtManagerName.setText(selectedBranch.getManagerName());
        txtOpenHour.setText(selectedBranch.getOpenHour());
        txtCloseHour.setText(selectedBranch.getCloseHour());

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedBranch = tableBranches.getSelectionModel().getSelectedItem();

        if (selectedBranch == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a branch to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this branch?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = branchDAO.deleteBranch(selectedBranch.getBranchId());

            if (deleted) {
                loadBranches();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Branch deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete branch. It may be used by cars, employees, or reservations.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        Branch branch = new Branch();

        branch.setBranchName(txtBranchName.getText().trim());
        branch.setCity(txtCity.getText().trim());
        branch.setAddress(txtAddress.getText().trim());
        branch.setPhoneNumber(txtPhoneNumber.getText().trim());
        branch.setManagerName(txtManagerName.getText().trim());
        branch.setOpenHour(txtOpenHour.getText().trim());
        branch.setCloseHour(txtCloseHour.getText().trim());

        boolean success;

        if (editMode) {
            branch.setBranchId(selectedBranch.getBranchId());
            success = branchDAO.updateBranch(branch);
        } else {
            success = branchDAO.addBranch(branch);
        }

        if (success) {
            loadBranches();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Branch updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Branch added successfully.");
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
        if (txtBranchName.getText().trim().isEmpty()
                || txtCity.getText().trim().isEmpty()
                || txtOpenHour.getText().trim().isEmpty()
                || txtCloseHour.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Branch name, city, open hour, and close hour are required.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtBranchName.clear();
        txtCity.clear();
        txtAddress.clear();
        txtPhoneNumber.clear();
        txtManagerName.clear();
        txtOpenHour.clear();
        txtCloseHour.clear();
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