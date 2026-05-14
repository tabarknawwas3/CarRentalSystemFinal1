package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.CarCategory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarCategoryDAO {

    public List<CarCategory> getAllCategories() {
        List<CarCategory> categories = new ArrayList<>();

        String sql =
                "SELECT category_id, category_name "
                + "FROM Car_Category "
                + "ORDER BY category_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading car categories: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }

    public List<CarCategory> searchCategories(String keyword) {
        List<CarCategory> categories = new ArrayList<>();

        String sql =
                "SELECT category_id, category_name "
                + "FROM Car_Category "
                + "WHERE category_name LIKE ? "
                + "OR CAST(category_id AS CHAR) LIKE ? "
                + "ORDER BY category_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            stmt.setString(1, searchValue);
            stmt.setString(2, searchValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching car categories: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }

    public boolean addCategory(CarCategory category) {
        String sql =
                "INSERT INTO Car_Category "
                + "(category_name) "
                + "VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getCategoryName());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding car category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCategory(CarCategory category) {
        String sql =
                "UPDATE Car_Category SET "
                + "category_name = ? "
                + "WHERE category_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getCategoryName());
            stmt.setInt(2, category.getCategoryId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating car category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM Car_Category WHERE category_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting car category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private CarCategory mapResultSetToCategory(ResultSet rs) throws SQLException {
        CarCategory category = new CarCategory();

        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));

        return category;
    }
}