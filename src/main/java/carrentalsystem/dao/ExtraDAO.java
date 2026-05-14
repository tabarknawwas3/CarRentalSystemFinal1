package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Extra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExtraDAO {

    public List<Extra> getAllExtras() {
        List<Extra> extras = new ArrayList<>();

        String sql =
                "SELECT extra_id, extra_name, description, price_per_day, is_available "
                + "FROM Extras "
                + "ORDER BY extra_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                extras.add(mapResultSetToExtra(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading extras: " + e.getMessage());
            e.printStackTrace();
        }

        return extras;
    }

    public List<Extra> searchExtras(String keyword) {
        List<Extra> extras = new ArrayList<>();

        String sql =
                "SELECT extra_id, extra_name, description, price_per_day, is_available "
                + "FROM Extras "
                + "WHERE extra_name LIKE ? "
                + "OR description LIKE ? "
                + "OR CAST(extra_id AS CHAR) LIKE ? "
                + "ORDER BY extra_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            stmt.setString(1, searchValue);
            stmt.setString(2, searchValue);
            stmt.setString(3, searchValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    extras.add(mapResultSetToExtra(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching extras: " + e.getMessage());
            e.printStackTrace();
        }

        return extras;
    }

    public boolean addExtra(Extra extra) {
        String sql =
                "INSERT INTO Extras "
                + "(extra_name, description, price_per_day, is_available) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillExtraStatement(stmt, extra, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding extra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateExtra(Extra extra) {
        String sql =
                "UPDATE Extras SET "
                + "extra_name = ?, "
                + "description = ?, "
                + "price_per_day = ?, "
                + "is_available = ? "
                + "WHERE extra_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillExtraStatement(stmt, extra, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating extra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExtra(int extraId) {
        String sql = "DELETE FROM Extras WHERE extra_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, extraId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting extra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Extra mapResultSetToExtra(ResultSet rs) throws SQLException {
        Extra extra = new Extra();

        extra.setExtraId(rs.getInt("extra_id"));
        extra.setExtraName(rs.getString("extra_name"));
        extra.setDescription(rs.getString("description"));
        extra.setPricePerDay(rs.getDouble("price_per_day"));
        extra.setAvailable(rs.getBoolean("is_available"));

        return extra;
    }

    private void fillExtraStatement(PreparedStatement stmt, Extra extra, boolean includeId)
            throws SQLException {

        stmt.setString(1, extra.getExtraName());
        stmt.setString(2, extra.getDescription());
        stmt.setDouble(3, extra.getPricePerDay());
        stmt.setBoolean(4, extra.isAvailable());

        if (includeId) {
            stmt.setInt(5, extra.getExtraId());
        }
    }
}