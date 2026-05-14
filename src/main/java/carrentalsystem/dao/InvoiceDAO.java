package carrentalsystem.dao;

import carrentalsystem.database.DatabaseConnection;
import carrentalsystem.models.Invoice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();

        String sql =
                "SELECT invoice_id, issue_date, rental_cost, extra_charges, late_fees, "
                + "discount, tax, total_amount, invoice_status, contract_id "
                + "FROM Invoice "
                + "ORDER BY invoice_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error loading invoices: " + e.getMessage());
            e.printStackTrace();
        }

        return invoices;
    }

    public List<Invoice> getInvoicesByCustomerId(int customerId) {
        List<Invoice> invoices = new ArrayList<>();

        String sql =
                "SELECT i.invoice_id, i.issue_date, i.rental_cost, i.extra_charges, "
                + "i.late_fees, i.discount, i.tax, i.total_amount, "
                + "i.invoice_status, i.contract_id "
                + "FROM Invoice i "
                + "JOIN Rental_Contract rc ON i.contract_id = rc.contract_id "
                + "WHERE rc.customer_id = ? "
                + "ORDER BY i.invoice_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading customer invoices: " + e.getMessage());
            e.printStackTrace();
        }

        return invoices;
    }

    public List<Invoice> searchInvoices(String keyword) {
        List<Invoice> invoices = new ArrayList<>();

        String sql =
                "SELECT invoice_id, issue_date, rental_cost, extra_charges, late_fees, "
                + "discount, tax, total_amount, invoice_status, contract_id "
                + "FROM Invoice "
                + "WHERE invoice_status LIKE ? "
                + "OR CAST(invoice_id AS CHAR) LIKE ? "
                + "OR CAST(contract_id AS CHAR) LIKE ? "
                + "ORDER BY invoice_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            stmt.setString(1, searchValue);
            stmt.setString(2, searchValue);
            stmt.setString(3, searchValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching invoices: " + e.getMessage());
            e.printStackTrace();
        }

        return invoices;
    }

    public boolean addInvoice(Invoice invoice) {
    String sql =
            "INSERT INTO Invoice "
            + "(rental_cost, extra_charges, late_fees, discount, tax, total_amount, invoice_status, contract_id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setDouble(1, invoice.getRentalCost());
        stmt.setDouble(2, invoice.getExtraCharges());
        stmt.setDouble(3, invoice.getLateFees());
        stmt.setDouble(4, invoice.getDiscount());
        stmt.setDouble(5, invoice.getTax());
        stmt.setDouble(6, invoice.getTotalAmount());
        stmt.setString(7, invoice.getInvoiceStatus());
        stmt.setInt(8, invoice.getContractId());

        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error adding invoice: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    public boolean updateInvoice(Invoice invoice) {
        String sql =
                "UPDATE Invoice SET "
                + "issue_date = ?, "
                + "rental_cost = ?, "
                + "extra_charges = ?, "
                + "late_fees = ?, "
                + "discount = ?, "
                + "tax = ?, "
                + "total_amount = ?, "
                + "invoice_status = ?, "
                + "contract_id = ? "
                + "WHERE invoice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillInvoiceStatement(stmt, invoice, true);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating invoice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInvoice(int invoiceId) {
        String sql = "DELETE FROM Invoice WHERE invoice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting invoice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();

        invoice.setInvoiceId(rs.getInt("invoice_id"));

        Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) {
            invoice.setIssueDate(issueDate.toLocalDate());
        }

        invoice.setRentalCost(rs.getDouble("rental_cost"));
        invoice.setExtraCharges(rs.getDouble("extra_charges"));
        invoice.setLateFees(rs.getDouble("late_fees"));
        invoice.setDiscount(rs.getDouble("discount"));
        invoice.setTax(rs.getDouble("tax"));
        invoice.setTotalAmount(rs.getDouble("total_amount"));
        invoice.setInvoiceStatus(rs.getString("invoice_status"));
        invoice.setContractId(rs.getInt("contract_id"));

        return invoice;
    }

    private void fillInvoiceStatement(PreparedStatement stmt, Invoice invoice, boolean includeId)
            throws SQLException {

        if (invoice.getIssueDate() != null) {
            stmt.setDate(1, Date.valueOf(invoice.getIssueDate()));
        } else {
            stmt.setDate(1, null);
        }

        stmt.setDouble(2, invoice.getRentalCost());
        stmt.setDouble(3, invoice.getExtraCharges());
        stmt.setDouble(4, invoice.getLateFees());
        stmt.setDouble(5, invoice.getDiscount());
        stmt.setDouble(6, invoice.getTax());
        stmt.setDouble(7, invoice.getTotalAmount());
        stmt.setString(8, invoice.getInvoiceStatus());
        stmt.setInt(9, invoice.getContractId());

        if (includeId) {
            stmt.setInt(10, invoice.getInvoiceId());
        }
    }
}