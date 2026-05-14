package carrentalsystem.models;

import java.time.LocalDate;

public class Reservation {

    private int reservationId;
    private LocalDate reservationDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reservationStatus;
    private int customerId;
    private int categoryId;
    private int branchId;
    private int carId;

    public Reservation() {
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getId() {
        return reservationId;
    }

    public void setId(int id) {
        this.reservationId = id;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getStatus() {
        return reservationStatus;
    }

    public void setStatus(String status) {
        this.reservationStatus = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
    public int getCarId() {
    return carId;
}

public void setCarId(int carId) {
    this.carId = carId;
}
}