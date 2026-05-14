package carrentalsystem.models;

import java.time.LocalDate;

public class Customer {

    private int customerId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String email;
    private String address;
    private String nationalIdOrPassport;
    private LocalDate registrationDate;

    private String username;
    private String password;

    public Customer() {
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getId() {
        return customerId;
    }

    public void setId(int id) {
        this.customerId = id;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNationalIdOrPassport() {
        return nationalIdOrPassport;
    }

    public void setNationalIdOrPassport(String nationalIdOrPassport) {
        this.nationalIdOrPassport = nationalIdOrPassport;
    }

    public String getNationalId() {
        return nationalIdOrPassport;
    }

    public void setNationalId(String nationalId) {
        this.nationalIdOrPassport = nationalId;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
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
}