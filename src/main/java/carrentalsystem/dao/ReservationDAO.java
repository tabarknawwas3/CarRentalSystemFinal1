package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Reservation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();

        String sql =
                "SELECT reservation_id, reservation_date, start_date, end_date, "
                + "reservation_status, customer_id, car_id, category_id, branch_id "
                + "FROM Reservation "
                + "ORDER BY reservation_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    public List<Reservation> getReservationsByCustomerId(int customerId) {
        List<Reservation> reservations = new ArrayList<>();

        String sql =
                "SELECT reservation_id, reservation_date, start_date, end_date, "
                + "reservation_status, customer_id, car_id, category_id, branch_id "
                + "FROM Reservation "
                + "WHERE customer_id = ? "
                + "ORDER BY reservation_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading customer reservations: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    public List<Reservation> searchReservations(String keyword) {
        List<Reservation> reservations = new ArrayList<>();

        String sql =
                "SELECT reservation_id, reservation_date, start_date, end_date, "
                + "reservation_status, customer_id, car_id, category_id, branch_id "
                + "FROM Reservation "
                + "WHERE reservation_status LIKE ? "
                + "OR CAST(reservation_id AS CHAR) LIKE ? "
                + "OR CAST(customer_id AS CHAR) LIKE ? "
                + "OR CAST(car_id AS CHAR) LIKE ? "
                + "OR CAST(category_id AS CHAR) LIKE ? "
                + "OR CAST(branch_id AS CHAR) LIKE ? "
                + "ORDER BY reservation_id";

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
                    reservations.add(mapResultSetToReservation(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching reservations: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    public boolean addReservation(Reservation reservation) {
        String sql =
                "INSERT INTO Reservation "
                + "(reservation_date, start_date, end_date, reservation_status, customer_id, car_id, category_id, branch_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillReservationStatement(stmt, reservation, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int addReservationAndReturnId(Reservation reservation) {
        String sql =
                "INSERT INTO Reservation "
                + "(start_date, end_date, reservation_status, customer_id, car_id, category_id, branch_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(reservation.getStartDate()));
            stmt.setDate(2, Date.valueOf(reservation.getEndDate()));
            stmt.setString(3, reservation.getReservationStatus());
            stmt.setInt(4, reservation.getCustomerId());

            if (reservation.getCarId() > 0) {
                stmt.setInt(5, reservation.getCarId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.setInt(6, reservation.getCategoryId());
            stmt.setInt(7, reservation.getBranchId());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding reservation and returning ID: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public boolean updateReservation(Reservation reservation) {
        String sql =
                "UPDATE Reservation SET "
                + "reservation_date = ?, "
                + "start_date = ?, "
                + "end_date = ?, "
                + "reservation_status = ?, "
                + "customer_id = ?, "
                + "car_id = ?, "
                + "category_id = ?, "
                + "branch_id = ? "
                + "WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillReservationStatement(stmt, reservation, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReservation(int reservationId) {
        String sql = "DELETE FROM Reservation WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();

        reservation.setReservationId(rs.getInt("reservation_id"));

        Date reservationDate = rs.getDate("reservation_date");
        if (reservationDate != null) {
            reservation.setReservationDate(reservationDate.toLocalDate());
        }

        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            reservation.setStartDate(startDate.toLocalDate());
        }

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            reservation.setEndDate(endDate.toLocalDate());
        }

        reservation.setReservationStatus(rs.getString("reservation_status"));
        reservation.setCustomerId(rs.getInt("customer_id"));

        int carId = rs.getInt("car_id");
        if (rs.wasNull()) {
            carId = 0;
        }
        reservation.setCarId(carId);

        reservation.setCategoryId(rs.getInt("category_id"));
        reservation.setBranchId(rs.getInt("branch_id"));

        return reservation;
    }

    private void fillReservationStatement(PreparedStatement stmt, Reservation reservation, boolean includeId)
            throws SQLException {

        if (reservation.getReservationDate() != null) {
            stmt.setDate(1, Date.valueOf(reservation.getReservationDate()));
        } else {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
        }

        if (reservation.getStartDate() != null) {
            stmt.setDate(2, Date.valueOf(reservation.getStartDate()));
        } else {
            stmt.setDate(2, null);
        }

        if (reservation.getEndDate() != null) {
            stmt.setDate(3, Date.valueOf(reservation.getEndDate()));
        } else {
            stmt.setDate(3, null);
        }

        stmt.setString(4, reservation.getReservationStatus());
        stmt.setInt(5, reservation.getCustomerId());

        if (reservation.getCarId() > 0) {
            stmt.setInt(6, reservation.getCarId());
        } else {
            stmt.setNull(6, java.sql.Types.INTEGER);
        }

        stmt.setInt(7, reservation.getCategoryId());
        stmt.setInt(8, reservation.getBranchId());

        if (includeId) {
            stmt.setInt(9, reservation.getReservationId());
        }
    }
}