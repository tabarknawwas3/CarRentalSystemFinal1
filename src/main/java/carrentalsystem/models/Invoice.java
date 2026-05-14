package carrentalsystem.models;

import java.time.LocalDate;

public class Invoice {

    private int invoiceId;
    private LocalDate issueDate;
    private double rentalCost;
    private double extraCharges;
    private double lateFees;
    private double discount;
    private double tax;
    private double totalAmount;
    private String invoiceStatus;
    private int contractId;

    public Invoice() {
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public double getRentalCost() {
        return rentalCost;
    }

    public void setRentalCost(double rentalCost) {
        this.rentalCost = rentalCost;
    }

    public double getExtraCharges() {
        return extraCharges;
    }

    public void setExtraCharges(double extraCharges) {
        this.extraCharges = extraCharges;
    }

    public double getLateFees() {
        return lateFees;
    }

    public void setLateFees(double lateFees) {
        this.lateFees = lateFees;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }
}