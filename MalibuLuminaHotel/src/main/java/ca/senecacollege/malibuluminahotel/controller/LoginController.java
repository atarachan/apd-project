package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.repositories.AdminUserRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.IAdminUserRepository;
import ca.senecacollege.malibuluminahotel.security.AuthenticationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Controller for the admin login screen.
 * Handles authentication and navigation to admin dashboard.
 * 
 * @author Malibu Lumina Hotel Team
 * @version 1.0
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;

    private AuthenticationService authService;
    private IAdminUserRepository adminUserRepository;

    /**
     * Initialize method called after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("LoginController initialized");

        // Initialize repositories and services
        adminUserRepository = new AdminUserRepositoryImpl();
        authService = new AuthenticationService(adminUserRepository);

        // Add Enter key handler for password field
        passwordField.setOnAction(event -> handleLogin());

        // Clear error when user starts typing
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> hideError());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> hideError());
    }

    /**
     * Handle login button click.
     * Validates credentials and navigates to admin dashboard on success.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty()) {
            showError("Please enter your username.");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password.");
            passwordField.requestFocus();
            return;
        }

        // Disable button during authentication
        loginButton.setDisable(true);
        hideError();

        try {
            // Attempt authentication
            Optional<AdminUser> userOptional = authService.authenticate(username, password);

            if (userOptional.isPresent()) {
                AdminUser user = userOptional.get();
                logger.info("Login successful for user: {} (Role: {})", user.getUsername(), user.getRole());

                // Navigate to admin dashboard
                SceneManager.switchScene(loginButton, "AdminDashboard.fxml");

            } else {
                showError("Invalid username or password. Please try again.");
                passwordField.clear();
                usernameField.requestFocus();
            }

        } catch (Exception e) {
            logger.error("Login error", e);
            showError("An error occurred during login. Please try again.");

        } finally {
            loginButton.setDisable(false);
        }
    }

    /**
     * Handle back button click.
     * Returns to welcome screen.
     */
    @FXML
    private void handleBack() {
        logger.info("Returning to welcome screen");
        SceneManager.switchScene(backButton, "Welcome.fxml");
    }

    /**
     * Show error message to user.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Hide error message.
     */
    private void hideError() {
        errorLabel.setVisible(false);
    }
}
