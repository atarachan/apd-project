package ca.senecacollege.malibuluminahotel.controller;
import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.repositories.GuestRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.ReservationRepositoryImpl;
import javafx.scene.control.Alert;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class GuestCheckoutController {

    @FXML
    private void handleProceedToPayment(ActionEvent event) {

        try {

            BookingSession session =
                    BookingSession.getInstance();

            Guest guest = new Guest(
                    session.getGuestFirstName(),
                    session.getGuestLastName(),
                    session.getGuestEmail(),
                    session.getGuestPhone()
            );

            GuestRepositoryImpl guestRepository =
                    new GuestRepositoryImpl();

            guestRepository.save(guest);

            Reservation reservation =
                    new Reservation(
                            guest,
                            session.getCheckInDate(),
                            session.getCheckOutDate()
                    );

            ReservationRepositoryImpl reservationRepository =
                    new ReservationRepositoryImpl();

            reservationRepository.save(
                    reservation
            );

            session.setSavedReservationId(
                    reservation.getReservationId()
            );

            SceneNavigator.switchScene(
                    event,
                    "Confirmation.fxml"
            );

        } catch (Exception exception) {

            System.out.println("ERROR OCCURRED:");
            System.out.println(exception.getMessage());

            exception.printStackTrace();

            Alert alert =
                    new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Reservation could not be saved."
            );

            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "Welcome.fxml"
        );
    }
}