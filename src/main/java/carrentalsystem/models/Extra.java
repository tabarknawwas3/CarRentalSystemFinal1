package carrentalsystem.models;

public class Extra {

    private int extraId;
    private String extraName;
    private String description;
    private double pricePerDay;
    private boolean available;

    public Extra() {
    }

    public int getExtraId() {
        return extraId;
    }

    public void setExtraId(int extraId) {
        this.extraId = extraId;
    }

    public int getId() {
        return extraId;
    }

    public void setId(int id) {
        this.extraId = id;
    }

    public String getExtraName() {
        return extraName;
    }

    public void setExtraName(String extraName) {
        this.extraName = extraName;
    }

    public String getName() {
        return extraName;
    }

    public void setName(String name) {
        this.extraName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public double getPrice() {
        return pricePerDay;
    }

    public void setPrice(double price) {
        this.pricePerDay = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}