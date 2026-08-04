package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;
import ca.senecacollege.malibuluminahotel.repositories.ILoyaltyAccountRepository;
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

    @Inject
    public AdminLoyaltyController(ILoyaltyAccountRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
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
