package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Car;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();

        String sql =
                "SELECT car_id, plate_number, brand, model, color, daily_price, "
                + "manufacture_year, fuel_type, transmission_type, mileage, category_id, branch_id "
                + "FROM Car "
                + "ORDER BY car_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading cars: " + e.getMessage());
            e.printStackTrace();
        }

        return cars;
    }

    public List<Car> searchCars(String keyword) {
        List<Car> cars = new ArrayList<>();

        String sql =
                "SELECT car_id, plate_number, brand, model, color, daily_price, "
                + "manufacture_year, fuel_type, transmission_type, mileage, category_id, branch_id "
                + "FROM Car "
                + "WHERE plate_number LIKE ? "
                + "OR brand LIKE ? "
                + "OR model LIKE ? "
                + "OR color LIKE ? "
                + "OR fuel_type LIKE ? "
                + "OR transmission_type LIKE ? "
                + "OR CAST(car_id AS CHAR) LIKE ? "
                + "ORDER BY car_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            stmt.setString(1, searchValue);
            stmt.setString(2, searchValue);
            stmt.setString(3, searchValue);
            stmt.setString(4, searchValue);
            stmt.setString(5, searchValue);
            stmt.setString(6, searchValue);
            stmt.setString(7, searchValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapResultSetToCar(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching cars: " + e.getMessage());
            e.printStackTrace();
        }

        return cars;
    }

    public boolean addCar(Car car) {
        String sql =
                "INSERT INTO Car "
                + "(plate_number, brand, model, color, daily_price, manufacture_year, "
                + "fuel_type, transmission_type, mileage, category_id, branch_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillCarStatement(stmt, car, false);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCar(Car car) {
        String sql =
                "UPDATE Car SET "
                + "plate_number = ?, "
                + "brand = ?, "
                + "model = ?, "
                + "color = ?, "
                + "daily_price = ?, "
                + "manufacture_year = ?, "
                + "fuel_type = ?, "
                + "transmission_type = ?, "
                + "mileage = ?, "
                + "category_id = ?, "
                + "branch_id = ? "
                + "WHERE car_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillCarStatement(stmt, car, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCar(int carId) {
        String sql = "DELETE FROM Car WHERE car_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, carId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
        Car car = new Car();

        car.setCarId(rs.getInt("car_id"));
        car.setPlateNumber(rs.getString("plate_number"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setColor(rs.getString("color"));
        car.setDailyPrice(rs.getDouble("daily_price"));
        car.setManufactureYear(rs.getInt("manufacture_year"));
        car.setFuelType(rs.getString("fuel_type"));
        car.setTransmissionType(rs.getString("transmission_type"));
        car.setMileage(rs.getInt("mileage"));
        car.setCategoryId(rs.getInt("category_id"));
        car.setBranchId(rs.getInt("branch_id"));

        return car;
    }

    private void fillCarStatement(PreparedStatement stmt, Car car, boolean includeId)
            throws SQLException {

        stmt.setString(1, car.getPlateNumber());
        stmt.setString(2, car.getBrand());
        stmt.setString(3, car.getModel());
        stmt.setString(4, car.getColor());
        stmt.setDouble(5, car.getDailyPrice());
        stmt.setInt(6, car.getManufactureYear());
        stmt.setString(7, car.getFuelType());
        stmt.setString(8, car.getTransmissionType());
        stmt.setInt(9, car.getMileage());
        stmt.setInt(10, car.getCategoryId());
        stmt.setInt(11, car.getBranchId());

        if (includeId) {
            stmt.setInt(12, car.getCarId());
        }
    }
}