package carrentalsystem.models;

import java.time.LocalDate;

public class MaintenanceRecord {

    private int maintenanceId;
    private String maintenanceType;
    private String description;
    private double maintenanceCost;
    private LocalDate maintenanceDateIn;
    private LocalDate maintenanceDateOut;
    private int carId;
    private int employeeId;

    public MaintenanceRecord() {
    }

    public int getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(int maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(double maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public LocalDate getMaintenanceDateIn() {
        return maintenanceDateIn;
    }

    public void setMaintenanceDateIn(LocalDate maintenanceDateIn) {
        this.maintenanceDateIn = maintenanceDateIn;
    }

    public LocalDate getMaintenanceDateOut() {
        return maintenanceDateOut;
    }

    public void setMaintenanceDateOut(LocalDate maintenanceDateOut) {
        this.maintenanceDateOut = maintenanceDateOut;
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
}