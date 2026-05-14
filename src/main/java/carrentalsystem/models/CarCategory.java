package carrentalsystem.models;

public class CarCategory {

    private int categoryId;
    private String categoryName;

    public CarCategory() {
    }

    public CarCategory(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getId() {
        return categoryId;
    }

    public void setId(int id) {
        this.categoryId = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return categoryName;
    }

    public void setName(String name) {
        this.categoryName = name;
    }
}