package carrentalsystem.controllers;

import carrentalsystem.dao.ReportDAO;
import carrentalsystem.models.ReportItem;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReportsController {

    @FXML private Label lblTotalRevenue;
    @FXML private Label lblActiveRentals;
    @FXML private Label lblFleetUtilization;
    @FXML private Label lblAverageRating;

    @FXML private TableView<ReportItem> tableReports;
    @FXML private TableColumn<ReportItem, String> colReportType;
    @FXML private TableColumn<ReportItem, String> colDescription;
    @FXML private TableColumn<ReportItem, String> colDateRange;
    @FXML private TableColumn<ReportItem, String> colStatus;

    private final ReportDAO reportDAO = new ReportDAO();
    private final ObservableList<ReportItem> reportList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadReports();
    }

    private void setupTable() {
        colReportType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDateRange.setCellValueFactory(new PropertyValueFactory<>("dateRange"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colReportType.setCellFactory(column -> createEnglishTextCell());
        colDescription.setCellFactory(column -> createEnglishTextCell());
        colDateRange.setCellFactory(column -> createEnglishTextCell());
        colStatus.setCellFactory(column -> createEnglishTextCell());

        tableReports.setItems(reportList);
    }

    private TableCell<ReportItem, String> createEnglishTextCell() {
        return new TableCell<ReportItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(forceEnglishNumbers(item));
                }
            }
        };
    }

    private void loadReports() {
        double totalRevenue = reportDAO.getTotalRevenue();
        int activeRentals = reportDAO.getActiveRentals();
        double fleetUtilization = reportDAO.getFleetUtilization();
        double averageRating = reportDAO.getAverageRating();

        lblTotalRevenue.setText(formatUsd(totalRevenue));
        lblActiveRentals.setText(formatInt(activeRentals));
        lblFleetUtilization.setText(formatPercent(fleetUtilization));
        lblAverageRating.setText(formatNumber(averageRating) + " / 5");

        reportList.clear();

        List<ReportItem> reports = reportDAO.getReportItems();
        reportList.addAll(reports);

        tableReports.refresh();
    }

    @FXML
    private void handleGenerateReport() {
        loadReports();

        showAlert(
                Alert.AlertType.INFORMATION,
                "Report Generated",
                "Reports have been refreshed successfully."
        );
    }

    @FXML
    private void handleExport() {
        showAlert(
                Alert.AlertType.INFORMATION,
                "Export",
                "Export feature is prepared for future implementation."
        );
    }

    private String formatUsd(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(amount) + " USD";
    }

    private String formatNumber(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formatter = new DecimalFormat("#,##0.##", symbols);
        return formatter.format(number);
    }

    private String formatInt(int number) {
        return String.format(Locale.US, "%d", number);
    }

    private String formatPercent(double percent) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formatter = new DecimalFormat("#,##0.0", symbols);
        return formatter.format(percent) + "%";
    }

    private String forceEnglishNumbers(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace('٠', '0')
                .replace('١', '1')
                .replace('٢', '2')
                .replace('٣', '3')
                .replace('٤', '4')
                .replace('٥', '5')
                .replace('٦', '6')
                .replace('٧', '7')
                .replace('٨', '8')
                .replace('٩', '9')
                .replace('۰', '0')
                .replace('۱', '1')
                .replace('۲', '2')
                .replace('۳', '3')
                .replace('۴', '4')
                .replace('۵', '5')
                .replace('۶', '6')
                .replace('۷', '7')
                .replace('۸', '8')
                .replace('۹', '9')
                .replace('٫', '.')
                .replace('٬', ',');
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(forceEnglishNumbers(message));
        alert.showAndWait();
    }
}