package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.*;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;
import ca.senecacollege.malibuluminahotel.repositories.*;
import ca.senecacollege.malibuluminahotel.security.SessionManager;
import ca.senecacollege.malibuluminahotel.services.IActivityLogService;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdminReservationsController {

    @FXML
    private VBox reservationsTableBody;

    private final IReservationRepository reservationRepository;
    private final IGuestRepository guestRepository;
    private final IRoomRepository roomRepository;
    private final IRoomTypeRepository roomTypeRepository;
    private final IReservationItemRepository reservationItemRepository;
    private final IActivityLogService activityLogService;

    @Inject
    public AdminReservationsController(IReservationRepository reservationRepository,
                                       IGuestRepository guestRepository,
                                       IRoomRepository roomRepository,
                                       IRoomTypeRepository roomTypeRepository,
                                       IReservationItemRepository reservationItemRepository,
                                       IActivityLogService activityLogService) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.reservationItemRepository = reservationItemRepository;
        this.activityLogService = activityLogService;
    }

    @FXML
    public void initialize() {
        loadReservations();
    }

    private void loadReservations() {
        reservationsTableBody.getChildren().clear();

        List<Reservation> reservations = reservationRepository.findAllWithDetails();

        if (reservations.isEmpty()) {
            Label empty = new Label("No reservations found.");
            empty.getStyleClass().add("helper-label");
            reservationsTableBody.getChildren().add(empty);
            return;
        }

        for (Reservation r : reservations) {
            Guest g = r.getGuest();
            String roomTypeName = getRoomTypeName(r);

            HBox row = new HBox();
            row.getStyleClass().add("table-data-row");
            row.getChildren().addAll(
                    cell("R" + r.getReservationId(), 130),
                    cell(g.getFirstName() + " " + g.getLastName(), 180),
                    cell(roomTypeName, 140),
                    cell(r.getCheckInDate() != null ? r.getCheckInDate().toString() : "—", 120),
                    cell(r.getCheckOutDate() != null ? r.getCheckOutDate().toString() : "—", 120),
                    cell(formatStatus(r.getStatus()), 160),
                    createActionButtons(r));
            reservationsTableBody.getChildren().add(row);
        }
    }

    private HBox createActionButtons(Reservation reservation) {
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("small-button");
        editBtn.setPrefWidth(70);
        editBtn.setOnAction(e -> handleEditReservation(reservation));

        Button deleteBtn = new Button("Cancel");
        deleteBtn.getStyleClass().add("small-button");
        deleteBtn.setPrefWidth(70);
        deleteBtn.setOnAction(e -> handleDeleteReservation(reservation));

        HBox actions = new HBox(10, editBtn, deleteBtn);
        actions.setPrefWidth(200);
        actions.setAlignment(Pos.CENTER_LEFT);
        return actions;
    }

    private String getRoomTypeName(Reservation r) {
        List<ReservationItem> items = r.getReservationItems();
        if (items == null || items.isEmpty())
            return "—";
        ReservationItem first = items.get(0);
        if (first.getRoom() == null)
            return "—";
        if (first.getRoom().getRoomType() == null)
            return "—";
        String name = first.getRoom().getRoomType().getRoomTypeName().toString();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    private Label cell(String text, double width) {
        Label l = new Label(text != null ? text : "—");
        l.setPrefWidth(width);
        l.getStyleClass().add("table-cell");
        return l;
    }

    private String formatStatus(ReservationStatus status) {
        if (status == null)
            return "Pending";
        return switch (status) {
            case PENDING -> "Pending";
            case CONFIRMED -> "Confirmed";
            case CHECKED_IN -> "Checked In";
            case CHECKED_OUT -> "Checked Out";
            case CANCELLED -> "Cancelled";
        };
    }

    @FXML
    private void handleCreateReservation(ActionEvent event) {
        Dialog<Reservation> dialog = createReservationDialog(null);
        Optional<Reservation> result = dialog.showAndWait();

        result.ifPresent(reservation -> {
            try {
                // Save the reservation
                Reservation saved = reservationRepository.save(reservation);

                // Log the action
                AdminUser admin = SessionManager.getInstance().getCurrentUser();
                if (admin != null) {
                    activityLogService.logReservationCreated(admin, saved);
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation created successfully!");
                loadReservations();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create reservation: " + e.getMessage());
            }
        });
    }

    private void handleEditReservation(Reservation reservation) {
        Dialog<Reservation> dialog = createReservationDialog(reservation);
        Optional<Reservation> result = dialog.showAndWait();

        result.ifPresent(updated -> {
            try {
                // Update reservation fields
                reservation.setCheckInDate(updated.getCheckInDate());
                reservation.setCheckOutDate(updated.getCheckOutDate());
                reservation.setStatus(updated.getStatus());
                reservation.setAdults(updated.getAdults());
                reservation.setChildren(updated.getChildren());

                // Save the updated reservation
                reservationRepository.update(reservation);

                // Log the action
                AdminUser admin = SessionManager.getInstance().getCurrentUser();
                if (admin != null) {
                    activityLogService.logReservationUpdated(admin, reservation);
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation updated successfully!");
                loadReservations();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update reservation: " + e.getMessage());
            }
        });
    }

    private void handleDeleteReservation(Reservation reservation) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Reservation #" + reservation.getReservationId());
        confirmAlert.setContentText("Are you sure you want to cancel this reservation?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Mark as cancelled instead of deleting
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.update(reservation);

                // Log the action
                AdminUser admin = SessionManager.getInstance().getCurrentUser();
                if (admin != null) {
                    activityLogService.logReservationCancelled(admin, reservation);
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation cancelled successfully!");
                loadReservations();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel reservation: " + e.getMessage());
            }
        }
    }

    private Dialog<Reservation> createReservationDialog(Reservation existing) {
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Create New Reservation" : "Edit Reservation");
        dialog.setHeaderText(existing == null ? "Enter reservation details" : "Update reservation details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Guest selection
        ComboBox<Guest> guestCombo = new ComboBox<>();
        List<Guest> guests = guestRepository.findAll();
        guestCombo.setItems(FXCollections.observableArrayList(guests));
        guestCombo.setConverter(new javafx.util.StringConverter<Guest>() {
            @Override
            public String toString(Guest guest) {
                return guest == null ? "" : guest.getFirstName() + " " + guest.getLastName();
            }

            @Override
            public Guest fromString(String string) {
                return null;
            }
        });

        // Date pickers
        DatePicker checkInPicker = new DatePicker();
        DatePicker checkOutPicker = new DatePicker();

        // Number of adults and children
        Spinner<Integer> adultsSpinner = new Spinner<>(1, 10, 1);
        Spinner<Integer> childrenSpinner = new Spinner<>(0, 10, 0);

        // Status
        ComboBox<ReservationStatus> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(ReservationStatus.values()));

        // Set existing values if editing
        if (existing != null) {
            guestCombo.setValue(existing.getGuest());
            checkInPicker.setValue(existing.getCheckInDate());
            checkOutPicker.setValue(existing.getCheckOutDate());
            adultsSpinner.getValueFactory().setValue(existing.getAdults());
            childrenSpinner.getValueFactory().setValue(existing.getChildren());
            statusCombo.setValue(existing.getStatus());
            guestCombo.setDisable(true); // Don't allow changing guest
        } else {
            checkInPicker.setValue(LocalDate.now());
            checkOutPicker.setValue(LocalDate.now().plusDays(1));
            statusCombo.setValue(ReservationStatus.PENDING);
        }

        grid.add(new Label("Guest:"), 0, 0);
        grid.add(guestCombo, 1, 0);
        grid.add(new Label("Check-In Date:"), 0, 1);
        grid.add(checkInPicker, 1, 1);
        grid.add(new Label("Check-Out Date:"), 0, 2);
        grid.add(checkOutPicker, 1, 2);
        grid.add(new Label("Adults:"), 0, 3);
        grid.add(adultsSpinner, 1, 3);
        grid.add(new Label("Children:"), 0, 4);
        grid.add(childrenSpinner, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert result to reservation
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Guest selectedGuest = guestCombo.getValue();
                LocalDate checkIn = checkInPicker.getValue();
                LocalDate checkOut = checkOutPicker.getValue();
                Integer adults = adultsSpinner.getValue();
                Integer children = childrenSpinner.getValue();
                ReservationStatus status = statusCombo.getValue();

                if (selectedGuest == null || checkIn == null || checkOut == null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
                    return null;
                }

                if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Check-out date must be after check-in date.");
                    return null;
                }

                if (existing != null) {
                    // Return existing with updated values
                    existing.setCheckInDate(checkIn);
                    existing.setCheckOutDate(checkOut);
                    existing.setStatus(status);
                    existing.setAdults(adults);
                    existing.setChildren(children);
                    return existing;
                } else {
                    // Create new reservation
                    Reservation newReservation = new Reservation();
                    newReservation.setGuest(selectedGuest);
                    newReservation.setCheckInDate(checkIn);
                    newReservation.setCheckOutDate(checkOut);
                    newReservation.setAdults(adults);
                    newReservation.setChildren(children);
                    newReservation.setStatus(status);
                    return newReservation;
                }
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }
}
