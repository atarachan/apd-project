package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.ReservationItem;
import ca.senecacollege.malibuluminahotel.models.ReservationItemAddOn;
import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.PricingModel;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import ca.senecacollege.malibuluminahotel.repositories.IAddOnRepository;
import ca.senecacollege.malibuluminahotel.repositories.IGuestRepository;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository.ReservationEditDraft;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository.ReservationItemDraft;
import ca.senecacollege.malibuluminahotel.repositories.IRoomRepository;
import ca.senecacollege.malibuluminahotel.security.SessionManager;
import ca.senecacollege.malibuluminahotel.services.BillLineItem;
import ca.senecacollege.malibuluminahotel.services.BillSummary;
import ca.senecacollege.malibuluminahotel.services.IActivityLogService;
import ca.senecacollege.malibuluminahotel.services.PricingStrategy;
import ca.senecacollege.malibuluminahotel.services.StandardPricingStrategy;
import ca.senecacollege.malibuluminahotel.services.WeekendPricingStrategy;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class AdminReservationsController {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.13");

    @FXML
    private VBox reservationsTableBody;

    private final IReservationRepository reservationRepository;
    private final IGuestRepository guestRepository;
    private final IRoomRepository roomRepository;
    private final IAddOnRepository addOnRepository;
    private final IActivityLogService activityLogService;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.CANADA);

    @Inject
    public AdminReservationsController(IReservationRepository reservationRepository,
                                       IGuestRepository guestRepository,
                                       IRoomRepository roomRepository,
                                       IAddOnRepository addOnRepository,
                                       IActivityLogService activityLogService) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.addOnRepository = addOnRepository;
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

        for (Reservation reservation : reservations) {
            Guest guest = reservation.getGuest();
            HBox row = new HBox();
            row.getStyleClass().add("table-data-row");
            row.getChildren().addAll(
                    cell("R" + reservation.getReservationId(), 130),
                    cell(guest == null ? "--" : guest.getFirstName() + " " + guest.getLastName(), 180),
                    cell(String.valueOf(roomCount(reservation)), 140),
                    cell(formatDate(reservation.getCheckInDate()), 120),
                    cell(formatDate(reservation.getCheckOutDate()), 120),
                    cell(formatStatus(reservation.getStatus()), 160),
                    createActionButtons(reservation));
            reservationsTableBody.getChildren().add(row);
        }
    }

    private HBox createActionButtons(Reservation reservation) {
        Button modifyBtn = rowButton("Modify");
        modifyBtn.setOnAction(e -> handleModifyReservation(reservation));

        Button cancelBtn = rowButton("Cancel");
        cancelBtn.setOnAction(e -> handleCancelReservation(reservation));

        Button checkoutBtn = rowButton("Check Out");
        checkoutBtn.setOnAction(e -> handleCheckoutReservation(reservation));

        HBox actions = new HBox(8, modifyBtn, cancelBtn, checkoutBtn);
        actions.setPrefWidth(250);
        actions.setAlignment(Pos.CENTER_LEFT);
        return actions;
    }

    private Button rowButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("small-button");
        button.setPrefWidth(76);
        return button;
    }

    private int roomCount(Reservation reservation) {
        return reservation.getReservationItems() == null ? 0 : reservation.getReservationItems().size();
    }

    private Label cell(String text, double width) {
        Label label = new Label(text != null ? text : "--");
        label.setPrefWidth(width);
        label.getStyleClass().add("table-cell");
        return label;
    }

    private String formatDate(LocalDate date) {
        return date == null ? "--" : date.toString();
    }

    private String formatStatus(ReservationStatus status) {
        if (status == null) {
            return "Pending";
        }
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
                Reservation saved = reservationRepository.save(reservation);
                logReservationCreated(saved);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation created successfully!");
                loadReservations();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create reservation: " + e.getMessage());
            }
        });
    }

    private void handleModifyReservation(Reservation reservation) {
        Reservation fullReservation = reservationRepository.findByIdWithDetails(reservation.getReservationId())
                .orElseThrow(() -> new IllegalStateException("Reservation not found."));

        ReservationDraft draft = ReservationDraft.from(fullReservation);
        Dialog<ReservationEditDraft> dialog = createModifyDialog(fullReservation, draft);
        Optional<ReservationEditDraft> result = dialog.showAndWait();

        result.ifPresent(updateDraft -> {
            try {
                Reservation saved = reservationRepository.updateReservationDetails(updateDraft);
                logReservationUpdated(saved);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation updated successfully!");
                loadReservations();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update reservation: " + e.getMessage());
            }
        });
    }

    private void handleCancelReservation(Reservation reservation) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Reservation #" + reservation.getReservationId());
        confirmAlert.setContentText("Are you sure you want to cancel this reservation?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatus(ReservationStatus.CANCELLED);
                Reservation saved = reservationRepository.update(reservation);
                logReservationCancelled(saved);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation cancelled successfully!");
                loadReservations();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel reservation: " + e.getMessage());
            }
        }
    }

    private void handleCheckoutReservation(Reservation reservation) {
        Reservation fullReservation = reservationRepository.findByIdWithDetails(reservation.getReservationId())
                .orElseThrow(() -> new IllegalStateException("Reservation not found."));

        if (fullReservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            showAlert(Alert.AlertType.INFORMATION, "Checkout", "This reservation is already checked out.");
            return;
        }
        if (fullReservation.getStatus() == ReservationStatus.CANCELLED) {
            showAlert(Alert.AlertType.ERROR, "Checkout", "Cancelled reservations cannot be checked out.");
            return;
        }

        CheckoutDraft checkoutDraft = createCheckoutDraft(fullReservation);
        Dialog<CheckoutPayment> dialog = createCheckoutDialog(fullReservation, checkoutDraft);
        Optional<CheckoutPayment> result = dialog.showAndWait();

        result.ifPresent(payment -> {
            try {
                reservationRepository.checkoutReservation(
                        fullReservation.getReservationId(),
                        payment.paymentMethod(),
                        payment.amount());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation checked out successfully!");
                loadReservations();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Checkout Failed", e.getMessage());
            }
        });
    }

    private Dialog<ReservationEditDraft> createModifyDialog(Reservation reservation, ReservationDraft draft) {
        Dialog<ReservationEditDraft> dialog = new Dialog<>();
        dialog.setTitle("Modify Reservation");
        dialog.setHeaderText("Reservation #" + reservation.getReservationId() + " details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(850);

        VBox content = new VBox(14);
        content.setPadding(new Insets(12));

        Guest guest = reservation.getGuest();
        Label guestLabel = new Label("Guest: " + (guest == null ? "--" : guest.getFirstName() + " " + guest.getLastName()));
        guestLabel.getStyleClass().add("field-label");

        DatePicker checkInPicker = new DatePicker(draft.checkIn);
        DatePicker checkOutPicker = new DatePicker(draft.checkOut);
        Spinner<Integer> adultsSpinner = new Spinner<>(1, 10, draft.adults);
        Spinner<Integer> childrenSpinner = new Spinner<>(0, 10, draft.children);
        ComboBox<ReservationStatus> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList(ReservationStatus.values()));
        statusCombo.setValue(draft.status);

        GridPane basicsGrid = new GridPane();
        basicsGrid.setHgap(12);
        basicsGrid.setVgap(10);
        basicsGrid.add(new Label("Check-in:"), 0, 0);
        basicsGrid.add(checkInPicker, 1, 0);
        basicsGrid.add(new Label("Check-out:"), 2, 0);
        basicsGrid.add(checkOutPicker, 3, 0);
        basicsGrid.add(new Label("Adults:"), 0, 1);
        basicsGrid.add(adultsSpinner, 1, 1);
        basicsGrid.add(new Label("Children:"), 2, 1);
        basicsGrid.add(childrenSpinner, 3, 1);
        basicsGrid.add(new Label("Status:"), 0, 2);
        basicsGrid.add(statusCombo, 1, 2);

        ListView<ReservationItemDraftModel> roomsList = new ListView<>(
                FXCollections.observableArrayList(draft.items));
        roomsList.setPrefHeight(170);
        roomsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ReservationItemDraftModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.displayName());
            }
        });

        VBox selectedRoomBox = new VBox(8);
        selectedRoomBox.setPadding(new Insets(8, 0, 0, 0));

        Runnable refreshSelectedRoom = () -> renderSelectedRoomControls(
                selectedRoomBox,
                roomsList,
                checkInPicker,
                checkOutPicker);
        roomsList.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) ->
                refreshSelectedRoom.run());
        refreshSelectedRoom.run();

        Button addRoomButton = new Button("Add Room");
        addRoomButton.getStyleClass().add("secondary-button");
        addRoomButton.setOnAction(e -> chooseRoom(checkInPicker.getValue(), checkOutPicker.getValue(), null)
                .ifPresent(room -> {
                    roomsList.getItems().add(ReservationItemDraftModel.fromRoom(room));
                    roomsList.getSelectionModel().selectLast();
                    refreshSelectedRoom.run();
                }));

        VBox roomsSection = new VBox(8,
                new Label("Rooms"),
                roomsList,
                selectedRoomBox,
                addRoomButton);

        content.getChildren().addAll(guestLabel, basicsGrid, roomsSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(560);
        dialog.getDialogPane().setContent(scrollPane);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            String validationError = validateModifyInputs(
                    checkInPicker.getValue(),
                    checkOutPicker.getValue(),
                    roomsList.getItems());
            if (validationError != null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", validationError);
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton != saveButtonType) {
                return null;
            }

            LocalDate checkIn = checkInPicker.getValue();
            LocalDate checkOut = checkOutPicker.getValue();
            List<ReservationItemDraft> itemDrafts = roomsList.getItems().stream()
                    .map(ReservationItemDraftModel::toRepositoryDraft)
                    .toList();
            BillSummary summary = calculateBill(
                    checkIn,
                    checkOut,
                    new ArrayList<>(roomsList.getItems()));

            return new ReservationEditDraft(
                    reservation.getReservationId(),
                    checkIn,
                    checkOut,
                    adultsSpinner.getValue(),
                    childrenSpinner.getValue(),
                    statusCombo.getValue(),
                    itemDrafts,
                    summary.subtotal(),
                    summary.tax(),
                    summary.total());
        });

        return dialog;
    }

    private void renderSelectedRoomControls(VBox container,
                                            ListView<ReservationItemDraftModel> roomsList,
                                            DatePicker checkInPicker,
                                            DatePicker checkOutPicker) {
        container.getChildren().clear();
        ReservationItemDraftModel selected = roomsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Label helper = new Label("No room selected. Use Add Room to attach a room to this reservation.");
            helper.getStyleClass().add("helper-label");
            container.getChildren().add(helper);
            return;
        }

        Label roomDetails = new Label(selected.detailText());
        roomDetails.setWrapText(true);
        roomDetails.getStyleClass().add("helper-label");

        Button modifyRoom = new Button("Modify Room");
        modifyRoom.setOnAction(e -> chooseRoom(checkInPicker.getValue(), checkOutPicker.getValue(), selected)
                .ifPresent(room -> {
                    selected.applyRoom(room);
                    roomsList.refresh();
                    renderSelectedRoomControls(container, roomsList, checkInPicker, checkOutPicker);
                }));

        Button removeRoom = new Button("Remove Room");
        removeRoom.setOnAction(e -> {
            roomsList.getItems().remove(selected);
            renderSelectedRoomControls(container, roomsList, checkInPicker, checkOutPicker);
        });

        Button addAddOn = new Button("Add Add-on");
        addAddOn.setOnAction(e -> chooseAddOn(selected, checkInPicker.getValue(), checkOutPicker.getValue())
                .ifPresent(addOn -> {
                    int quantity = addOnQuantity(addOn, checkInPicker.getValue(), checkOutPicker.getValue());
                    selected.addOnQuantities.put(addOn.getAddOnId(), quantity);
                    selected.addOnNames.put(addOn.getAddOnId(), addOn.getName());
                    selected.addOnPrices.put(addOn.getAddOnId(), addOn.getPrice());
                    selected.addOnPricingModels.put(addOn.getAddOnId(), addOn.getPricingModel());
                    roomsList.refresh();
                    renderSelectedRoomControls(container, roomsList, checkInPicker, checkOutPicker);
                }));

        Button removeAddOn = new Button("Remove Add-on");
        removeAddOn.setDisable(selected.addOnQuantities.isEmpty());
        removeAddOn.setOnAction(e -> chooseAddOnToRemove(selected)
                .ifPresent(addOnId -> {
                    selected.removeAddOn(addOnId);
                    roomsList.refresh();
                    renderSelectedRoomControls(container, roomsList, checkInPicker, checkOutPicker);
                }));

        HBox buttons = new HBox(8, modifyRoom, removeRoom, addAddOn, removeAddOn);
        container.getChildren().addAll(roomDetails, buttons);
    }

    private Optional<Room> chooseRoom(LocalDate checkIn, LocalDate checkOut, ReservationItemDraftModel current) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            showAlert(Alert.AlertType.ERROR, "Dates Required", "Select valid check-in and check-out dates first.");
            return Optional.empty();
        }

        List<Room> rooms = roomRepository.findAll().stream()
                .sorted((left, right) -> left.getRoomNumber().compareToIgnoreCase(right.getRoomNumber()))
                .toList();
        return chooseValue(
                current == null ? "Add Room" : "Modify Room",
                "Choose a room",
                "Room:",
                rooms,
                current == null ? null : current.room,
                room -> room == null ? "" : formatRoom(room));
    }

    private Optional<AddOn> chooseAddOn(ReservationItemDraftModel selected, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            showAlert(Alert.AlertType.ERROR, "Dates Required", "Select valid check-in and check-out dates first.");
            return Optional.empty();
        }

        List<AddOn> choices = addOnRepository.findAll().stream()
                .filter(addOn -> !selected.addOnQuantities.containsKey(addOn.getAddOnId()))
                .toList();
        if (choices.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Add-ons", "All available add-ons are already selected.");
            return Optional.empty();
        }

        return chooseValue(
                "Add Add-on",
                "Choose an add-on",
                "Add-on:",
                choices,
                choices.get(0),
                addOn -> addOn == null ? "" : addOn.getName() + " - " + formatCurrency(addOn.getPrice()));
    }

    private Optional<Long> chooseAddOnToRemove(ReservationItemDraftModel selected) {
        List<Long> addOnIds = new ArrayList<>(selected.addOnQuantities.keySet());
        return chooseValue(
                "Remove Add-on",
                "Choose an add-on to remove",
                "Add-on:",
                addOnIds,
                addOnIds.get(0),
                addOnId -> selected.addOnNames.getOrDefault(addOnId, "Add-on #" + addOnId));
    }

    private <T> Optional<T> chooseValue(String title,
                                        String header,
                                        String label,
                                        List<T> choices,
                                        T initial,
                                        ValueFormatter<T> formatter) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableArrayList(choices));
        comboBox.setPrefWidth(420);
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T value) {
                return value == null ? "" : formatter.format(value);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
        comboBox.setValue(initial == null && !choices.isEmpty() ? choices.get(0) : initial);

        HBox content = new HBox(10, new Label(label), comboBox);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(12));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(520);
        dialog.setResultConverter(button -> button == selectButtonType ? comboBox.getValue() : null);
        return dialog.showAndWait();
    }

    private String validateModifyInputs(LocalDate checkIn,
                                        LocalDate checkOut,
                                        List<ReservationItemDraftModel> items) {
        if (checkIn == null || checkOut == null) {
            return "Check-in and check-out dates are required.";
        }
        if (!checkOut.isAfter(checkIn)) {
            return "Check-out date must be after check-in date.";
        }
        if (items.isEmpty()) {
            return "Add at least one room to the reservation.";
        }
        return null;
    }

    private Dialog<CheckoutPayment> createCheckoutDialog(Reservation reservation, CheckoutDraft checkoutDraft) {
        Dialog<CheckoutPayment> dialog = new Dialog<>();
        dialog.setTitle("Checkout Reservation");
        dialog.setHeaderText("Reservation #" + reservation.getReservationId() + " checkout");

        ButtonType checkoutButtonType = new ButtonType("Checkout", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(checkoutButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(760);

        VBox content = new VBox(14);
        content.setPadding(new Insets(12));
        content.getChildren().add(buildBillView(checkoutDraft.summary()));

        VBox totals = new VBox(8,
                amountRow("Total:", checkoutDraft.summary().total()),
                amountRow("Payments already made:", checkoutDraft.paymentsMade()),
                amountRow("Required payment:", checkoutDraft.requiredPayment()));
        content.getChildren().add(totals);

        ComboBox<PaymentMethod> paymentMethodCombo = new ComboBox<>(
                FXCollections.observableArrayList(PaymentMethod.values()));
        paymentMethodCombo.setValue(PaymentMethod.CREDIT_CARD);

        TextField paymentAmountField = new TextField(checkoutDraft.requiredPayment().toPlainString());
        paymentAmountField.getStyleClass().add("input-field");
        TextField cardholderNameField = new TextField();
        cardholderNameField.setPromptText("Name on card");
        cardholderNameField.getStyleClass().add("input-field");
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Card number");
        cardNumberField.getStyleClass().add("input-field");
        TextField expiryDateField = new TextField();
        expiryDateField.setPromptText("MM/YY");
        expiryDateField.getStyleClass().add("input-field");
        PasswordField cvvField = new PasswordField();
        cvvField.setPromptText("CVV");
        cvvField.getStyleClass().add("input-field");

        GridPane paymentGrid = new GridPane();
        paymentGrid.setHgap(12);
        paymentGrid.setVgap(10);
        paymentGrid.add(new Label("Payment Method:"), 0, 0);
        paymentGrid.add(paymentMethodCombo, 1, 0);
        paymentGrid.add(new Label("Payment Amount:"), 0, 1);
        paymentGrid.add(paymentAmountField, 1, 1);
        paymentGrid.add(new Label("Cardholder Name:"), 0, 2);
        paymentGrid.add(cardholderNameField, 1, 2);
        paymentGrid.add(new Label("Card Number:"), 0, 3);
        paymentGrid.add(cardNumberField, 1, 3);
        paymentGrid.add(new Label("Expiry Date:"), 0, 4);
        paymentGrid.add(expiryDateField, 1, 4);
        paymentGrid.add(new Label("CVV:"), 0, 5);
        paymentGrid.add(cvvField, 1, 5);
        content.getChildren().add(paymentGrid);

        dialog.getDialogPane().setContent(content);

        Node checkoutButton = dialog.getDialogPane().lookupButton(checkoutButtonType);
        checkoutButton.addEventFilter(ActionEvent.ACTION, event -> {
            BigDecimal enteredAmount = parseAmount(paymentAmountField.getText()).orElse(null);
            if (enteredAmount == null) {
                showAlert(Alert.AlertType.ERROR, "Payment Error", "Enter a valid payment amount.");
                event.consume();
                return;
            }
            if (!hasText(cardholderNameField)
                    || !hasText(cardNumberField)
                    || !hasText(expiryDateField)
                    || !hasText(cvvField)) {
                showAlert(Alert.AlertType.ERROR, "Payment Error", "Enter all card payment details.");
                event.consume();
                return;
            }
            if (enteredAmount.compareTo(checkoutDraft.requiredPayment()) != 0) {
                showAlert(Alert.AlertType.ERROR, "Payment Error",
                        "Payment must equal the required amount: "
                                + formatCurrency(checkoutDraft.requiredPayment()));
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton != checkoutButtonType) {
                return null;
            }
            return new CheckoutPayment(
                    paymentMethodCombo.getValue(),
                    parseAmount(paymentAmountField.getText()).orElse(BigDecimal.ZERO));
        });

        return dialog;
    }

    private boolean hasText(TextField field) {
        return field.getText() != null && !field.getText().trim().isEmpty();
    }

    private VBox buildBillView(BillSummary summary) {
        VBox billBox = new VBox(8);
        Label title = new Label("Detailed Bill");
        title.getStyleClass().add("section-label");
        billBox.getChildren().add(title);

        VBox lineItems = new VBox(4);
        for (BillLineItem lineItem : summary.lineItems()) {
            HBox row = new HBox(12);
            VBox descriptionBox = new VBox(2);
            descriptionBox.setPrefWidth(500);
            HBox.setHgrow(descriptionBox, Priority.ALWAYS);

            Label description = new Label(lineItem.description());
            description.setWrapText(true);
            description.getStyleClass().add("field-label");

            Label calculation = new Label(lineItem.calculation());
            calculation.setWrapText(true);
            calculation.getStyleClass().add("helper-label");

            Label amount = new Label(formatCurrency(lineItem.amount()));
            amount.setPrefWidth(140);
            amount.getStyleClass().add("field-label");

            descriptionBox.getChildren().addAll(description, calculation);
            row.getChildren().addAll(descriptionBox, amount);
            lineItems.getChildren().add(row);
        }

        ScrollPane linesScroll = new ScrollPane(lineItems);
        linesScroll.setFitToWidth(true);
        linesScroll.setPrefHeight(260);
        billBox.getChildren().addAll(
                linesScroll,
                amountRow("Room charges:", summary.roomTotal()),
                amountRow("Add-ons:", summary.addOnTotal()),
                amountRow("Subtotal:", summary.subtotal()),
                amountRow("Taxes (13%):", summary.tax()));
        return billBox;
    }

    private HBox amountRow(String label, BigDecimal amount) {
        Label labelNode = new Label(label);
        labelNode.setPrefWidth(560);
        labelNode.getStyleClass().add("field-label");
        Label amountNode = new Label(formatCurrency(amount));
        amountNode.getStyleClass().add("field-label");
        return new HBox(10, labelNode, amountNode);
    }

    private CheckoutDraft createCheckoutDraft(Reservation reservation) {
        BillSummary summary = calculateBill(
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getReservationItems().stream()
                        .map(ReservationItemDraftModel::fromItem)
                        .toList());
        BigDecimal paymentsMade = sumPayments(reservation.getBill());
        BigDecimal requiredPayment = summary.total().subtract(paymentsMade).max(BigDecimal.ZERO);
        return new CheckoutDraft(summary, paymentsMade, requiredPayment);
    }

    private BillSummary calculateBill(LocalDate checkIn,
                                      LocalDate checkOut,
                                      List<ReservationItemDraftModel> items) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal roomTotal = BigDecimal.ZERO;
        BigDecimal addOnTotal = BigDecimal.ZERO;
        List<BillLineItem> lineItems = new ArrayList<>();
        int roomNumber = 1;

        for (ReservationItemDraftModel item : items) {
            BigDecimal itemRoomTotal = calculateRoomTotal(item.roomType, checkIn, checkOut);
            BigDecimal averageNightlyRate = itemRoomTotal.divide(
                    BigDecimal.valueOf(nights),
                    2,
                    RoundingMode.HALF_UP);

            lineItems.add(new BillLineItem(
                    "Room " + roomNumber + " - " + formatRoom(item.room),
                    "CAD " + averageNightlyRate + " x " + nights + " night(s)",
                    itemRoomTotal));

            roomTotal = roomTotal.add(itemRoomTotal);
            for (Long addOnId : item.addOnQuantities.keySet()) {
                String name = item.addOnNames.get(addOnId);
                BigDecimal price = item.addOnPrices.get(addOnId);
                int quantity = item.addOnQuantities.get(addOnId);
                PricingModel pricingModel = item.addOnPricingModels.get(addOnId);
                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(quantity));
                String unit = pricingModel == PricingModel.PER_NIGHT ? "night(s)" : "room";

                lineItems.add(new BillLineItem(
                        "  Add-on - " + name,
                        "CAD " + price + " x " + quantity + " " + unit,
                        lineTotal));
                addOnTotal = addOnTotal.add(lineTotal);
            }
            roomNumber++;
        }

        BigDecimal subtotal = roomTotal.add(addOnTotal);
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);
        return new BillSummary(roomTotal, addOnTotal, subtotal, tax, total, nights, lineItems);
    }

    private BigDecimal calculateRoomTotal(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        BigDecimal roomTotal = BigDecimal.ZERO;
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            PricingStrategy strategy = isWeekend(date)
                    ? new WeekendPricingStrategy()
                    : new StandardPricingStrategy();
            roomTotal = roomTotal.add(strategy.calculateNightlyRate(roomType, date));
        }
        return roomTotal;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private int addOnQuantity(AddOn addOn, LocalDate checkIn, LocalDate checkOut) {
        if (addOn.getPricingModel() == PricingModel.PER_NIGHT) {
            return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        }
        return 1;
    }

    private BigDecimal sumPayments(Bill bill) {
        if (bill == null || bill.getPayments() == null) {
            return BigDecimal.ZERO;
        }
        return bill.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Optional<BigDecimal> parseAmount(String text) {
        try {
            if (text == null || text.trim().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new BigDecimal(text.trim()).setScale(2, RoundingMode.HALF_UP));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private String formatCurrency(BigDecimal amount) {
        return currencyFormat.format(amount == null ? BigDecimal.ZERO : amount);
    }

    private String formatRoom(Room room) {
        if (room == null) {
            return "--";
        }
        RoomType type = room.getRoomType();
        String typeName = type == null ? "Room" : formatRoomType(type);
        return "Room " + room.getRoomNumber() + " - " + typeName;
    }

    private String formatRoomType(RoomType roomType) {
        if (roomType == null || roomType.getRoomTypeName() == null) {
            return "Room";
        }
        return switch (roomType.getRoomTypeName()) {
            case SINGLE -> "Single Room";
            case DOUBLE -> "Double Room";
            case PENTHOUSE -> "Penthouse";
        };
    }

    private void logReservationCreated(Reservation reservation) {
        AdminUser admin = SessionManager.getInstance().getCurrentUser();
        if (admin != null) {
            activityLogService.logReservationCreated(admin, reservation);
        }
    }

    private void logReservationUpdated(Reservation reservation) {
        AdminUser admin = SessionManager.getInstance().getCurrentUser();
        if (admin != null) {
            activityLogService.logReservationUpdated(admin, reservation);
        }
    }

    private void logReservationCancelled(Reservation reservation) {
        AdminUser admin = SessionManager.getInstance().getCurrentUser();
        if (admin != null) {
            activityLogService.logReservationCancelled(admin, reservation);
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

        ComboBox<Guest> guestCombo = new ComboBox<>();
        guestCombo.setItems(FXCollections.observableArrayList(guestRepository.findAll()));
        guestCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Guest guest) {
                return guest == null ? "" : guest.getFirstName() + " " + guest.getLastName();
            }

            @Override
            public Guest fromString(String string) {
                return null;
            }
        });

        DatePicker checkInPicker = new DatePicker(LocalDate.now());
        DatePicker checkOutPicker = new DatePicker(LocalDate.now().plusDays(1));
        Spinner<Integer> adultsSpinner = new Spinner<>(1, 10, 1);
        Spinner<Integer> childrenSpinner = new Spinner<>(0, 10, 0);
        ComboBox<ReservationStatus> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList(ReservationStatus.values()));
        statusCombo.setValue(ReservationStatus.PENDING);

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
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton != saveButtonType) {
                return null;
            }
            if (guestCombo.getValue() == null
                    || checkInPicker.getValue() == null
                    || checkOutPicker.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
                return null;
            }
            if (!checkOutPicker.getValue().isAfter(checkInPicker.getValue())) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Check-out date must be after check-in date.");
                return null;
            }

            Reservation reservation = new Reservation();
            reservation.setGuest(guestCombo.getValue());
            reservation.setCheckInDate(checkInPicker.getValue());
            reservation.setCheckOutDate(checkOutPicker.getValue());
            reservation.setAdults(adultsSpinner.getValue());
            reservation.setChildren(childrenSpinner.getValue());
            reservation.setStatus(statusCombo.getValue());
            return reservation;
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

    private record CheckoutPayment(PaymentMethod paymentMethod, BigDecimal amount) {
    }

    private record CheckoutDraft(BillSummary summary, BigDecimal paymentsMade, BigDecimal requiredPayment) {
    }

    private interface ValueFormatter<T> {
        String format(T value);
    }

    private static class ReservationDraft {
        private final LocalDate checkIn;
        private final LocalDate checkOut;
        private final int adults;
        private final int children;
        private final ReservationStatus status;
        private final List<ReservationItemDraftModel> items;

        private ReservationDraft(LocalDate checkIn,
                                 LocalDate checkOut,
                                 int adults,
                                 int children,
                                 ReservationStatus status,
                                 List<ReservationItemDraftModel> items) {
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.adults = adults;
            this.children = children;
            this.status = status;
            this.items = items;
        }

        private static ReservationDraft from(Reservation reservation) {
            return new ReservationDraft(
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate(),
                    reservation.getAdults(),
                    reservation.getChildren(),
                    reservation.getStatus(),
                    reservation.getReservationItems().stream()
                            .map(ReservationItemDraftModel::fromItem)
                            .toList());
        }
    }

    private static class ReservationItemDraftModel {
        private Room room;
        private Long roomId;
        private RoomType roomType;
        private BigDecimal nightlyRate;
        private final Map<Long, Integer> addOnQuantities = new LinkedHashMap<>();
        private final Map<Long, String> addOnNames = new LinkedHashMap<>();
        private final Map<Long, BigDecimal> addOnPrices = new LinkedHashMap<>();
        private final Map<Long, PricingModel> addOnPricingModels = new LinkedHashMap<>();

        private static ReservationItemDraftModel fromRoom(Room room) {
            ReservationItemDraftModel model = new ReservationItemDraftModel();
            model.applyRoom(room);
            return model;
        }

        private static ReservationItemDraftModel fromItem(ReservationItem item) {
            ReservationItemDraftModel model = fromRoom(item.getRoom());
            model.nightlyRate = item.getNightlyRate();
            for (ReservationItemAddOn line : item.getAddOns()) {
                AddOn addOn = line.getAddOn();
                model.addOnQuantities.put(addOn.getAddOnId(), line.getQuantity());
                model.addOnNames.put(addOn.getAddOnId(), addOn.getName());
                model.addOnPrices.put(addOn.getAddOnId(), addOn.getPrice());
                model.addOnPricingModels.put(addOn.getAddOnId(), addOn.getPricingModel());
            }
            return model;
        }

        private void applyRoom(Room room) {
            this.room = room;
            this.roomId = room.getRoomId();
            this.roomType = room.getRoomType();
            this.nightlyRate = roomType == null ? BigDecimal.ZERO : roomType.getBaseRate();
        }

        private void removeAddOn(Long addOnId) {
            addOnQuantities.remove(addOnId);
            addOnNames.remove(addOnId);
            addOnPrices.remove(addOnId);
            addOnPricingModels.remove(addOnId);
        }

        private ReservationItemDraft toRepositoryDraft() {
            return new ReservationItemDraft(roomId, nightlyRate, new LinkedHashMap<>(addOnQuantities));
        }

        private String displayName() {
            return room.getRoomNumber() + " - "
                    + (roomType == null ? "Room" : roomType.getRoomTypeName())
                    + " (" + addOnQuantities.size() + " add-on(s))";
        }

        private String detailText() {
            String addOns = addOnNames.isEmpty()
                    ? "No add-ons selected"
                    : String.join(", ", addOnNames.values());
            return "Selected: " + displayName() + "\nAdd-ons: " + addOns;
        }
    }
}
