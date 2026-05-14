package carrentalsystem.controllers;

import carrentalsystem.dao.CarCategoryDAO;
import carrentalsystem.models.CarCategory;
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

public class CarCategoriesController {

    @FXML private TextField txtSearch;

    @FXML private TableView<CarCategory> tableCategories;
    @FXML private TableColumn<CarCategory, Integer> colCategoryId;
    @FXML private TableColumn<CarCategory, String> colCategoryName;

    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;

    @FXML private ComboBox<String> cmbCategoryName;

    private final CarCategoryDAO categoryDAO = new CarCategoryDAO();
    private final ObservableList<CarCategory> categoryList = FXCollections.observableArrayList();

    private CarCategory selectedCategory = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        hideForm();
        loadCategories();
    }

    private void setupComboBox() {
        cmbCategoryName.setItems(
                FXCollections.observableArrayList(
                        "Small",
                        "Medium",
                        "Large",
                        "Luxury"
                )
        );
    }

    private void setupTable() {
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        tableCategories.setItems(categoryList);

        tableCategories.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> selectedCategory = newSelection
        );
    }

    private void loadCategories() {
        categoryList.clear();

        List<CarCategory> categories = categoryDAO.getAllCategories();
        categoryList.addAll(categories);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        categoryList.clear();

        if (keyword.isEmpty()) {
            categoryList.addAll(categoryDAO.getAllCategories());
        } else {
            categoryList.addAll(categoryDAO.searchCategories(keyword));
        }
    }

    @FXML
    private void handleAdd() {
        editMode = false;
        selectedCategory = null;

        lblFormTitle.setText("Add New Car Category");
        clearForm();
        showForm();
    }

    @FXML
    private void handleEdit() {
        selectedCategory = tableCategories.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a category to edit.");
            return;
        }

        editMode = true;
        lblFormTitle.setText("Edit Car Category");

        cmbCategoryName.setValue(selectedCategory.getCategoryName());

        showForm();
    }

    @FXML
    private void handleDelete() {
        selectedCategory = tableCategories.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a category to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this category?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = categoryDAO.deleteCategory(selectedCategory.getCategoryId());

            if (deleted) {
                loadCategories();
                hideForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete category. This category may be used by cars or reservations.");
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        String selectedName = cmbCategoryName.getValue();

        if (isDuplicateCategory(selectedName)) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Category", "This category already exists.");
            return;
        }

        CarCategory category = new CarCategory();
        category.setCategoryName(selectedName);

        boolean success;

        if (editMode) {
            category.setCategoryId(selectedCategory.getCategoryId());
            success = categoryDAO.updateCategory(category);
        } else {
            success = categoryDAO.addCategory(category);
        }

        if (success) {
            loadCategories();
            clearForm();
            hideForm();

            if (editMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Category updated successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully.");
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
        if (cmbCategoryName.getValue() == null || cmbCategoryName.getValue().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a category name.");
            return false;
        }

        return true;
    }

    private boolean isDuplicateCategory(String categoryName) {
        for (CarCategory category : categoryList) {
            boolean sameName = category.getCategoryName().equalsIgnoreCase(categoryName);

            if (editMode && selectedCategory != null) {
                boolean sameId = category.getCategoryId() == selectedCategory.getCategoryId();

                if (sameName && !sameId) {
                    return true;
                }

            } else {
                if (sameName) {
                    return true;
                }
            }
        }

        return false;
    }

    private void clearForm() {
        cmbCategoryName.setValue(null);
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