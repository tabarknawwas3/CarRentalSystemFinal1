package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.MaintenanceRecord;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceDAO {

    public List<MaintenanceRecord> getAllMaintenanceRecords() {
        List<MaintenanceRecord> records = new ArrayList<>();

        String sql =
                "SELECT maintenance_id, maintenance_type, description, maintenance_cost, "
                + "maintenance_date_in, maintenance_date_out, car_id, employee_id "
                + "FROM Maintenance "
                + "ORDER BY maintenance_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                records.add(mapResultSetToMaintenance(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading maintenance records: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    public List<MaintenanceRecord> searchMaintenanceRecords(String keyword) {
        List<MaintenanceRecord> records = new ArrayList<>();

        String sql =
                "SELECT maintenance_id, maintenance_type, description, maintenance_cost, "
                + "maintenance_date_in, maintenance_date_out, car_id, employee_id "
                + "FROM Maintenance "
                + "WHERE maintenance_type LIKE ? "
                + "OR description LIKE ? "
                + "OR CAST(maintenance_id AS CHAR) LIKE ? "
                + "OR CAST(car_id AS CHAR) LIKE ? "
                + "OR CAST(employee_id AS CHAR) LIKE ? "
                + "ORDER BY maintenance_id";

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
                    records.add(mapResultSetToMaintenance(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching maintenance records: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    public boolean addMaintenanceRecord(MaintenanceRecord record) {
        String sql =
                "INSERT INTO Maintenance "
                + "(maintenance_type, description, maintenance_cost, maintenance_date_in, "
                + "maintenance_date_out, car_id, employee_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillMaintenanceStatement(stmt, record, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding maintenance record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMaintenanceRecord(MaintenanceRecord record) {
        String sql =
                "UPDATE Maintenance SET "
                + "maintenance_type = ?, "
                + "description = ?, "
                + "maintenance_cost = ?, "
                + "maintenance_date_in = ?, "
                + "maintenance_date_out = ?, "
                + "car_id = ?, "
                + "employee_id = ? "
                + "WHERE maintenance_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillMaintenanceStatement(stmt, record, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating maintenance record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMaintenanceRecord(int maintenanceId) {
        String sql = "DELETE FROM Maintenance WHERE maintenance_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maintenanceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting maintenance record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private MaintenanceRecord mapResultSetToMaintenance(ResultSet rs) throws SQLException {
        MaintenanceRecord record = new MaintenanceRecord();

        record.setMaintenanceId(rs.getInt("maintenance_id"));
        record.setMaintenanceType(rs.getString("maintenance_type"));
        record.setDescription(rs.getString("description"));
        record.setMaintenanceCost(rs.getDouble("maintenance_cost"));

        Date dateIn = rs.getDate("maintenance_date_in");
        if (dateIn != null) {
            record.setMaintenanceDateIn(dateIn.toLocalDate());
        }

        Date dateOut = rs.getDate("maintenance_date_out");
        if (dateOut != null) {
            record.setMaintenanceDateOut(dateOut.toLocalDate());
        }

        record.setCarId(rs.getInt("car_id"));

        int employeeId = rs.getInt("employee_id");
        if (!rs.wasNull()) {
            record.setEmployeeId(employeeId);
        }

        return record;
    }

    private void fillMaintenanceStatement(PreparedStatement stmt, MaintenanceRecord record, boolean includeId)
            throws SQLException {

        stmt.setString(1, record.getMaintenanceType());
        stmt.setString(2, record.getDescription());
        stmt.setDouble(3, record.getMaintenanceCost());

        if (record.getMaintenanceDateIn() != null) {
            stmt.setDate(4, Date.valueOf(record.getMaintenanceDateIn()));
        } else {
            stmt.setDate(4, null);
        }

        if (record.getMaintenanceDateOut() != null) {
            stmt.setDate(5, Date.valueOf(record.getMaintenanceDateOut()));
        } else {
            stmt.setDate(5, null);
        }

        stmt.setInt(6, record.getCarId());

        if (record.getEmployeeId() > 0) {
            stmt.setInt(7, record.getEmployeeId());
        } else {
            stmt.setNull(7, java.sql.Types.INTEGER);
        }

        if (includeId) {
            stmt.setInt(8, record.getMaintenanceId());
        }
    }
}