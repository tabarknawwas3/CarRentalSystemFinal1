package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.CarStatus;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CarStatusDAO {

    public List<CarStatus> getAllCarStatuses() {
        List<CarStatus> statuses = new ArrayList<>();

        String sql =
                "SELECT cs.status_id, cs.car_id, cs.status, cs.start_date, cs.end_date, cs.employee_id, "
                + "CONCAT(c.brand, ' ', c.model, ' - ', c.plate_number) AS car_info, "
                + "CONCAT(e.first_name, ' ', e.last_name) AS employee_name "
                + "FROM Car_Status cs "
                + "JOIN Car c ON cs.car_id = c.car_id "
                + "LEFT JOIN Employee e ON cs.employee_id = e.employee_id "
                + "ORDER BY cs.status_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                statuses.add(mapResultSetToCarStatus(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading car statuses: " + e.getMessage());
            e.printStackTrace();
        }

        return statuses;
    }

    public List<CarStatus> searchCarStatuses(String keyword) {
        List<CarStatus> statuses = new ArrayList<>();

        String sql =
                "SELECT cs.status_id, cs.car_id, cs.status, cs.start_date, cs.end_date, cs.employee_id, "
                + "CONCAT(c.brand, ' ', c.model, ' - ', c.plate_number) AS car_info, "
                + "CONCAT(e.first_name, ' ', e.last_name) AS employee_name "
                + "FROM Car_Status cs "
                + "JOIN Car c ON cs.car_id = c.car_id "
                + "LEFT JOIN Employee e ON cs.employee_id = e.employee_id "
                + "WHERE cs.status LIKE ? "
                + "OR c.brand LIKE ? "
                + "OR c.model LIKE ? "
                + "OR c.plate_number LIKE ? "
                + "OR CONCAT(e.first_name, ' ', e.last_name) LIKE ? "
                + "OR CAST(cs.status_id AS CHAR) LIKE ? "
                + "OR CAST(cs.car_id AS CHAR) LIKE ? "
                + "ORDER BY cs.status_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            stmt.setString(1, searchValue);
            stmt.setString(2, searchValue);
            stmt.setString(3, searchValue);
            stmt.setString(4, searchValue);
            stmt.setString(5, searchValue);
            stmt.setString(6, searchValue);
            stmt.setString(7, searchValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statuses.add(mapResultSetToCarStatus(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching car statuses: " + e.getMessage());
            e.printStackTrace();
        }

        return statuses;
    }

    public boolean addCarStatus(CarStatus carStatus) {
        String sql =
                "INSERT INTO Car_Status "
                + "(car_id, status, start_date, end_date, employee_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillCarStatusStatement(stmt, carStatus, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding car status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarStatus(CarStatus carStatus) {
        String sql =
                "UPDATE Car_Status SET "
                + "car_id = ?, "
                + "status = ?, "
                + "start_date = ?, "
                + "end_date = ?, "
                + "employee_id = ? "
                + "WHERE status_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillCarStatusStatement(stmt, carStatus, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating car status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCarStatus(int statusId) {
        String sql = "DELETE FROM Car_Status WHERE status_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, statusId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting car status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private CarStatus mapResultSetToCarStatus(ResultSet rs) throws SQLException {
        CarStatus carStatus = new CarStatus();

        carStatus.setStatusId(rs.getInt("status_id"));
        carStatus.setCarId(rs.getInt("car_id"));
        carStatus.setStatus(rs.getString("status"));

        Date start = rs.getDate("start_date");
        if (start != null) {
            carStatus.setStartDate(start.toLocalDate());
        }

        Date end = rs.getDate("end_date");
        if (end != null) {
            carStatus.setEndDate(end.toLocalDate());
        }

        int employeeId = rs.getInt("employee_id");
        if (!rs.wasNull()) {
            carStatus.setEmployeeId(employeeId);
        }

        carStatus.setCarInfo(rs.getString("car_info"));
        carStatus.setEmployeeName(rs.getString("employee_name"));

        return carStatus;
    }

    private void fillCarStatusStatement(PreparedStatement stmt, CarStatus carStatus, boolean includeId)
            throws SQLException {

        stmt.setInt(1, carStatus.getCarId());
        stmt.setString(2, carStatus.getStatus());

        if (carStatus.getStartDate() != null) {
            stmt.setDate(3, Date.valueOf(carStatus.getStartDate()));
        } else {
            stmt.setNull(3, Types.DATE);
        }

        if (carStatus.getEndDate() != null) {
            stmt.setDate(4, Date.valueOf(carStatus.getEndDate()));
        } else {
            stmt.setNull(4, Types.DATE);
        }

        if (carStatus.getEmployeeId() > 0) {
            stmt.setInt(5, carStatus.getEmployeeId());
        } else {
            stmt.setNull(5, Types.INTEGER);
        }

        if (includeId) {
            stmt.setInt(6, carStatus.getStatusId());
        }
    }
}