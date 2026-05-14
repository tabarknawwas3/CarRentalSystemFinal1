package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Admin;
import carrentalsystem.models.Customer;
import carrentalsystem.models.Employee;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {

    public Admin loginAdmin(String username, String password) {
    String sql =
            "SELECT admin_id, first_name, last_name, username, password, email, phone_number, created_at "
            + "FROM Admin "
            + "WHERE TRIM(username) = ? AND TRIM(password) = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        System.out.println("========== ADMIN LOGIN DEBUG ==========");
        System.out.println("DB URL = " + conn.getMetaData().getURL());
        System.out.println("DB USER = " + conn.getMetaData().getUserName());
        System.out.println("Username typed = [" + username + "]");
        System.out.println("Password typed = [" + password + "]");
        System.out.println("=======================================");

        stmt.setString(1, username.trim());
        stmt.setString(2, password.trim());

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Admin admin = new Admin();

                admin.setAdminId(rs.getInt("admin_id"));
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                admin.setEmail(rs.getString("email"));
                admin.setPhoneNumber(rs.getString("phone_number"));

                return admin;
            } else {
                System.out.println("No admin found with this username/password.");
            }
        }

    } catch (SQLException e) {
        System.err.println("Admin login error: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
}

    public Customer loginCustomer(String username, String password) {
        String sql =
                "SELECT customer_id, first_name, last_name, date_of_birth, gender, "
                + "phone_number, email, address, national_id_or_passport, registration_date, "
                + "username, password "
                + "FROM Customer "
                + "WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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

                    Date registrationDate = rs.getDate("registration_date");
                    if (registrationDate != null) {
                        customer.setRegistrationDate(registrationDate.toLocalDate());
                    }

                    customer.setUsername(rs.getString("username"));
                    customer.setPassword(rs.getString("password"));

                    return customer;
                }
            }

        } catch (SQLException e) {
            System.err.println("Customer login error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /*
     * خليتها موجودة حتى لو ما بدنا Employee Login حاليًا،
     * عشان ما ينكسر أي كود قديم كان يستعملها.
     */
    public Employee loginEmployee(String username, String password) {
        String sql =
                "SELECT employee_id, first_name, last_name, job_title, phone_number, email, "
                + "salary, hire_date, username, password, branch_id, admin_id "
                + "FROM Employee "
                + "WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();

                    employee.setEmployeeId(rs.getInt("employee_id"));
                    employee.setFirstName(rs.getString("first_name"));
                    employee.setLastName(rs.getString("last_name"));
                    employee.setJobTitle(rs.getString("job_title"));
                    employee.setPhoneNumber(rs.getString("phone_number"));
                    employee.setEmail(rs.getString("email"));
                    employee.setSalary(rs.getDouble("salary"));

                    Date hireDate = rs.getDate("hire_date");
                    if (hireDate != null) {
                        employee.setHireDate(hireDate.toLocalDate());
                    }

                    employee.setUsername(rs.getString("username"));
                    employee.setPassword(rs.getString("password"));
                    employee.setBranchId(rs.getInt("branch_id"));
                    employee.setAdminId(rs.getInt("admin_id"));

                    return employee;
                }
            }

        } catch (SQLException e) {
            System.err.println("Employee login error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}