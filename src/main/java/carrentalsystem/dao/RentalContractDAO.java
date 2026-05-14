package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.RentalContract;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class RentalContractDAO {

    public List<RentalContract> getAllContracts() {
        List<RentalContract> contracts = new ArrayList<>();

        String sql =
                "SELECT rc.contract_id, rc.start_date, rc.expected_return_date, rc.actual_return_date, "
                + "rc.mileage_at_pickup, rc.mileage_at_return, rc.contract_status, "
                + "rc.customer_id, rc.car_id, rc.employee_id, rc.reservation_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name, "
                + "CONCAT(car.brand, ' ', car.model, ' - ', car.plate_number) AS car_info, "
                + "CONCAT(e.first_name, ' ', e.last_name) AS employee_name "
                + "FROM Rental_Contract rc "
                + "JOIN Customer c ON rc.customer_id = c.customer_id "
                + "JOIN Car car ON rc.car_id = car.car_id "
                + "JOIN Employee e ON rc.employee_id = e.employee_id "
                + "ORDER BY rc.contract_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading contracts: " + e.getMessage());
            e.printStackTrace();
        }

        return contracts;
    }

    public List<RentalContract> getContractsByCustomerId(int customerId) {
        List<RentalContract> contracts = new ArrayList<>();

        String sql =
                "SELECT rc.contract_id, rc.start_date, rc.expected_return_date, rc.actual_return_date, "
                + "rc.mileage_at_pickup, rc.mileage_at_return, rc.contract_status, "
                + "rc.customer_id, rc.car_id, rc.employee_id, rc.reservation_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name, "
                + "CONCAT(car.brand, ' ', car.model, ' - ', car.plate_number) AS car_info, "
                + "CONCAT(e.first_name, ' ', e.last_name) AS employee_name "
                + "FROM Rental_Contract rc "
                + "JOIN Customer c ON rc.customer_id = c.customer_id "
                + "JOIN Car car ON rc.car_id = car.car_id "
                + "JOIN Employee e ON rc.employee_id = e.employee_id "
                + "WHERE rc.customer_id = ? "
                + "ORDER BY rc.contract_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapResultSetToContract(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading customer contracts: " + e.getMessage());
            e.printStackTrace();
        }

        return contracts;
    }

    public List<RentalContract> searchContracts(String keyword) {
        List<RentalContract> contracts = new ArrayList<>();

        String sql =
                "SELECT rc.contract_id, rc.start_date, rc.expected_return_date, rc.actual_return_date, "
                + "rc.mileage_at_pickup, rc.mileage_at_return, rc.contract_status, "
                + "rc.customer_id, rc.car_id, rc.employee_id, rc.reservation_id, "
                + "CONCAT(c.first_name, ' ', c.last_name) AS customer_name, "
                + "CONCAT(car.brand, ' ', car.model, ' - ', car.plate_number) AS car_info, "
                + "CONCAT(e.first_name, ' ', e.last_name) AS employee_name "
                + "FROM Rental_Contract rc "
                + "JOIN Customer c ON rc.customer_id = c.customer_id "
                + "JOIN Car car ON rc.car_id = car.car_id "
                + "JOIN Employee e ON rc.employee_id = e.employee_id "
                + "WHERE rc.contract_status LIKE ? "
                + "OR c.first_name LIKE ? "
                + "OR c.last_name LIKE ? "
                + "OR car.brand LIKE ? "
                + "OR car.model LIKE ? "
                + "OR car.plate_number LIKE ? "
                + "OR e.first_name LIKE ? "
                + "OR e.last_name LIKE ? "
                + "OR CAST(rc.contract_id AS CHAR) LIKE ? "
                + "OR CAST(rc.customer_id AS CHAR) LIKE ? "
                + "OR CAST(rc.car_id AS CHAR) LIKE ? "
                + "OR CAST(rc.employee_id AS CHAR) LIKE ? "
                + "ORDER BY rc.contract_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            for (int i = 1; i <= 12; i++) {
                stmt.setString(i, searchValue);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapResultSetToContract(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching contracts: " + e.getMessage());
            e.printStackTrace();
        }

        return contracts;
    }

    public boolean addContract(RentalContract contract) {
        String sql =
                "INSERT INTO Rental_Contract "
                + "(start_date, expected_return_date, actual_return_date, mileage_at_pickup, "
                + "mileage_at_return, contract_status, customer_id, car_id, employee_id, reservation_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillContractStatement(stmt, contract, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding contract: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateContract(RentalContract contract) {
        String sql =
                "UPDATE Rental_Contract SET "
                + "start_date = ?, "
                + "expected_return_date = ?, "
                + "actual_return_date = ?, "
                + "mileage_at_pickup = ?, "
                + "mileage_at_return = ?, "
                + "contract_status = ?, "
                + "customer_id = ?, "
                + "car_id = ?, "
                + "employee_id = ?, "
                + "reservation_id = ? "
                + "WHERE contract_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillContractStatement(stmt, contract, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating contract: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteContract(int contractId) {
        String sql = "DELETE FROM Rental_Contract WHERE contract_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contractId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting contract: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private RentalContract mapResultSetToContract(ResultSet rs) throws SQLException {
        RentalContract contract = new RentalContract();

        contract.setContractId(rs.getInt("contract_id"));

        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            contract.setStartDate(startDate.toLocalDate());
        }

        Date expectedReturnDate = rs.getDate("expected_return_date");
        if (expectedReturnDate != null) {
            contract.setExpectedReturnDate(expectedReturnDate.toLocalDate());
        }

        Date actualReturnDate = rs.getDate("actual_return_date");
        if (actualReturnDate != null) {
            contract.setActualReturnDate(actualReturnDate.toLocalDate());
        }

        contract.setMileageAtPickup(rs.getInt("mileage_at_pickup"));
        contract.setMileageAtReturn(rs.getInt("mileage_at_return"));
        contract.setContractStatus(rs.getString("contract_status"));

        contract.setCustomerId(rs.getInt("customer_id"));
        contract.setCarId(rs.getInt("car_id"));
        contract.setEmployeeId(rs.getInt("employee_id"));

        int reservationId = rs.getInt("reservation_id");
        if (!rs.wasNull()) {
            contract.setReservationId(reservationId);
        }

        contract.setCustomerName(rs.getString("customer_name"));
        contract.setCarInfo(rs.getString("car_info"));
        contract.setEmployeeName(rs.getString("employee_name"));

        return contract;
    }

    private void fillContractStatement(PreparedStatement stmt, RentalContract contract, boolean includeId)
            throws SQLException {

        if (contract.getStartDate() != null) {
            stmt.setDate(1, Date.valueOf(contract.getStartDate()));
        } else {
            stmt.setNull(1, Types.DATE);
        }

        if (contract.getExpectedReturnDate() != null) {
            stmt.setDate(2, Date.valueOf(contract.getExpectedReturnDate()));
        } else {
            stmt.setNull(2, Types.DATE);
        }

        if (contract.getActualReturnDate() != null) {
            stmt.setDate(3, Date.valueOf(contract.getActualReturnDate()));
        } else {
            stmt.setNull(3, Types.DATE);
        }

        stmt.setInt(4, contract.getMileageAtPickup());

        if (contract.getMileageAtReturn() > 0) {
            stmt.setInt(5, contract.getMileageAtReturn());
        } else {
            stmt.setNull(5, Types.INTEGER);
        }

        stmt.setString(6, contract.getContractStatus());
        stmt.setInt(7, contract.getCustomerId());
        stmt.setInt(8, contract.getCarId());
        stmt.setInt(9, contract.getEmployeeId());

        if (contract.getReservationId() > 0) {
            stmt.setInt(10, contract.getReservationId());
        } else {
            stmt.setNull(10, Types.INTEGER);
        }

        if (includeId) {
            stmt.setInt(11, contract.getContractId());
        }
    }
    public int addContractAndReturnId(RentalContract contract) {
    String sql =
            "INSERT INTO Rental_Contract "
            + "(start_date, expected_return_date, actual_return_date, mileage_at_pickup, mileage_at_return, "
            + "contract_status, customer_id, car_id, employee_id, reservation_id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setDate(1, Date.valueOf(contract.getStartDate()));
        stmt.setDate(2, Date.valueOf(contract.getExpectedReturnDate()));

        if (contract.getActualReturnDate() != null) {
            stmt.setDate(3, Date.valueOf(contract.getActualReturnDate()));
        } else {
            stmt.setDate(3, null);
        }

        stmt.setInt(4, contract.getMileageAtPickup());
        stmt.setInt(5, contract.getMileageAtReturn());
        stmt.setString(6, contract.getContractStatus());
        stmt.setInt(7, contract.getCustomerId());
        stmt.setInt(8, contract.getCarId());
        stmt.setInt(9, contract.getEmployeeId());
        stmt.setInt(10, contract.getReservationId());

        stmt.executeUpdate();

        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

    } catch (SQLException e) {
        System.err.println("Error adding contract and returning ID: " + e.getMessage());
        e.printStackTrace();
    }

    return -1;
}
}