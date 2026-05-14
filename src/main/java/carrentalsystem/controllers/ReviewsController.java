package carrentalsystem.controllers;

import carrentalsystem.dao.ReviewDAO;
import carrentalsystem.models.Review;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReviewsController {

    @FXML private TextField txtSearch;

    @FXML private TableView<Review> tableReviews;
    @FXML private TableColumn<Review, Integer> colReviewId;
    @FXML private TableColumn<Review, Integer> colRating;
    @FXML private TableColumn<Review, String> colComment;
    @FXML private TableColumn<Review, LocalDate> colReviewDate;
    @FXML private TableColumn<Review, Integer> colCustomerId;
    @FXML private TableColumn<Review, String> colCustomerName;
    @FXML private TableColumn<Review, Integer> colContractId;

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final ObservableList<Review> reviewList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadReviews();
    }

    private void setupTable() {
        colReviewId.setCellValueFactory(new PropertyValueFactory<>("reviewId"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        colReviewDate.setCellValueFactory(new PropertyValueFactory<>("reviewDate"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colContractId.setCellValueFactory(new PropertyValueFactory<>("contractId"));

        tableReviews.setItems(reviewList);
    }

    private void loadReviews() {
        reviewList.clear();

        List<Review> reviews = reviewDAO.getAllReviews();
        reviewList.addAll(reviews);
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        reviewList.clear();

        if (keyword.isEmpty()) {
            reviewList.addAll(reviewDAO.getAllReviews());
        } else {
            reviewList.addAll(reviewDAO.searchReviews(keyword));
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadReviews();
    }
}