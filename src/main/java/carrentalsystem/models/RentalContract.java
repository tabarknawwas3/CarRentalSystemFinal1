package carrentalsystem.models;

import java.time.LocalDate;

public class RentalContract {

    private int contractId;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private int mileageAtPickup;
    private int mileageAtReturn;
    private String contractStatus;

    private int customerId;
    private int carId;
    private int employeeId;
    private int reservationId;

    private String customerName;
    private String carInfo;
    private String employeeName;

    public RentalContract() {
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getId() {
        return contractId;
    }

    public void setId(int id) {
        this.contractId = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public int getMileageAtPickup() {
        return mileageAtPickup;
    }

    public void setMileageAtPickup(int mileageAtPickup) {
        this.mileageAtPickup = mileageAtPickup;
    }

    public int getMileageAtReturn() {
        return mileageAtReturn;
    }

    public void setMileageAtReturn(int mileageAtReturn) {
        this.mileageAtReturn = mileageAtReturn;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getStatus() {
        return contractStatus;
    }

    public void setStatus(String status) {
        this.contractStatus = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(String carInfo) {
        this.carInfo = carInfo;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}