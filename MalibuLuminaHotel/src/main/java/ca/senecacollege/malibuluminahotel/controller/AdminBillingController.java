package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.repositories.BillRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.IBillRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class AdminBillingController {

    @FXML private VBox billsTableBody;

    @FXML
    public void initialize() {
        IBillRepository repo = new BillRepositoryImpl();
        List<Bill> bills = repo.findAllWithDetails();

        if (bills.isEmpty()) {
            Label empty = new Label("No billing records found.");
            empty.getStyleClass().add("helper-label");
            billsTableBody.getChildren().add(empty);
            return;
        }

        for (Bill b : bills) {
            Reservation r = b.getReservation();
            Guest g = r != null ? r.getGuest() : null;

            String reservationId = r != null ? "R" + r.getReservationId() : "—";
            String guestName = g != null ? g.getFirstName() + " " + g.getLastName() : "—";

            HBox row = new HBox();
            row.getStyleClass().add("table-data-row");
            row.getChildren().addAll(
                cell(reservationId,          150),
                cell(guestName,              200),
                cell(fmt(b.getSubtotal()),   140),
                cell(fmt(b.getTax()),        120),
                cell(fmt(b.getTotal()),      140),
                cell(fmt(b.getBalanceDue()), 130)
            );
            billsTableBody.getChildren().add(row);
        }
    }

    private Label cell(String text, double width) {
        Label l = new Label(text != null ? text : "—");
        l.setPrefWidth(width);
        l.getStyleClass().add("table-cell");
        return l;
    }

    private String fmt(BigDecimal value) {
        if (value == null) return "CAD 0.00";
        return "CAD " + String.format("%,.2f", value);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }
}
