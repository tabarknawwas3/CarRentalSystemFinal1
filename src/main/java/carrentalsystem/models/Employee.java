package carrentalsystem.models;

import java.time.LocalDate;

public class Employee {

    private int employeeId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String phoneNumber;
    private String email;
    private double salary;
    private LocalDate hireDate;
    private String username;
    private String password;
    private int branchId;
    private int adminId;

    public Employee() {
    }

    public Employee(int employeeId, String firstName, String lastName, String jobTitle,
                    String phoneNumber, String email, double salary, LocalDate hireDate,
                    String username, String password, int branchId, int adminId) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.salary = salary;
        this.hireDate = hireDate;
        this.username = username;
        this.password = password;
        this.branchId = branchId;
        this.adminId = adminId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        String f = firstName == null ? "" : firstName;
        String l = lastName == null ? "" : lastName;
        return (f + " " + l).trim();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /*
     * هدول للتوافق مع الواجهة القديمة إذا كان فيها Role.
     * role = jobTitle
     */
    public String getRole() {
        return jobTitle;
    }

    public void setRole(String role) {
        this.jobTitle = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /*
     * هدول للتوافق مع أكواد قديمة كانت تستخدم phone.
     */
    public String getPhone() {
        return phoneNumber;
    }

    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
    
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}