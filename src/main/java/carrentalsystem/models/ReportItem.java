package carrentalsystem.models;

public class ReportItem {

    private String reportType;
    private String description;
    private String dateRange;
    private String status;

    public ReportItem() {
    }

    public ReportItem(String reportType, String description, String dateRange, String status) {
        this.reportType = reportType;
        this.description = description;
        this.dateRange = dateRange;
        this.status = status;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}