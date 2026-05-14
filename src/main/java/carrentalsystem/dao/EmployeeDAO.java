package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Employee;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        String sql =
                "SELECT "
                + "employee_id, first_name, last_name, job_title, phone_number, "
                + "email, salary, hire_date, username, password, branch_id, admin_id "
                + "FROM Employee "
                + "ORDER BY employee_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading employees: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    public List<Employee> searchEmployees(String keyword) {
        List<Employee> employees = new ArrayList<>();

        String sql =
                "SELECT "
                + "employee_id, first_name, last_name, job_title, phone_number, "
                + "email, salary, hire_date, username, password, branch_id, admin_id "
                + "FROM Employee "
                + "WHERE first_name LIKE ? "
                + "OR last_name LIKE ? "
                + "OR job_title LIKE ? "
                + "OR phone_number LIKE ? "
                + "OR email LIKE ? "
                + "OR username LIKE ? "
                + "ORDER BY employee_id";

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
                    employees.add(mapResultSetToEmployee(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    public boolean addEmployee(Employee emp) {
        String sql =
                "INSERT INTO Employee "
                + "(first_name, last_name, job_title, phone_number, email, salary, "
                + "hire_date, username, password, branch_id, admin_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillEmployeeStatement(stmt, emp, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmployee(Employee emp) {
        String sql =
                "UPDATE Employee SET "
                + "first_name = ?, "
                + "last_name = ?, "
                + "job_title = ?, "
                + "phone_number = ?, "
                + "email = ?, "
                + "salary = ?, "
                + "hire_date = ?, "
                + "username = ?, "
                + "password = ?, "
                + "branch_id = ?, "
                + "admin_id = ? "
                + "WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillEmployeeStatement(stmt, emp, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM Employee WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();

        emp.setEmployeeId(rs.getInt("employee_id"));
        emp.setFirstName(rs.getString("first_name"));
        emp.setLastName(rs.getString("last_name"));
        emp.setJobTitle(rs.getString("job_title"));
        emp.setPhoneNumber(rs.getString("phone_number"));
        emp.setEmail(rs.getString("email"));
        emp.setSalary(rs.getDouble("salary"));

        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) {
            emp.setHireDate(hireDate.toLocalDate());
        }

        emp.setUsername(rs.getString("username"));
        emp.setPassword(rs.getString("password"));
        emp.setBranchId(rs.getInt("branch_id"));

        int adminId = rs.getInt("admin_id");
        if (!rs.wasNull()) {
            emp.setAdminId(adminId);
        }

        return emp;
    }

    private void fillEmployeeStatement(PreparedStatement stmt, Employee emp, boolean includeId)
            throws SQLException {

        stmt.setString(1, emp.getFirstName());
        stmt.setString(2, emp.getLastName());
        stmt.setString(3, emp.getJobTitle());
        stmt.setString(4, emp.getPhoneNumber());
        stmt.setString(5, emp.getEmail());
        stmt.setDouble(6, emp.getSalary());

        if (emp.getHireDate() != null) {
            stmt.setDate(7, Date.valueOf(emp.getHireDate()));
        } else {
            stmt.setDate(7, null);
        }

        stmt.setString(8, emp.getUsername());
        stmt.setString(9, emp.getPassword());
        stmt.setInt(10, emp.getBranchId());

        if (emp.getAdminId() > 0) {
            stmt.setInt(11, emp.getAdminId());
        } else {
            stmt.setNull(11, java.sql.Types.INTEGER);
        }

        if (includeId) {
            stmt.setInt(12, emp.getEmployeeId());
        }
    }
}