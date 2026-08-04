package ca.senecacollege.malibuluminahotel.security;

import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.repositories.IAdminUserRepository;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for handling admin user authentication.
 * Uses BCrypt for password verification and manages login sessions.
 * 
 * @author Malibu Lumina Hotel Team
 * @version 1.0
 */
public class AuthenticationService implements IAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    private final IAdminUserRepository adminUserRepository;
    private final SessionManager sessionManager;

    /**
     * Constructor with dependency injection.
     * 
     * @param adminUserRepository Repository for admin user operations
     */
    @Inject
    public AuthenticationService(IAdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * Authenticate an admin user with username and password.
     * Verifies credentials using BCrypt and updates last login timestamp.
     * 
     * @param username The username to authenticate
     * @param password The plaintext password to verify
     * @return Optional containing the AdminUser if authentication succeeds, empty otherwise
     */
    @Override
    public Optional<AdminUser> authenticate(String username, String password) {
        logger.debug("Authentication attempt for user: {}", username);

        if (username == null || username.trim().isEmpty()) {
            logger.warn("Authentication failed: Empty username");
            return Optional.empty();
        }

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Authentication failed: Empty password for user {}", username);
            return Optional.empty();
        }

        // Find user by username
        Optional<AdminUser> userOptional = adminUserRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            logger.warn("Authentication failed: User not found - {}", username);
            return Optional.empty();
        }

        AdminUser user = userOptional.get();

        // Check if account is active
        if (!user.getIsActive()) {
            logger.warn("Authentication failed: Account inactive - {}", username);
            return Optional.empty();
        }

        // Verify password using BCrypt
        boolean passwordMatches = PasswordHasher.checkPassword(password, user.getPassword());

        if (!passwordMatches) {
            logger.warn("Authentication failed: Invalid password for user {}", username);
            return Optional.empty();
        }

        // Update last login timestamp
        user.setLastLogin(LocalDateTime.now());
        adminUserRepository.update(user);

        // Set user in session
        sessionManager.login(user);

        logger.info("Authentication successful: {} (Role: {})", user.getUsername(), user.getRole());
        return Optional.of(user);
    }

    /**
     * Logout the current user.
     */
    @Override
    public void logout() {
        sessionManager.logout();
        logger.info("User logged out");
    }

    /**
     * Get the currently logged-in admin user.
     * 
     * @return Optional containing the current user if logged in
     */
    @Override
    public Optional<AdminUser> getCurrentUser() {
        return Optional.ofNullable(sessionManager.getCurrentUser());
    }

    /**
     * Check if there is a user currently logged in.
     * 
     * @return true if a user is logged in
     */
    @Override
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
}
