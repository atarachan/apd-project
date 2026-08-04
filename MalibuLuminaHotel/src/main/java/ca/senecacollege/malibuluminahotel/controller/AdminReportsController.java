package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.services.ReportService;
import ca.senecacollege.malibuluminahotel.util.CsvExporter;
import ca.senecacollege.malibuluminahotel.util.PdfExporter;
import ca.senecacollege.malibuluminahotel.util.TxtExporter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminReportsController {

    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Map<String, String>> reportTableView;
    @FXML private TextArea summaryTextArea;
    @FXML private Button generateButton;
    @FXML private Button exportCsvButton;
    @FXML private Button exportPdfButton;
    @FXML private Button exportTxtButton;

    private ReportService reportService;
    private List<Map<String, String>> currentReportData;

    @FXML
    public void initialize() {
        reportService = new ReportService();
        currentReportData = new ArrayList<>();

        // Setup report type combo box
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
                "Revenue Report",
                "Occupancy Report",
                "Activity Log Report",
                "Feedback Summary"
        ));
        reportTypeComboBox.getSelectionModel().selectFirst();

        // Set default date range (last 30 days)
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(30));

        // Disable export buttons initially
        exportCsvButton.setDisable(true);
        exportPdfButton.setDisable(true);
        exportTxtButton.setDisable(true);
    }

    @FXML
    private void handleGenerateReport(ActionEvent event) {
        String reportType = reportTypeComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (reportType == null || startDate == null || endDate == null) {
            showAlert("Error", "Please select report type and date range.", Alert.AlertType.ERROR);
            return;
        }

        if (startDate.isAfter(endDate)) {
            showAlert("Error", "Start date must be before end date.", Alert.AlertType.ERROR);
            return;
        }

        try {
            switch (reportType) {
                case "Revenue Report" -> generateRevenueReport(startDate, endDate);
                case "Occupancy Report" -> generateOccupancyReport(startDate, endDate);
                case "Activity Log Report" -> generateActivityLogReport(startDate, endDate);
                case "Feedback Summary" -> generateFeedbackSummary();
                default -> showAlert("Error", "Unknown report type.", Alert.AlertType.ERROR);
            }

            // Enable export buttons after successful generation
            exportCsvButton.setDisable(currentReportData.isEmpty());
            exportPdfButton.setDisable(currentReportData.isEmpty());
            exportTxtButton.setDisable(false);

        } catch (Exception e) {
            showAlert("Error", "Failed to generate report: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        currentReportData = reportService.generateRevenueReport(startDate, endDate);
        displayReportInTable(currentReportData);
        summaryTextArea.setText(String.format("Revenue Report\nPeriod: %s to %s\nTotal Records: %d",
                startDate, endDate, currentReportData.size()));
    }

    private void generateOccupancyReport(LocalDate startDate, LocalDate endDate) {
        currentReportData = reportService.generateOccupancyReport(startDate, endDate);
        displayReportInTable(currentReportData);
        summaryTextArea.setText(String.format("Occupancy Report\nPeriod: %s to %s\nTotal Records: %d",
                startDate, endDate, currentReportData.size()));
    }

    private void generateActivityLogReport(LocalDate startDate, LocalDate endDate) {
        currentReportData = reportService.generateActivityLogReport(startDate, endDate);
        displayReportInTable(currentReportData);
        summaryTextArea.setText(String.format("Activity Log Report\nPeriod: %s to %s\nTotal Records: %d",
                startDate, endDate, currentReportData.size()));
    }

    private void generateFeedbackSummary() {
        Map<String, Object> summary = reportService.generateFeedbackSummary();
        
        // Convert summary to table format
        currentReportData = new ArrayList<>();
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            Map<String, String> row = Map.of(
                    "Metric", entry.getKey(),
                    "Value", entry.getValue().toString()
            );
            currentReportData.add(row);
        }
        
        displayReportInTable(currentReportData);
        
        // Build summary text
        StringBuilder sb = new StringBuilder("Feedback Summary\n\n");
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        summaryTextArea.setText(sb.toString());
    }

    private void displayReportInTable(List<Map<String, String>> data) {
        if (data.isEmpty()) {
            showAlert("Info", "No data found for the selected criteria.", Alert.AlertType.INFORMATION);
            reportTableView.getItems().clear();
            reportTableView.getColumns().clear();
            return;
        }

        // Clear existing columns
        reportTableView.getColumns().clear();

        // Create columns from first row keys
        Map<String, String> firstRow = data.get(0);
        for (String columnName : firstRow.keySet()) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().getOrDefault(columnName, "")));
            column.setPrefWidth(150);
            reportTableView.getColumns().add(column);
        }

        // Set data
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList(data);
        reportTableView.setItems(items);
    }

    @FXML
    private void handleExportCsv(ActionEvent event) {
        if (currentReportData.isEmpty()) {
            showAlert("Error", "No data to export.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("report.csv");

        File file = fileChooser.showSaveDialog(reportTableView.getScene().getWindow());
        if (file != null) {
            try {
                CsvExporter.exportToCsv(currentReportData, file.getAbsolutePath());
                showAlert("Success", "Report exported to CSV successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to export CSV: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleExportPdf(ActionEvent event) {
        if (currentReportData.isEmpty()) {
            showAlert("Error", "No data to export.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("report.pdf");

        File file = fileChooser.showSaveDialog(reportTableView.getScene().getWindow());
        if (file != null) {
            try {
                String title = reportTypeComboBox.getValue();
                PdfExporter.exportToPdf(currentReportData, file.getAbsolutePath(), title);
                showAlert("Success", "Report exported to PDF successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleExportTxt(ActionEvent event) {
        String content = summaryTextArea.getText();
        if (content == null || content.trim().isEmpty()) {
            showAlert("Error", "No summary to export.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("report.txt");

        File file = fileChooser.showSaveDialog(reportTableView.getScene().getWindow());
        if (file != null) {
            try {
                TxtExporter.exportToTxt(content, file.getAbsolutePath());
                showAlert("Success", "Report exported to TXT successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to export TXT: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
