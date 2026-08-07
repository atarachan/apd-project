package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.repositories.IBillRepository;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentStatus;
import ca.senecacollege.malibuluminahotel.repositories.IPaymentRepository;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import java.math.BigDecimal;
import java.util.List;

public class AdminBillingController {

    @FXML private VBox billsTableBody;

    private final IBillRepository billRepository;
    private final IPaymentRepository paymentRepository;

    @Inject
    public AdminBillingController(
            IBillRepository billRepository,
            IPaymentRepository paymentRepository) {

        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
    }

    @FXML
    public void initialize() {
        billsTableBody.getChildren().clear();

        List<Bill> bills = billRepository.findAllWithDetails();

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
            Button payButton = new Button("Add Payment");
            payButton.getStyleClass().add("primary-button");
            payButton.setPrefWidth(130);
            payButton.setPrefHeight(30);

            payButton.setOnAction(e -> handleAddPayment(b));

            row.getChildren().addAll(
                    cell(reservationId,          150),
                    cell(guestName,              200),
                    cell(fmt(b.getSubtotal()),   140),
                    cell(fmt(b.getTax()),        120),
                    cell(fmt(b.getTotal()),      140),
                    cell(fmt(b.getBalanceDue()), 130),
                    payButton
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

    private void handleAddPayment(Bill bill) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Payment");

        ButtonType saveButton =
                new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton,
                ButtonType.CANCEL
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        ComboBox<PaymentMethod> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll(PaymentMethod.values());
        methodCombo.getSelectionModel().selectFirst();

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(
                "Customer Payment",
                "Hotel Refund"
        );
        typeCombo.getSelectionModel().selectFirst();

        grid.add(new Label("Amount"), 0, 0);
        grid.add(amountField, 1, 0);

        grid.add(new Label("Payment Method"), 0, 1);
        grid.add(methodCombo, 1, 1);

        grid.add(new Label("Payment Type"), 0, 2);
        grid.add(typeCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != saveButton) {
            return;
        }

        try {

            BigDecimal amount =
                    new BigDecimal(amountField.getText());

            PaymentStatus status =
                    typeCombo.getValue().equals("Customer Payment")
                            ? PaymentStatus.COMPLETED
                            : PaymentStatus.REFUNDED;

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                showError(
                        "Invalid Amount",
                        "Payment amount must be greater than zero."
                );

                return;
            }

            if (status == PaymentStatus.COMPLETED
                    && amount.compareTo(bill.getBalanceDue()) > 0) {

                showError(
                        "Invalid Amount",
                        "Payment cannot exceed the remaining balance."
                );

                return;
            }

            Payment payment = new Payment(
                    bill,
                    amount,
                    methodCombo.getValue(),
                    status,
                    "TXN" + System.currentTimeMillis()
            );

            paymentRepository.save(payment);

            if (status == PaymentStatus.COMPLETED) {

                bill.setBalanceDue(
                        bill.getBalanceDue().subtract(amount)
                );

            } else {

                bill.setBalanceDue(
                        bill.getBalanceDue().add(amount)
                );
            }

            billRepository.update(bill);

            billsTableBody.getChildren().clear();

            initialize();

        } catch (Exception ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Error");

            alert.setHeaderText(null);

            alert.setContentText("Unable to save payment.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }

    private void showError(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}
