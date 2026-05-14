package carrentalsystem.controllers;

import carrentalsystem.dao.EmployeeDAO;
import carrentalsystem.models.Employee;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

public class EmployeesController {

    @FXML private TextField txtSearch;

    @FXML private TableView<Employee> tableEmployees;
    @FXML private TableColumn<Employee, Integer> colEmployeeId;
    @FXML private TableColumn<Employee, String> colFirstName;
    @FXML private TableColumn<Employee, String> colLastName;
    @FXML private TableColumn<Employee, String> colJobTitle;
    @FXML private TableColumn<Employee, String> colPhoneNumber;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, Double> colSalary;
    @FXML private TableColumn<Employee, LocalDate> colHireDate;
    @FXML private TableColumn<Employee, String> colUsername;
    @FXML private TableColumn<Employee, Integer> colBranchId;
    @FXML private TableColumn<Employee, Integer> colAdminId;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtJobTitle;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtEmail;
    @FXML private TextField txtSalary;
    @FXML private DatePicker dpHireDate;
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private TextField txtBranchId;
    @FXML private TextField txtAdminId;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    private Employee selectedEmployee = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupTable();
        hideForm();
        loadEmployees();
    }

    private void setupTable() {
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colJobTitle.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("adminId"));

        tableEmployees.setItems(employeeList);

        tableEmployees.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedEmployee = newSelection
        );
    }

    private void loadEmployees() {
        employeeList.clear();

        List<Employee> employees = employeeDAO.getAllEmployees();
        employeeList.addAll(employees);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        employeeList.clear();

        if (keyword.isEmpty()) {
            employeeList.addAll(employeeDAO.getAllEmployees());
        } else {
            employeeList.addAll(employeeDAO.searchEmployees(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedEmployee = null;

        lblFormTitle.setText("Add New Employee");
        clearForm();
        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedEmployee = tableEmployees.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Employee");

        txtFirstName.setText(selectedEmployee.getFirstName());
        txtLastName.setText(selectedEmployee.getLastName());
        txtJobTitle.setText(selectedEmployee.getJobTitle());
        txtPhoneNumber.setText(selectedEmployee.getPhoneNumber());
        txtEmail.setText(selectedEmployee.getEmail());
        txtSalary.setText(String.valueOf(selectedEmployee.getSalary()));
        dpHireDate.setValue(selectedEmployee.getHireDate());
        txtUsername.setText(selectedEmployee.getUsername());
        txtPassword.setText(selectedEmployee.getPassword());
        txtBranchId.setText(String.valueOf(selectedEmployee.getBranchId()));
        txtAdminId.setText(String.valueOf(selectedEmployee.getAdminId()));

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedEmployee = tableEmployees.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this employee?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = employeeDAO.deleteEmployee(selectedEmployee.getEmployeeId());

            if (deleted) {
                loadEmployees();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete employee.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        Employee emp = new Employee();

        emp.setFirstName(txtFirstName.getText().trim());
        emp.setLastName(txtLastName.getText().trim());
        emp.setJobTitle(txtJobTitle.getText().trim());
        emp.setPhoneNumber(txtPhoneNumber.getText().trim());
        emp.setEmail(txtEmail.getText().trim());
        emp.setSalary(Double.parseDouble(txtSalary.getText().trim()));
        emp.setHireDate(dpHireDate.getValue());
        emp.setUsername(txtUsername.getText().trim());
        emp.setPassword(txtPassword.getText().trim());
        emp.setBranchId(Integer.parseInt(txtBranchId.getText().trim()));

        if (txtAdminId.getText().trim().isEmpty()) {
            emp.setAdminId(0);
        } else {
            emp.setAdminId(Integer.parseInt(txtAdminId.getText().trim()));
        }

        boolean success;

        if (editMode) {
            emp.setEmployeeId(selectedEmployee.getEmployeeId());
            success = employeeDAO.updateEmployee(emp);
        } else {
            success = employeeDAO.addEmployee(emp);
        }

        if (success) {
            loadEmployees();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully.");
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
        if (txtFirstName.getText().trim().isEmpty()
                || txtLastName.getText().trim().isEmpty()
                || txtBranchId.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "First name, last name, and branch ID are required.");
            return false;
        }

        try {
            if (!txtSalary.getText().trim().isEmpty()) {
                Double.parseDouble(txtSalary.getText().trim());
            } else {
                txtSalary.setText("0");
            }

            Integer.parseInt(txtBranchId.getText().trim());

            if (!txtAdminId.getText().trim().isEmpty()) {
                Integer.parseInt(txtAdminId.getText().trim());
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Salary, Branch ID, and Admin ID must be numbers.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtFirstName.clear();
        txtLastName.clear();
        txtJobTitle.clear();
        txtPhoneNumber.clear();
        txtEmail.clear();
        txtSalary.clear();
        dpHireDate.setValue(null);
        txtUsername.clear();
        txtPassword.clear();
        txtBranchId.clear();
        txtAdminId.clear();
    }

    private void showForm() {
    formPanel.setVisible(true);
    formPanel.setManaged(true);

    tableEmployees.setMaxHeight(260);
    tableEmployees.setPrefHeight(260);

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

    tableEmployees.setMaxHeight(Double.MAX_VALUE);
    tableEmployees.setPrefHeight(-1);

    Platform.runLater(() -> {
        tableEmployees.applyCss();
        tableEmployees.requestLayout();
        tableEmployees.getParent().requestLayout();
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