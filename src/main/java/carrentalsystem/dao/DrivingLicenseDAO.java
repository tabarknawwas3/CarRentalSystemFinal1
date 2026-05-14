package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.DrivingLicense;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DrivingLicenseDAO {

    public List<DrivingLicense> getAllLicenses() {
        List<DrivingLicense> licenses = new ArrayList<>();

        String sql =
                "SELECT dl.license_id, dl.license_number, dl.issue_date, dl.expiry_date, "
                + "dl.country_of_issue, dl.customer_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name "
                + "FROM Driving_License dl "
                + "JOIN Customer c ON dl.customer_id = c.customer_id "
                + "ORDER BY dl.license_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                licenses.add(mapResultSetToLicense(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading driving licenses: " + e.getMessage());
            e.printStackTrace();
        }

        return licenses;
    }

    public DrivingLicense getLicenseByCustomerId(int customerId) {
        String sql =
                "SELECT dl.license_id, dl.license_number, dl.issue_date, dl.expiry_date, "
                + "dl.country_of_issue, dl.customer_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name "
                + "FROM Driving_License dl "
                + "JOIN Customer c ON dl.customer_id = c.customer_id "
                + "WHERE dl.customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLicense(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading customer driving license: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<DrivingLicense> searchLicenses(String keyword) {
        List<DrivingLicense> licenses = new ArrayList<>();

        String sql =
                "SELECT dl.license_id, dl.license_number, dl.issue_date, dl.expiry_date, "
                + "dl.country_of_issue, dl.customer_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name "
                + "FROM Driving_License dl "
                + "JOIN Customer c ON dl.customer_id = c.customer_id "
                + "WHERE dl.license_number LIKE ? "
                + "OR dl.country_of_issue LIKE ? "
                + "OR CONCAT(c.first_name, ' ', c.last_name) LIKE ? "
                + "OR CAST(dl.license_id AS CHAR) LIKE ? "
                + "OR CAST(dl.customer_id AS CHAR) LIKE ? "
                + "ORDER BY dl.license_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            stmt.setString(1, searchValue);
            stmt.setString(2, searchValue);
            stmt.setString(3, searchValue);
            stmt.setString(4, searchValue);
            stmt.setString(5, searchValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    licenses.add(mapResultSetToLicense(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching driving licenses: " + e.getMessage());
            e.printStackTrace();
        }

        return licenses;
    }

    public boolean addLicense(DrivingLicense license) {
        String sql =
                "INSERT INTO Driving_License "
                + "(license_number, issue_date, expiry_date, country_of_issue, customer_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillLicenseStatement(stmt, license, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding driving license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLicense(DrivingLicense license) {
        String sql =
                "UPDATE Driving_License SET "
                + "license_number = ?, "
                + "issue_date = ?, "
                + "expiry_date = ?, "
                + "country_of_issue = ?, "
                + "customer_id = ? "
                + "WHERE license_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillLicenseStatement(stmt, license, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating driving license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLicense(int licenseId) {
        String sql = "DELETE FROM Driving_License WHERE license_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, licenseId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting driving license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private DrivingLicense mapResultSetToLicense(ResultSet rs) throws SQLException {
        DrivingLicense license = new DrivingLicense();

        license.setLicenseId(rs.getInt("license_id"));
        license.setLicenseNumber(rs.getString("license_number"));

        Date issue = rs.getDate("issue_date");
        if (issue != null) {
            license.setIssueDate(issue.toLocalDate());
        }

        Date expiry = rs.getDate("expiry_date");
        if (expiry != null) {
            license.setExpiryDate(expiry.toLocalDate());
        }

        license.setCountryOfIssue(rs.getString("country_of_issue"));
        license.setCustomerId(rs.getInt("customer_id"));
        license.setCustomerName(rs.getString("customer_name"));

        return license;
    }

    private void fillLicenseStatement(PreparedStatement stmt, DrivingLicense license, boolean includeId)
            throws SQLException {

        stmt.setString(1, license.getLicenseNumber());

        if (license.getIssueDate() != null) {
            stmt.setDate(2, Date.valueOf(license.getIssueDate()));
        } else {
            stmt.setNull(2, Types.DATE);
        }

        if (license.getExpiryDate() != null) {
            stmt.setDate(3, Date.valueOf(license.getExpiryDate()));
        } else {
            stmt.setNull(3, Types.DATE);
        }

        stmt.setString(4, license.getCountryOfIssue());
        stmt.setInt(5, license.getCustomerId());

        if (includeId) {
            stmt.setInt(6, license.getLicenseId());
        }
    }
}