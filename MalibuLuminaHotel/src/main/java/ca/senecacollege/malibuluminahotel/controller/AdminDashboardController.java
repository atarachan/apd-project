package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository;
import ca.senecacollege.malibuluminahotel.repositories.ReservationRepositoryImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class AdminDashboardController {

    @FXML private TextField searchField;
    @FXML private VBox reservationsTableBody;

    @FXML
    public void initialize() {
        IReservationRepository repo = new ReservationRepositoryImpl();
        List<Reservation> reservations = repo.findAll();

        if (reservations.isEmpty()) {
            Label empty = new Label("No reservations found.");
            empty.getStyleClass().add("helper-label");
            reservationsTableBody.getChildren().add(empty);
            return;
        }

        for (Reservation r : reservations) {
            Guest g = r.getGuest();
            HBox row = new HBox();
            row.getStyleClass().add("table-data-row");
            row.getChildren().addAll(
                cell("R" + r.getReservationId(), 120),
                cell(g.getFirstName() + " " + g.getLastName(), 160),
                cell(g.getPhone() != null ? g.getPhone() : "—", 120),
                cell(g.getEmail(), 180),
                cell(r.getCheckInDate() != null ? r.getCheckInDate().toString() : "—", 110),
                cell(r.getCheckOutDate() != null ? r.getCheckOutDate().toString() : "—", 110),
                cell(formatStatus(r.getStatus()), 150)
            );
            reservationsTableBody.getChildren().add(row);
        }
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
    private void handleReservations(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminReservations.fxml");
    }

    @FXML
    private void handleBilling(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminBilling.fxml");
    }

    @FXML
    private void handleGuestFeedback(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminFeedback.fxml");
    }

    @FXML
    private void handleReports(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminReports.fxml");
    }

    @FXML
    private void handleLoyaltyProgram(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminLoyalty.fxml");
    }

    @FXML
    private void handleWaitlist(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminCheckout.fxml");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a guest name or reservation ID.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search");
            alert.setHeaderText(null);
            alert.setContentText("Searching for: " + searchText);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SceneNavigator.switchScene(event, "Welcome.fxml");
    }
}
