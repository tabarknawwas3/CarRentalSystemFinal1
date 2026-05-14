package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Branch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();

        String sql =
                "SELECT branch_id, branch_name, city, address, phone_number, "
                + "manager_name, open_hour, close_hour "
                + "FROM Branch "
                + "ORDER BY branch_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                branches.add(mapResultSetToBranch(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading branches: " + e.getMessage());
            e.printStackTrace();
        }

        return branches;
    }

    public List<Branch> searchBranches(String keyword) {
        List<Branch> branches = new ArrayList<>();

        String sql =
                "SELECT branch_id, branch_name, city, address, phone_number, "
                + "manager_name, open_hour, close_hour "
                + "FROM Branch "
                + "WHERE branch_name LIKE ? "
                + "OR city LIKE ? "
                + "OR address LIKE ? "
                + "OR phone_number LIKE ? "
                + "OR manager_name LIKE ? "
                + "OR CAST(branch_id AS CHAR) LIKE ? "
                + "ORDER BY branch_id";

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
                    branches.add(mapResultSetToBranch(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching branches: " + e.getMessage());
            e.printStackTrace();
        }

        return branches;
    }

    public boolean addBranch(Branch branch) {
        String sql =
                "INSERT INTO Branch "
                + "(branch_name, city, address, phone_number, manager_name, open_hour, close_hour) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillBranchStatement(stmt, branch, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding branch: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBranch(Branch branch) {
        String sql =
                "UPDATE Branch SET "
                + "branch_name = ?, "
                + "city = ?, "
                + "address = ?, "
                + "phone_number = ?, "
                + "manager_name = ?, "
                + "open_hour = ?, "
                + "close_hour = ? "
                + "WHERE branch_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillBranchStatement(stmt, branch, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating branch: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBranch(int branchId) {
        String sql = "DELETE FROM Branch WHERE branch_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting branch: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Branch mapResultSetToBranch(ResultSet rs) throws SQLException {
        Branch branch = new Branch();

        branch.setBranchId(rs.getInt("branch_id"));
        branch.setBranchName(rs.getString("branch_name"));
        branch.setCity(rs.getString("city"));
        branch.setAddress(rs.getString("address"));
        branch.setPhoneNumber(rs.getString("phone_number"));
        branch.setManagerName(rs.getString("manager_name"));

        Object open = rs.getObject("open_hour");
        Object close = rs.getObject("close_hour");

        branch.setOpenHour(open == null ? "" : open.toString());
        branch.setCloseHour(close == null ? "" : close.toString());

        return branch;
    }

    private void fillBranchStatement(PreparedStatement stmt, Branch branch, boolean includeId)
            throws SQLException {

        stmt.setString(1, branch.getBranchName());
        stmt.setString(2, branch.getCity());
        stmt.setString(3, branch.getAddress());
        stmt.setString(4, branch.getPhoneNumber());
        stmt.setString(5, branch.getManagerName());
        stmt.setString(6, branch.getOpenHour());
        stmt.setString(7, branch.getCloseHour());

        if (includeId) {
            stmt.setInt(8, branch.getBranchId());
        }
    }
}