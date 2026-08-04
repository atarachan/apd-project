package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;
import ca.senecacollege.malibuluminahotel.repositories.IRoomRepository;
import ca.senecacollege.malibuluminahotel.repositories.RoomRepositoryImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class RoomSelectionController {

    @FXML private Label recommendationLabel;

    @FXML private ImageView singleRoomImage;
    @FXML private ImageView doubleRoomImage;
    @FXML private ImageView penthouseImage;
    @FXML private Button singleSelectButton;
    @FXML private Button doubleSelectButton;
    @FXML private Button penthouseSelectButton;

    private final IRoomRepository roomRepository = new RoomRepositoryImpl();

    @FXML
    public void initialize() {
        roundImage(singleRoomImage);
        roundImage(doubleRoomImage);
        roundImage(penthouseImage);
        updateRecommendationText();
        updateRoomAvailabilityButtons();
    }

    private void roundImage(ImageView imageView) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(imageView.fitWidthProperty());
        clip.heightProperty().bind(imageView.fitHeightProperty());
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        imageView.setClip(clip);
    }

    private void updateRecommendationText() {
        BookingSession session = BookingSession.getInstance();
        int totalGuests = getTotalGuests(session);
        int selectedRooms = getSelectedRoomCount(session);
        int completedRooms = getCompletedRoomCount(session);
        int guestsPerRemainingRoom = getGuestsPerRemainingRoom(session);

        String recommendedRoom = guestsPerRemainingRoom <= 2 ? "Single Room" : "Double Room";
        if (guestsPerRemainingRoom > 4) {
            recommendedRoom = "Double Room";
        }

        String message = "Room " + (completedRooms + 1) + " of " + selectedRooms
                + ": Suggested " + recommendedRoom + " based on "
                + totalGuests + " guest(s) across " + selectedRooms + " room(s).";

        if (totalGuests >= 3 && totalGuests <= 4) {
            message += " You can choose one Double Room or two Single Rooms.";
        } else if (totalGuests > 4) {
            message += " Larger groups should use multiple Double Rooms, or a mix of Double and Single Rooms, until capacity is covered.";
        }

        message += " You may still choose any room type and quantity.";
        recommendationLabel.setText(message);
    }

    private int estimateCapacityAlreadySelected(BookingSession session) {
        int capacity = 0;

        for (BookingSession.ReservationItemSelection selection : session.getReservationItemSelections()) {
            capacity += switch (selection.roomTypeName()) {
                case SINGLE, PENTHOUSE -> 2;
                case DOUBLE -> 4;
            };
        }

        return capacity;
    }

    private int getGuestsPerRemainingRoom(BookingSession session) {
        int remainingRooms = Math.max(1, getSelectedRoomCount(session) - getCompletedRoomCount(session));
        int remainingGuests = Math.max(0, getTotalGuests(session) - estimateCapacityAlreadySelected(session));
        return (int) Math.ceil((double) remainingGuests / remainingRooms);
    }

    private int getTotalGuests(BookingSession session) {
        return session.getAdults() + session.getChildren();
    }

    private int getSelectedRoomCount(BookingSession session) {
        return Math.max(1, session.getRoomCount());
    }

    private int getCompletedRoomCount(BookingSession session) {
        return session.getReservationItemSelectionCount();
    }

    private void updateRoomAvailabilityButtons() {
        updateRoomAvailabilityButton(singleSelectButton, RoomTypeName.SINGLE);
        updateRoomAvailabilityButton(doubleSelectButton, RoomTypeName.DOUBLE);
        updateRoomAvailabilityButton(penthouseSelectButton, RoomTypeName.PENTHOUSE);
    }

    private void updateRoomAvailabilityButton(Button button, RoomTypeName roomTypeName) {
        BookingSession session = BookingSession.getInstance();

        boolean available = session.getCheckInDate() != null
                && session.getCheckOutDate() != null
                && availableRoomCount(roomTypeName, session) > selectedRoomCount(roomTypeName, session);

        button.setDisable(!available);
        button.setText(available ? "Select" : "Unavailable");
    }

    private int availableRoomCount(RoomTypeName roomTypeName, BookingSession session) {
        return roomRepository.findAvailable(
                roomTypeName,
                session.getCheckInDate(),
                session.getCheckOutDate(),
                Integer.MAX_VALUE
        ).size();
    }

    private int selectedRoomCount(RoomTypeName roomTypeName, BookingSession session) {
        int count = 0;

        for (BookingSession.ReservationItemSelection selection : session.getReservationItemSelections()) {
            if (selection.roomTypeName() == roomTypeName) {
                count++;
            }
        }

        return count;
    }

    @FXML
    private void handleSelectSingle(ActionEvent event) {
        if (!checkOccupancy(2, "Single Room")) return;
        BookingSession.getInstance().setSelectedRoomTypeName(RoomTypeName.SINGLE);
        continueAfterRoomSelection(event);
    }

    @FXML
    private void handleSelectDouble(ActionEvent event) {
        if (!checkOccupancy(4, "Double Room")) return;
        BookingSession.getInstance().setSelectedRoomTypeName(RoomTypeName.DOUBLE);
        continueAfterRoomSelection(event);
    }

    @FXML
    private void handleSelectPenthouse(ActionEvent event) {
        if (!checkOccupancy(2, "Penthouse")) return;
        BookingSession.getInstance().setSelectedRoomTypeName(RoomTypeName.PENTHOUSE);
        continueAfterRoomSelection(event);
    }

    private void continueAfterRoomSelection(ActionEvent event) {
        BookingSession session = BookingSession.getInstance();
        if (session.getGuestEmail() == null || session.getGuestEmail().isBlank()) {
            SceneNavigator.switchScene(event, "ReservationDetails.fxml");
            return;
        }

        SceneNavigator.switchScene(event, "AddOns.fxml");
    }

    private boolean checkOccupancy(int maxGuests, String roomName) {
        BookingSession session = BookingSession.getInstance();
        int guestsForThisRoom = getGuestsPerRemainingRoom(session);
        if (guestsForThisRoom > maxGuests) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Occupancy Limit Exceeded");
            alert.setHeaderText(null);
            alert.setContentText(
                roomName + " fits up to " + maxGuests + " guests, but this selection needs capacity for about "
                + guestsForThisRoom + " guest(s).\n\n"
                + "Choose a room with higher capacity, or go back and select more rooms."
            );
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "GuestPreference.fxml");
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        if (BookingSession.getInstance().getSelectedRoomTypeName() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Room Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a room type before continuing.");
            alert.showAndWait();
            return;
        }
        continueAfterRoomSelection(event);
    }

    @FXML
    private void handleRules(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hotel Rules");
        alert.setHeaderText("Malibu Lumina Hotel Rules");
        alert.setContentText(
                "• Check-in begins at 3:00 PM\n\n" +
                "• Check-out before 11:00 AM\n\n" +
                "• Single Room: Maximum 2 Guests\n\n" +
                "• Double Room: Maximum 4 Guests\n\n" +
                "• Penthouse: Maximum 2 Guests\n\n" +
                "• Smoking is prohibited inside rooms\n\n" +
                "• Guests are responsible for damages"
        );
        alert.showAndWait();
    }
}
