package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.ReportItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public double getTotalRevenue() {
        String sql =
                "SELECT COALESCE(SUM(total_amount), 0) AS total_revenue "
                + "FROM Invoice "
                + "WHERE LOWER(invoice_status) = 'paid'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }

        } catch (SQLException e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    public int getActiveRentals() {
        String sql =
                "SELECT COUNT(*) AS active_rentals "
                + "FROM Rental_Contract "
                + "WHERE LOWER(contract_status) IN ('active', 'ongoing', 'open')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("active_rentals");
            }

        } catch (SQLException e) {
            System.err.println("Error counting active rentals: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalCars() {
        String sql = "SELECT COUNT(*) AS total_cars FROM Car";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_cars");
            }

        } catch (SQLException e) {
            System.err.println("Error counting cars: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public double getFleetUtilization() {
        int totalCars = getTotalCars();
        int activeRentals = getActiveRentals();

        if (totalCars == 0) {
            return 0.0;
        }

        return (activeRentals * 100.0) / totalCars;
    }

    public int getTotalInvoices() {
        String sql = "SELECT COUNT(*) AS total_invoices FROM Invoice";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_invoices");
            }

        } catch (SQLException e) {
            System.err.println("Error counting invoices: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int getPaidInvoices() {
        String sql =
                "SELECT COUNT(*) AS paid_invoices "
                + "FROM Invoice "
                + "WHERE LOWER(invoice_status) = 'paid'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("paid_invoices");
            }

        } catch (SQLException e) {
            System.err.println("Error counting paid invoices: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalReservations() {
        String sql = "SELECT COUNT(*) AS total_reservations FROM Reservation";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_reservations");
            }

        } catch (SQLException e) {
            System.err.println("Error counting reservations: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalMaintenanceRecords() {
        String sql = "SELECT COUNT(*) AS total_maintenance FROM Maintenance";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_maintenance");
            }

        } catch (SQLException e) {
            System.err.println("Error counting maintenance records: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalReviews() {
        String sql = "SELECT COUNT(*) AS total_reviews FROM Review";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_reviews");
            }

        } catch (SQLException e) {
            System.err.println("Error counting reviews: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public double getAverageRating() {
        String sql = "SELECT COALESCE(AVG(rating), 0) AS average_rating FROM Review";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("average_rating");
            }

        } catch (SQLException e) {
            System.err.println("Error calculating average rating: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    public List<ReportItem> getReportItems() {
        List<ReportItem> reports = new ArrayList<>();

        double totalRevenue = getTotalRevenue();
        int activeRentals = getActiveRentals();
        int totalCars = getTotalCars();
        double fleetUtilization = getFleetUtilization();

        int totalInvoices = getTotalInvoices();
        int paidInvoices = getPaidInvoices();

        int totalReservations = getTotalReservations();
        int totalMaintenance = getTotalMaintenanceRecords();

        int totalReviews = getTotalReviews();
        double averageRating = getAverageRating();

        reports.add(new ReportItem(
                "Revenue Summary",
                "Total revenue from paid invoices is " + String.format("%.2f", totalRevenue) + " USD.",
                "All time",
                "Ready"
        ));

        reports.add(new ReportItem(
                "Active Rentals",
                "There are " + activeRentals + " active rental contracts currently.",
                "Current",
                "Ready"
        ));

        reports.add(new ReportItem(
                "Fleet Utilization",
                "Fleet utilization is " + String.format("%.1f", fleetUtilization)
                        + "% based on " + activeRentals + " active contracts and " + totalCars + " total cars.",
                "Current",
                "Ready"
        ));

        reports.add(new ReportItem(
                "Invoice Status",
                paidInvoices + " paid invoices out of " + totalInvoices + " total invoices.",
                "All time",
                "Ready"
        ));

        reports.add(new ReportItem(
                "Reservations Overview",
                "Total reservations recorded in the system: " + totalReservations + ".",
                "All time",
                "Ready"
        ));

        reports.add(new ReportItem(
                "Maintenance Overview",
                "Total maintenance records recorded in the system: " + totalMaintenance + ".",
                "All time",
                "Ready"
        ));

        reports.add(new ReportItem(
                "Reviews Overview",
                "Total customer reviews: " + totalReviews + ", average rating: "
                        + String.format("%.1f", averageRating) + " out of 5.",
                "All time",
                "Ready"
        ));

        return reports;
    }
}