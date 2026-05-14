package carrentalsystem.models;

public class Car {

    private int carId;
    private String plateNumber;
    private String brand;
    private String model;
    private String color;
    private double dailyPrice;
    private int manufactureYear;
    private String fuelType;
    private String transmissionType;
    private int mileage;
    private int categoryId;
    private int branchId;

    public Car() {
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    /*
     * للتوافق مع أكواد قديمة ممكن تستخدم id بدل carId.
     */
    public int getId() {
        return carId;
    }

    public void setId(int id) {
        this.carId = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }
    
    public int getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }
    
    /*
     * للتوافق لو عندك كود قديم كان يسميها year.
     */
    public int getYear() {
        return manufactureYear;
    }

    public void setYear(int year) {
        this.manufactureYear = year;
    }
    
    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    
    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }
    
    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
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
}