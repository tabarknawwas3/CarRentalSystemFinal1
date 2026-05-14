package carrentalsystem.models;

import java.time.LocalDate;

public class Review {

    private int reviewId;
    private int rating;
    private String comment;
    private LocalDate reviewDate;
    private int customerId;
    private int contractId;

    private String customerName;

    public Review() {
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getId() {
        return reviewId;
    }

    public void setId(int id) {
        this.reviewId = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }   

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}