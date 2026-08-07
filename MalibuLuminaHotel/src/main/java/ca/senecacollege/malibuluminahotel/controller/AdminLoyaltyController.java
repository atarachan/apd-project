package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;
import ca.senecacollege.malibuluminahotel.repositories.ILoyaltyAccountRepository;
import ca.senecacollege.malibuluminahotel.repositories.IGuestRepository;
import ca.senecacollege.malibuluminahotel.models.Guest;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for admin loyalty program management.
 */
public class AdminLoyaltyController {

    private static final Logger LOGGER = Logger.getLogger(AdminLoyaltyController.class.getName());

    @FXML
    private TableView<LoyaltyAccountDisplay> loyaltyTable;
    @FXML
    private TableColumn<LoyaltyAccountDisplay, String> memberNumberColumn;
    @FXML
    private TableColumn<LoyaltyAccountDisplay, String> guestNameColumn;
    @FXML
    private TableColumn<LoyaltyAccountDisplay, String> emailColumn;
    @FXML
    private TableColumn<LoyaltyAccountDisplay, Integer> pointsColumn;
    @FXML
    private TableColumn<LoyaltyAccountDisplay, String> valueColumn;

    private final ILoyaltyAccountRepository loyaltyRepository;
    private final IGuestRepository guestRepository;

    @Inject
    public AdminLoyaltyController(
            ILoyaltyAccountRepository loyaltyRepository,
            IGuestRepository guestRepository) {

        this.loyaltyRepository = loyaltyRepository;
        this.guestRepository = guestRepository;
    }

    @FXML
    public void initialize() {
        // Initialize table columns if table exists (FXML may not have TableView yet)
        if (loyaltyTable != null) {
            setupTable();
            loadLoyaltyAccounts();
        }
    }

    private void setupTable() {
        memberNumberColumn.setCellValueFactory(new PropertyValueFactory<>("memberNumber"));
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    private void loadLoyaltyAccounts() {
        try {
            List<LoyaltyAccount> accounts = loyaltyRepository.findAll();
            loyaltyTable.getItems().clear();

            for (LoyaltyAccount account : accounts) {
                LoyaltyAccountDisplay display = new LoyaltyAccountDisplay(
                        account.getMemberNumber(),
                        account.getGuest().getFirstName() + " " + account.getGuest().getLastName(),
                        account.getGuest().getEmail(),
                        account.getCurrentPoints(),
                        String.format("CAD %.2f", account.getCurrentPoints() * 0.01));
                loyaltyTable.getItems().add(display);
            }

            LOGGER.info("Loaded " + accounts.size() + " loyalty accounts");
        } catch (Exception e) {
            LOGGER.severe("Failed to load loyalty accounts: " + e.getMessage());
            showError("Error", "Failed to load loyalty accounts: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Loyalty Account");

        ButtonType createButton =
                new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
                createButton,
                ButtonType.CANCEL
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<Guest> guestCombo = new ComboBox<>();
        guestCombo.setItems(FXCollections.observableArrayList(
                guestRepository.findAll()
        ));

        guestCombo.setConverter(new javafx.util.StringConverter<>() {

            @Override
            public String toString(Guest guest) {
                if (guest == null) {
                    return "";
                }

                return guest.getFirstName() + " " + guest.getLastName();
            }

            @Override
            public Guest fromString(String string) {
                return null;
            }
        });

        grid.add(new Label("Guest"), 0, 0);
        grid.add(guestCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != createButton) {
            return;
        }

        Guest guest = guestCombo.getValue();
        System.out.println("Guest = " + guest);

        if (guest == null) {
            showError("Error", "Please select a guest.");
            return;
        }

        if (loyaltyRepository.findByGuest(guest).isPresent()) {
            showError("Error", "This guest already has a loyalty account.");
            return;
        }

        String memberNumber = "MEM" + System.currentTimeMillis();
        System.out.println("Member Number = " + memberNumber);

        LoyaltyAccount account = new LoyaltyAccount(guest, memberNumber);

        try {

            System.out.println("About to save...");

            loyaltyRepository.save(account);

            System.out.println("Saved!");

            loadLoyaltyAccounts();

            System.out.println("Reloaded!");

        } catch (Exception e) {

            e.printStackTrace();

            showError("Error", e.getMessage());

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

    /**
     * Display class for loyalty account data in TableView.
     */
    public static class LoyaltyAccountDisplay {
        private final String memberNumber;
        private final String guestName;
        private final String email;
        private final Integer points;
        private final String value;

        public LoyaltyAccountDisplay(String memberNumber, String guestName, String email, Integer points,
                String value) {
            this.memberNumber = memberNumber;
            this.guestName = guestName;
            this.email = email;
            this.points = points;
            this.value = value;
        }

        public String getMemberNumber() {
            return memberNumber;
        }

        public String getGuestName() {
            return guestName;
        }

        public String getEmail() {
            return email;
        }

        public Integer getPoints() {
            return points;
        }

        public String getValue() {
            return value;
        }
    }
}
