package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Review;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();

        String sql =
                "SELECT r.review_id, r.rating, r.comment, r.review_date, "
                + "r.customer_id, r.contract_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name "
                + "FROM Review r "
                + "JOIN Customer c ON r.customer_id = c.customer_id "
                + "ORDER BY r.review_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading reviews: " + e.getMessage());
            e.printStackTrace();
        }

        return reviews;
    }

    public List<Review> getReviewsByCustomerId(int customerId) {
        List<Review> reviews = new ArrayList<>();

        String sql =
                "SELECT r.review_id, r.rating, r.comment, r.review_date, "
                + "r.customer_id, r.contract_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name "
                + "FROM Review r "
                + "JOIN Customer c ON r.customer_id = c.customer_id "
                + "WHERE r.customer_id = ? "
                + "ORDER BY r.review_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading customer reviews: " + e.getMessage());
            e.printStackTrace();
        }

        return reviews;
    }

    public boolean hasReviewForContract(int customerId, int contractId) {
        String sql =
                "SELECT COUNT(*) AS review_count "
                + "FROM Review "
                + "WHERE customer_id = ? AND contract_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.setInt(2, contractId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("review_count") > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking review existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public List<Review> searchReviews(String keyword) {
        List<Review> reviews = new ArrayList<>();

        String sql =
                "SELECT r.review_id, r.rating, r.comment, r.review_date, "
                + "r.customer_id, r.contract_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name "
                + "FROM Review r "
                + "JOIN Customer c ON r.customer_id = c.customer_id "
                + "WHERE r.comment LIKE ? "
                + "OR CONCAT(c.first_name, ' ', c.last_name) LIKE ? "
                + "OR CAST(r.review_id AS CHAR) LIKE ? "
                + "OR CAST(r.rating AS CHAR) LIKE ? "
                + "OR CAST(r.customer_id AS CHAR) LIKE ? "
                + "OR CAST(r.contract_id AS CHAR) LIKE ? "
                + "ORDER BY r.review_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            stmt.setString(1, searchValue);
            stmt.setString(2, searchValue);
            stmt.setString(3, searchValue);
            stmt.setString(4, searchValue);
            stmt.setString(5, searchValue);
            stmt.setString(6, searchValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching reviews: " + e.getMessage());
            e.printStackTrace();
        }

        return reviews;
    }

    public boolean addReview(Review review) {
        String sql =
                "INSERT INTO Review "
                + "(rating, comment, review_date, customer_id, contract_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillReviewStatement(stmt, review, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding review: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReview(Review review) {
        String sql =
                "UPDATE Review SET "
                + "rating = ?, "
                + "comment = ?, "
                + "review_date = ?, "
                + "customer_id = ?, "
                + "contract_id = ? "
                + "WHERE review_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillReviewStatement(stmt, review, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating review: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM Review WHERE review_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reviewId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting review: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();

        review.setReviewId(rs.getInt("review_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));

        Date reviewDate = rs.getDate("review_date");
        if (reviewDate != null) {
            review.setReviewDate(reviewDate.toLocalDate());
        }

        review.setCustomerId(rs.getInt("customer_id"));
        review.setContractId(rs.getInt("contract_id"));
        review.setCustomerName(rs.getString("customer_name"));

        return review;
    }

    private void fillReviewStatement(PreparedStatement stmt, Review review, boolean includeId)
            throws SQLException {

        stmt.setInt(1, review.getRating());
        stmt.setString(2, review.getComment());

        if (review.getReviewDate() != null) {
            stmt.setDate(3, Date.valueOf(review.getReviewDate()));
        } else {
            stmt.setNull(3, Types.DATE);
        }

        stmt.setInt(4, review.getCustomerId());
        stmt.setInt(5, review.getContractId());

        if (includeId) {
            stmt.setInt(6, review.getReviewId());
        }
    }
}