package carrentalsystem.models;

import java.time.LocalDate;

public class DrivingLicense {

    private int licenseId;
    private String licenseNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String countryOfIssue;
    private int customerId;

    private String customerName;

    public DrivingLicense() {
    }

    public int getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(int licenseId) {
        this.licenseId = licenseId;
    }

    public int getId() {
        return licenseId;
    }

    public void setId(int id) {
        this.licenseId = id;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCountryOfIssue() {
        return countryOfIssue;
    }

    public void setCountryOfIssue(String countryOfIssue) {
        this.countryOfIssue = countryOfIssue;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}