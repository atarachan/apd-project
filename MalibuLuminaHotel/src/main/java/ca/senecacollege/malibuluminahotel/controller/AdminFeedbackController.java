package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Feedback;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.services.IFeedbackService;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class AdminFeedbackController {

    @FXML
    private TableView<FeedbackTableRow> feedbackTable;
    @FXML
    private TableColumn<FeedbackTableRow, String> guestColumn;
    @FXML
    private TableColumn<FeedbackTableRow, String> roomTypeColumn;
    @FXML
    private TableColumn<FeedbackTableRow, Integer> ratingColumn;
    @FXML
    private TableColumn<FeedbackTableRow, String> commentColumn;
    @FXML
    private TableColumn<FeedbackTableRow, String> dateColumn;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private ComboBox<Integer> minRatingFilter;

    private final IFeedbackService feedbackService;

    @Inject
    public AdminFeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @FXML
    private void initialize() {
        // Initialize table columns if they exist
        if (feedbackTable != null && guestColumn != null) {
            guestColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getGuestName()));

            roomTypeColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getRoomType()));

            ratingColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getRating()));

            commentColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getComment()));

            dateColumn.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(cell.getValue().getDate()));
            feedbackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

            loadFeedback();
        }

        // Initialize filter if it exists
        if (minRatingFilter != null) {
            minRatingFilter.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        }

        // Update average rating
        updateAverageRating();
    }

    /**
     * Load all feedback into the table.
     */
    private void loadFeedback() {
        try {
            List<Feedback> feedbackList = feedbackService.getAllFeedback();
            ObservableList<FeedbackTableRow> rows = FXCollections.observableArrayList();

            for (Feedback feedback : feedbackList) {
                rows.add(new FeedbackTableRow(feedback));
            }

            if (feedbackTable != null) {
                feedbackTable.getItems().setAll(rows);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load feedback: " + e.getMessage());
        }
    }

    /**
     * Filter feedback by minimum rating.
     */
    @FXML
    private void handleFilterByRating(ActionEvent event) {
        Integer minRating = minRatingFilter.getValue();
        if (minRating == null) {
            loadFeedback();
            return;
        }

        try {
            List<Feedback> feedbackList = feedbackService.getFeedbackByMinRating(minRating);
            ObservableList<FeedbackTableRow> rows = FXCollections.observableArrayList();

            for (Feedback feedback : feedbackList) {
                rows.add(new FeedbackTableRow(feedback));
            }

            if (feedbackTable != null) {
                feedbackTable.setItems(rows);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to filter feedback: " + e.getMessage());
        }
    }

    /**
     * Update average rating label.
     */
    private void updateAverageRating() {
        try {
            double avgRating = feedbackService.getAverageRating();
            if (averageRatingLabel != null) {
                averageRatingLabel.setText(String.format("%.1f / 5.0", avgRating));
            }
        } catch (Exception e) {
            // Silently fail if label doesn't exist
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Table row wrapper for Feedback.
     */
    public static class FeedbackTableRow {
        private final String guestName;
        private final String roomType;
        private final int rating;
        private final String comment;
        private final String date;

        public FeedbackTableRow(Feedback feedback) {
            Guest guest = feedback.getGuest();
            this.guestName = guest.getFirstName() + " " + guest.getLastName();
            // Get room type from first reservation item if available
            this.roomType = feedback.getReservation().getReservationItems().isEmpty() ? "N/A"
                    : feedback.getReservation().getReservationItems().get(0).getRoom().getRoomType().getRoomTypeName()
                            .toString();
            this.rating = feedback.getRating();
            this.comment = feedback.getComment();
            this.date = feedback.getSubmittedDate().toString();
        }

        public String getGuestName() {
            return guestName;
        }

        public String getRoomType() {
            return roomType;
        }

        public int getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }

        public String getDate() {
            return date;
        }
    }
}
