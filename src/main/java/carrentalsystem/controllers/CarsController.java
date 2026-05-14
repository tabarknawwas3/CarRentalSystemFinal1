package carrentalsystem.controllers;

import carrentalsystem.dao.CarDAO;
import carrentalsystem.models.Car;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class CarsController {

    @FXML private TextField txtSearch;

    @FXML private TableView<Car> tableCars;
    @FXML private TableColumn<Car, Integer> colCarId;
    @FXML private TableColumn<Car, String> colPlateNumber;
    @FXML private TableColumn<Car, String> colBrand;
    @FXML private TableColumn<Car, String> colModel;
    @FXML private TableColumn<Car, String> colColor;
    @FXML private TableColumn<Car, Double> colDailyPrice;
    @FXML private TableColumn<Car, Integer> colManufactureYear;
    @FXML private TableColumn<Car, String> colFuelType;
    @FXML private TableColumn<Car, String> colTransmissionType;
    @FXML private TableColumn<Car, Integer> colMileage;
    @FXML private TableColumn<Car, Integer> colCategoryId;
    @FXML private TableColumn<Car, Integer> colBranchId;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtPlateNumber;
    @FXML private TextField txtBrand;
    @FXML private TextField txtModel;
    @FXML private TextField txtColor;
    @FXML private TextField txtDailyPrice;
    @FXML private TextField txtManufactureYear;
    @FXML private ComboBox<String> cmbFuelType;
    @FXML private ComboBox<String> cmbTransmissionType;
    @FXML private TextField txtMileage;
    @FXML private TextField txtCategoryId;
    @FXML private TextField txtBranchId;

    private final CarDAO carDAO = new CarDAO();
    private final ObservableList<Car> carList = FXCollections.observableArrayList();

    private Car selectedCar = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTable();
        hideForm();
        loadCars();
    }

    private void setupComboBoxes() {
        cmbFuelType.setItems(FXCollections.observableArrayList(
                "Petrol",
                "Diesel",
                "Hybrid",
                "Electric"
        ));

        cmbTransmissionType.setItems(FXCollections.observableArrayList(
                "Automatic",
                "Manual"
        ));
    }

    private void setupTable() {
        colCarId.setCellValueFactory(new PropertyValueFactory<>("carId"));
        colPlateNumber.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colDailyPrice.setCellValueFactory(new PropertyValueFactory<>("dailyPrice"));
        colManufactureYear.setCellValueFactory(new PropertyValueFactory<>("manufactureYear"));
        colFuelType.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
        colTransmissionType.setCellValueFactory(new PropertyValueFactory<>("transmissionType"));
        colMileage.setCellValueFactory(new PropertyValueFactory<>("mileage"));
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("branchId"));

        tableCars.setItems(carList);

        tableCars.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedCar = newSelection
        );
    }

    private void loadCars() {
        carList.clear();

        List<Car> cars = carDAO.getAllCars();
        carList.addAll(cars);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        carList.clear();

        if (keyword.isEmpty()) {
            carList.addAll(carDAO.getAllCars());
        } else {
            carList.addAll(carDAO.searchCars(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedCar = null;

        lblFormTitle.setText("Add New Car");
        clearForm();
        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedCar = tableCars.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a car to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Car");

        txtPlateNumber.setText(selectedCar.getPlateNumber());
        txtBrand.setText(selectedCar.getBrand());
        txtModel.setText(selectedCar.getModel());
        txtColor.setText(selectedCar.getColor());
        txtDailyPrice.setText(String.valueOf(selectedCar.getDailyPrice()));
        txtManufactureYear.setText(String.valueOf(selectedCar.getManufactureYear()));
        cmbFuelType.setValue(selectedCar.getFuelType());
        cmbTransmissionType.setValue(selectedCar.getTransmissionType());
        txtMileage.setText(String.valueOf(selectedCar.getMileage()));
        txtCategoryId.setText(String.valueOf(selectedCar.getCategoryId()));
        txtBranchId.setText(String.valueOf(selectedCar.getBranchId()));

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedCar = tableCars.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a car to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this car?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = carDAO.deleteCar(selectedCar.getCarId());

            if (deleted) {
                loadCars();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Car deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete car. It may be used in contracts, statuses, or maintenance records.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        Car car = new Car();

        car.setPlateNumber(txtPlateNumber.getText().trim());
        car.setBrand(txtBrand.getText().trim());
        car.setModel(txtModel.getText().trim());
        car.setColor(txtColor.getText().trim());
        car.setDailyPrice(Double.parseDouble(txtDailyPrice.getText().trim()));
        car.setManufactureYear(parseOptionalInt(txtManufactureYear.getText().trim()));
        car.setFuelType(cmbFuelType.getValue());
        car.setTransmissionType(cmbTransmissionType.getValue());
        car.setMileage(parseOptionalInt(txtMileage.getText().trim()));
        car.setCategoryId(Integer.parseInt(txtCategoryId.getText().trim()));
        car.setBranchId(Integer.parseInt(txtBranchId.getText().trim()));

        boolean success;

        if (editMode) {
            car.setCarId(selectedCar.getCarId());
            success = carDAO.updateCar(car);
        } else {
            success = carDAO.addCar(car);
        }

        if (success) {
            loadCars();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Car updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Car added successfully.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Operation failed. Check plate number, category ID, branch ID, or database constraints.");
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        hideForm();
    }

    private boolean validateForm() {
        if (txtPlateNumber.getText().trim().isEmpty()
                || txtBrand.getText().trim().isEmpty()
                || txtModel.getText().trim().isEmpty()
                || txtDailyPrice.getText().trim().isEmpty()
                || txtCategoryId.getText().trim().isEmpty()
                || txtBranchId.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Plate number, brand, model, daily price, category ID, and branch ID are required.");
            return false;
        }

        try {
            double price = Double.parseDouble(txtDailyPrice.getText().trim());

            if (price < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Daily price cannot be negative.");
                return false;
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Daily price must be a valid number.");
            return false;
        }

        if (!txtManufactureYear.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(txtManufactureYear.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Manufacture year must be a number.");
                return false;
            }
        }

        if (!txtMileage.getText().trim().isEmpty()) {
            try {
                int mileage = Integer.parseInt(txtMileage.getText().trim());

                if (mileage < 0) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", "Mileage cannot be negative.");
                    return false;
                }

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Mileage must be a number.");
                return false;
            }
        }

        try {
            Integer.parseInt(txtCategoryId.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Category ID must be a number.");
            return false;
        }

        try {
            Integer.parseInt(txtBranchId.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Branch ID must be a number.");
            return false;
        }

        return true;
    }

    private int parseOptionalInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Integer.parseInt(value.trim());
    }

    private void clearForm() {
        txtPlateNumber.clear();
        txtBrand.clear();
        txtModel.clear();
        txtColor.clear();
        txtDailyPrice.clear();
        txtManufactureYear.clear();
        cmbFuelType.setValue(null);
        cmbTransmissionType.setValue(null);
        txtMileage.clear();
        txtCategoryId.clear();
        txtBranchId.clear();
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