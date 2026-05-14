package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Extra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationExtraDAO {

    public List<Extra> getAvailableExtras() {
        List<Extra> extras = new ArrayList<>();

        String sql =
                "SELECT extra_id, extra_name, description, price_per_day, is_available "
                + "FROM Extras "
                + "WHERE is_available = TRUE "
                + "ORDER BY extra_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Extra extra = new Extra();

                extra.setExtraId(rs.getInt("extra_id"));
                extra.setExtraName(rs.getString("extra_name"));
                extra.setDescription(rs.getString("description"));
                extra.setPricePerDay(rs.getDouble("price_per_day"));
                extra.setAvailable(rs.getBoolean("is_available"));

                extras.add(extra);
            }

        } catch (SQLException e) {
            System.err.println("Error loading available extras: " + e.getMessage());
            e.printStackTrace();
        }

        return extras;
    }

    public boolean addReservationExtra(int reservationId, int extraId, int quantity) {
        String sql =
                "INSERT INTO Reservation_Extras (reservation_id, extra_id, quantity) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            stmt.setInt(2, extraId);
            stmt.setInt(3, quantity);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding reservation extra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addReservationExtras(int reservationId, Map<Extra, Integer> selectedExtras) {
        if (selectedExtras == null || selectedExtras.isEmpty()) {
            return true;
        }

        boolean allSaved = true;

        for (Map.Entry<Extra, Integer> entry : selectedExtras.entrySet()) {
            Extra extra = entry.getKey();
            int quantity = entry.getValue();

            if (extra != null && quantity > 0) {
                boolean saved = addReservationExtra(reservationId, extra.getExtraId(), quantity);

                if (!saved) {
                    allSaved = false;
                }
            }
        }

        return allSaved;
    }

    public Map<String, Integer> getExtrasByReservationId(int reservationId) {
        Map<String, Integer> extras = new HashMap<>();

        String sql =
                "SELECT e.extra_name, re.quantity "
                + "FROM Reservation_Extras re "
                + "JOIN Extras e ON re.extra_id = e.extra_id "
                + "WHERE re.reservation_id = ? "
                + "ORDER BY e.extra_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    extras.put(rs.getString("extra_name"), rs.getInt("quantity"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading reservation extras: " + e.getMessage());
            e.printStackTrace();
        }

        return extras;
    }
    public String getExtrasTextByReservationId(int reservationId) {
    String sql =
            "SELECT e.extra_name, re.quantity "
            + "FROM Reservation_Extras re "
            + "JOIN Extras e ON re.extra_id = e.extra_id "
            + "WHERE re.reservation_id = ?";

    StringBuilder extrasText = new StringBuilder();

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, reservationId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String extraName = rs.getString("extra_name");
                int quantity = rs.getInt("quantity");

                extrasText.append("• ")
                        .append(extraName)
                        .append(" x")
                        .append(quantity)
                        .append("\n");
            }
        }

    } catch (SQLException e) {
        System.err.println("Error loading reservation extras: " + e.getMessage());
        e.printStackTrace();
    }

    if (extrasText.length() == 0) {
        return "No extras selected.";
    }

    return extrasText.toString();
}
    
}