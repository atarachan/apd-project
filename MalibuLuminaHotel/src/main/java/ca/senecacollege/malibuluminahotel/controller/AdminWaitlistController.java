package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.WaitlistEntry;
import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatusType;
import ca.senecacollege.malibuluminahotel.services.WaitlistService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class AdminWaitlistController {

    @FXML
    private TableView<WaitlistTableRow> waitlistTable;
    @FXML
    private TableColumn<WaitlistTableRow, Long> idColumn;
    @FXML
    private TableColumn<WaitlistTableRow, String> guestNameColumn;
    @FXML
    private TableColumn<WaitlistTableRow, String> phoneColumn;
    @FXML
    private TableColumn<WaitlistTableRow, String> roomTypeColumn;
    @FXML
    private TableColumn<WaitlistTableRow, String> checkInColumn;
    @FXML
    private TableColumn<WaitlistTableRow, String> checkOutColumn;
    @FXML
    private TableColumn<WaitlistTableRow, String> dateAddedColumn;
    @FXML
    private TableColumn<WaitlistTableRow, String> statusColumn;
    @FXML
    private ComboBox<String> statusFilter;

    private final WaitlistService waitlistService;

    public AdminWaitlistController() {
        this.waitlistService = new WaitlistService();
    }

    @FXML
    private void initialize() {
        // Initialize table columns if they exist
        if (waitlistTable != null && idColumn != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("waitlistId"));
            guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
            checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
            checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
            dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            loadWaitlist();
        }

        // Initialize filter if it exists
        if (statusFilter != null) {
            statusFilter.setItems(FXCollections.observableArrayList(
                    "All", "IN_QUEUE", "SPOT_AVAILABLE", "WITHDRAWN"));
            statusFilter.setValue("All");
        }
    }

    /**
     * Load all waitlist entries into the table.
     */
    private void loadWaitlist() {
        try {
            List<WaitlistEntry> entries = waitlistService.getAllWaitlistEntries();
            ObservableList<WaitlistTableRow> rows = FXCollections.observableArrayList();

            for (WaitlistEntry entry : entries) {
                rows.add(new WaitlistTableRow(entry));
            }

            if (waitlistTable != null) {
                waitlistTable.setItems(rows);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load waitlist: " + e.getMessage());
        }
    }

    /**
     * Filter waitlist by status.
     */
    @FXML
    private void handleFilterByStatus(ActionEvent event) {
        String status = statusFilter.getValue();
        if (status == null || status.equals("All")) {
            loadWaitlist();
            return;
        }

        try {
            WaitlistStatusType statusType = WaitlistStatusType.valueOf(status);
            List<WaitlistEntry> entries = waitlistService.getEntriesByStatus(statusType);
            ObservableList<WaitlistTableRow> rows = FXCollections.observableArrayList();

            for (WaitlistEntry entry : entries) {
                rows.add(new WaitlistTableRow(entry));
            }

            if (waitlistTable != null) {
                waitlistTable.setItems(rows);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to filter waitlist: " + e.getMessage());
        }
    }

    /**
     * Handle notify guest action.
     */
    @FXML
    private void handleNotifyGuest(ActionEvent event) {
        WaitlistTableRow selected = waitlistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a waitlist entry to notify.");
            return;
        }

        // In a real implementation, you would:
        // 1. Get the WaitlistEntry entity by ID
        // 2. Call waitlistService.notifyGuestSpotAvailable(entry)
        // 3. Reload the table

        showAlert("Success", "Guest would be notified (feature pending full implementation).");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Table row wrapper for WaitlistEntry.
     */
    public static class WaitlistTableRow {
        private final Long waitlistId;
        private final String guestName;
        private final String phone;
        private final String roomType;
        private final String checkIn;
        private final String checkOut;
        private final String dateAdded;
        private final String status;

        public WaitlistTableRow(WaitlistEntry entry) {
            Guest guest = entry.getGuest();
            this.waitlistId = entry.getWaitlistId();
            this.guestName = guest.getFirstName() + " " + guest.getLastName();
            this.phone = guest.getPhone();
            this.roomType = entry.getRoomType().getRoomTypeName().toString();
            this.checkIn = entry.getRequestedCheckin().toString();
            this.checkOut = entry.getRequestedCheckout().toString();
            this.dateAdded = entry.getDateAdded().toString();
            this.status = entry.getStatus().toString();
        }

        public Long getWaitlistId() {
            return waitlistId;
        }

        public String getGuestName() {
            return guestName;
        }

        public String getPhone() {
            return phone;
        }

        public String getRoomType() {
            return roomType;
        }

        public String getCheckIn() {
            return checkIn;
        }

        public String getCheckOut() {
            return checkOut;
        }

        public String getDateAdded() {
            return dateAdded;
        }

        public String getStatus() {
            return status;
        }
    }
}
