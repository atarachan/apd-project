package ca.senecacollege.malibuluminahotel.app;

import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;

import java.time.LocalDate;

public class BookingSession {

    private static BookingSession instance;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private RoomTypeName selectedRoomTypeName;

    private String guestFirstName;
    private String guestLastName;
    private String guestEmail;
    private String guestPhone;

    private boolean breakfastSelected;
    private boolean wifiSelected;
    private boolean parkingSelected;
    private boolean spaSelected;

    private Long savedReservationId;

    private BookingSession() {}

    public static BookingSession getInstance() {
        if (instance == null) {
            instance = new BookingSession();
        }
        return instance;
    }

    public void reset() {
        checkInDate = null;
        checkOutDate = null;
        selectedRoomTypeName = null;
        guestFirstName = null;
        guestLastName = null;
        guestEmail = null;
        guestPhone = null;
        breakfastSelected = false;
        wifiSelected = false;
        parkingSelected = false;
        spaSelected = false;
        savedReservationId = null;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public RoomTypeName getSelectedRoomTypeName() {
        return selectedRoomTypeName;
    }

    public void setSelectedRoomTypeName(RoomTypeName selectedRoomTypeName) {
        this.selectedRoomTypeName = selectedRoomTypeName;
    }

    public String getGuestFirstName() {
        return guestFirstName;
    }

    public void setGuestFirstName(String guestFirstName) {
        this.guestFirstName = guestFirstName;
    }

    public String getGuestLastName() {
        return guestLastName;
    }

    public void setGuestLastName(String guestLastName) {
        this.guestLastName = guestLastName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public boolean isBreakfastSelected() {
        return breakfastSelected;
    }

    public void setBreakfastSelected(boolean breakfastSelected) {
        this.breakfastSelected = breakfastSelected;
    }

    public boolean isWifiSelected() {
        return wifiSelected;
    }

    public void setWifiSelected(boolean wifiSelected) {
        this.wifiSelected = wifiSelected;
    }

    public boolean isParkingSelected() {
        return parkingSelected;
    }

    public void setParkingSelected(boolean parkingSelected) {
        this.parkingSelected = parkingSelected;
    }

    public boolean isSpaSelected() {
        return spaSelected;
    }

    public void setSpaSelected(boolean spaSelected) {
        this.spaSelected = spaSelected;
    }

    public Long getSavedReservationId() {
        return savedReservationId;
    }

    public void setSavedReservationId(Long savedReservationId) {
        this.savedReservationId = savedReservationId;
    }
}
