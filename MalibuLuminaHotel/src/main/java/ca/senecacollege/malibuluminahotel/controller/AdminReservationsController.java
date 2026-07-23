package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.ReservationItem;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository;
import ca.senecacollege.malibuluminahotel.repositories.ReservationRepositoryImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class AdminReservationsController {

    @FXML private VBox reservationsTableBody;

    @FXML
    public void initialize() {
        IReservationRepository repo = new ReservationRepositoryImpl();
        List<Reservation> reservations = repo.findAllWithDetails();

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
                cell(formatStatus(r.getStatus()), 160)
            );
            reservationsTableBody.getChildren().add(row);
        }
    }

    private String getRoomTypeName(Reservation r) {
        List<ReservationItem> items = r.getReservationItems();
        if (items == null || items.isEmpty()) return "—";
        ReservationItem first = items.get(0);
        if (first.getRoom() == null) return "—";
        if (first.getRoom().getRoomType() == null) return "—";
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
        if (status == null) return "Pending";
        return switch (status) {
            case PENDING -> "Pending";
            case CONFIRMED -> "Confirmed";
            case CHECKED_IN -> "Checked In";
            case CHECKED_OUT -> "Checked Out";
            case CANCELLED -> "Cancelled";
        };
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }
}
