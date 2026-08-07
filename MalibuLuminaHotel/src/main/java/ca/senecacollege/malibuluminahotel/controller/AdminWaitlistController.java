package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.WaitlistEntry;
import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatusType;
import ca.senecacollege.malibuluminahotel.services.IWaitlistService;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox;
import java.util.List;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.repositories.IGuestRepository;
import ca.senecacollege.malibuluminahotel.repositories.IRoomTypeRepository;
import javafx.scene.layout.GridPane;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import java.time.LocalDate;

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

    private final IWaitlistService waitlistService;
    private final IGuestRepository guestRepository;
    private final IRoomTypeRepository roomTypeRepository;

    @Inject
    public AdminWaitlistController(
            IWaitlistService waitlistService,
            IGuestRepository guestRepository,
            IRoomTypeRepository roomTypeRepository) {

        this.waitlistService = waitlistService;
        this.guestRepository = guestRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @FXML
    private void initialize() {
        // Initialize table columns if they exist
        if (waitlistTable != null && idColumn != null) {
            idColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getWaitlistId()));

            guestNameColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getGuestName()));

            phoneColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getPhone()));

            roomTypeColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getRoomType()));

            checkInColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getCheckIn()));

            checkOutColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getCheckOut()));

            dateAddedColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getDateAdded()));

            statusColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));

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
                waitlistTable.getItems().clear();
                waitlistTable.setItems(rows);
                waitlistTable.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.toString());
        }
    }

    @FXML
    private void handleAddToWaitlist(ActionEvent event) {

        Dialog<WaitlistEntry> dialog = new Dialog<>();
        dialog.setTitle("Add to Waitlist");

        ButtonType saveButton =
                new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton,
                ButtonType.CANCEL
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<Guest> guestCombo = new ComboBox<>();
        guestCombo.setItems(FXCollections.observableArrayList(
                guestRepository.findAll()
        ));

        guestCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Guest guest) {
                if (guest == null) return "";
                return guest.getFirstName() + " " + guest.getLastName();
            }

            @Override
            public Guest fromString(String string) {
                return null;
            }
        });

        ComboBox<RoomType> roomTypeCombo = new ComboBox<>();
        roomTypeCombo.setItems(FXCollections.observableArrayList(
                roomTypeRepository.findAll()
        ));

        roomTypeCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(RoomType roomType) {
                if (roomType == null) return "";
                return roomType.getRoomTypeName().toString();
            }

            @Override
            public RoomType fromString(String string) {
                return null;
            }
        });

        DatePicker checkIn = new DatePicker(LocalDate.now());
        DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(1));

        grid.add(new Label("Guest"), 0, 0);
        grid.add(guestCombo, 1, 0);

        grid.add(new Label("Room Type"), 0, 1);
        grid.add(roomTypeCombo, 1, 1);

        grid.add(new Label("Check In"), 0, 2);
        grid.add(checkIn, 1, 2);

        grid.add(new Label("Check Out"), 0, 3);
        grid.add(checkOut, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {

            if (button != saveButton) {
                return null;
            }

            if (guestCombo.getValue() == null ||
                    roomTypeCombo.getValue() == null ||
                    checkIn.getValue() == null ||
                    checkOut.getValue() == null) {

                showAlert("Missing Information",
                        "Please complete all fields.");

                return null;
            }

            return waitlistService.addToWaitlist(
                    guestCombo.getValue(),
                    roomTypeCombo.getValue(),
                    checkIn.getValue(),
                    checkOut.getValue()
            );
        });

        dialog.showAndWait();

        loadWaitlist();
    }

    @FXML
    private void handleEditWaitlist(ActionEvent event) {

        WaitlistTableRow selectedRow =
                waitlistTable.getSelectionModel().getSelectedItem();

        if (selectedRow == null) {
            showAlert("No Selection",
                    "Please select a waitlist entry to edit.");
            return;
        }

        WaitlistEntry entry =
                waitlistService.findById(selectedRow.getWaitlistId()).orElse(null);

        if (entry == null) {
            showAlert("Error",
                    "Unable to find the selected waitlist entry.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Waitlist Entry");

        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<RoomType> roomTypeCombo = new ComboBox<>();
        roomTypeCombo.setItems(FXCollections.observableArrayList(
                roomTypeRepository.findAll()
        ));
        roomTypeCombo.setConverter(new javafx.util.StringConverter<>() {

            @Override
            public String toString(RoomType roomType) {
                if (roomType == null) {
                    return "";
                }
                return roomType.getRoomTypeName().toString();
            }

            @Override
            public RoomType fromString(String string) {
                return null;
            }
        });
        roomTypeCombo.setValue(entry.getRoomType());

        DatePicker checkIn = new DatePicker(entry.getRequestedCheckin());
        DatePicker checkOut = new DatePicker(entry.getRequestedCheckout());

        grid.add(new Label("Guest"), 0, 0);
        grid.add(new Label(entry.getGuest().getFirstName()
                + " "
                + entry.getGuest().getLastName()), 1, 0);

        grid.add(new Label("Room Type"), 0, 1);
        grid.add(roomTypeCombo, 1, 1);

        grid.add(new Label("Check In"), 0, 2);
        grid.add(checkIn, 1, 2);

        grid.add(new Label("Check Out"), 0, 3);
        grid.add(checkOut, 1, 3);

        dialog.getDialogPane().setContent(grid);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            entry.setRoomType(roomTypeCombo.getValue());
            entry.setRequestedCheckin(checkIn.getValue());
            entry.setRequestedCheckout(checkOut.getValue());

            waitlistService.updateEntry(entry);

            loadWaitlist();

            showAlert("Success", "Waitlist entry updated successfully.");
        }

    }

    @FXML
    private void handleRemoveWaitlist(ActionEvent event) {

        WaitlistTableRow selected =
                waitlistTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("No Selection",
                    "Please select a waitlist entry to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Waitlist Entry");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to remove this waitlist entry?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        WaitlistEntry entry =
                waitlistService.findById(selected.getWaitlistId()).orElse(null);

        if (entry == null) {
            showAlert("Error", "Waitlist entry not found.");
            return;
        }

        waitlistService.deleteEntry(entry);

        loadWaitlist();

        showAlert("Success", "Waitlist entry removed.");
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
