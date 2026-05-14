package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        String sql =
                "SELECT customer_id, first_name, last_name, date_of_birth, gender, "
                + "phone_number, email, address, national_id_or_passport, username, password, registration_date "
                + "FROM Customer "
                + "ORDER BY customer_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading customers: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    public List<Customer> searchCustomers(String keyword) {
        List<Customer> customers = new ArrayList<>();

        String sql =
                "SELECT customer_id, first_name, last_name, date_of_birth, gender, "
                + "phone_number, email, address, national_id_or_passport, username, password, registration_date "
                + "FROM Customer "
                + "WHERE first_name LIKE ? "
                + "OR last_name LIKE ? "
                + "OR phone_number LIKE ? "
                + "OR email LIKE ? "
                + "OR address LIKE ? "
                + "OR national_id_or_passport LIKE ? "
                + "OR username LIKE ? "
                + "OR CAST(customer_id AS CHAR) LIKE ? "
                + "ORDER BY customer_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            for (int i = 1; i <= 8; i++) {
                stmt.setString(i, searchValue);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    public Customer getCustomerByLogin(String username, String password) {
        String sql =
                "SELECT customer_id, first_name, last_name, date_of_birth, gender, "
                + "phone_number, email, address, national_id_or_passport, username, password, registration_date "
                + "FROM Customer "
                + "WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error customer login: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean addCustomer(Customer customer) {
        String sql =
                "INSERT INTO Customer "
                + "(first_name, last_name, date_of_birth, gender, phone_number, email, address, "
                + "national_id_or_passport, username, password) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillCustomerStatement(stmt, customer, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerCustomer(Customer customer) {
        return addCustomer(customer);
    }

    public boolean updateCustomer(Customer customer) {
        String sql =
                "UPDATE Customer SET "
                + "first_name = ?, "
                + "last_name = ?, "
                + "date_of_birth = ?, "
                + "gender = ?, "
                + "phone_number = ?, "
                + "email = ?, "
                + "address = ?, "
                + "national_id_or_passport = ?, "
                + "username = ?, "
                + "password = ? "
                + "WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillCustomerStatement(stmt, customer, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomerProfile(Customer customer) {
        String sql =
                "UPDATE Customer SET "
                + "first_name = ?, "
                + "last_name = ?, "
                + "phone_number = ?, "
                + "email = ?, "
                + "address = ? "
                + "WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getAddress());
            stmt.setInt(6, customer.getCustomerId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating customer profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM Customer WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();

        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            customer.setDateOfBirth(dob.toLocalDate());
        }

        customer.setGender(rs.getString("gender"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setEmail(rs.getString("email"));
        customer.setAddress(rs.getString("address"));
        customer.setNationalIdOrPassport(rs.getString("national_id_or_passport"));
        customer.setUsername(rs.getString("username"));
        customer.setPassword(rs.getString("password"));

        Date registrationDate = rs.getDate("registration_date");
        if (registrationDate != null) {
            customer.setRegistrationDate(registrationDate.toLocalDate());
        }

        return customer;
    }

    private void fillCustomerStatement(PreparedStatement stmt, Customer customer, boolean includeId)
            throws SQLException {

        stmt.setString(1, customer.getFirstName());
        stmt.setString(2, customer.getLastName());

        if (customer.getDateOfBirth() != null) {
            stmt.setDate(3, Date.valueOf(customer.getDateOfBirth()));
        } else {
            stmt.setDate(3, null);
        }

        stmt.setString(4, customer.getGender());
        stmt.setString(5, customer.getPhoneNumber());
        stmt.setString(6, customer.getEmail());
        stmt.setString(7, customer.getAddress());
        stmt.setString(8, customer.getNationalIdOrPassport());
        stmt.setString(9, customer.getUsername());
        stmt.setString(10, customer.getPassword());

        if (includeId) {
            stmt.setInt(11, customer.getCustomerId());
        }
    }
}