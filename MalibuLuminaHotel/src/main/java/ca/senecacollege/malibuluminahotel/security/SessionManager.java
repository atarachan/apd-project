package ca.senecacollege.malibuluminahotel.security;

import ca.senecacollege.malibuluminahotel.models.AdminUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton session manager for tracking authenticated admin users.
 * Maintains the currently logged-in user across the application.
 * Thread-safe implementation using double-checked locking.
 * 
 * @author Malibu Lumina Hotel Team
 * @version 1.0
 */
public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    
    private static volatile SessionManager instance;
    
    private AdminUser currentUser;

    /**
     * Private constructor for singleton pattern.
     */
    private SessionManager() {
        logger.info("SessionManager initialized");
    }

    /**
     * Get the singleton instance of SessionManager.
     * Thread-safe double-checked locking.
     * 
     * @return The singleton SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Login an admin user, setting them as the current session user.
     * 
     * @param user The AdminUser who has successfully authenticated
     * @throws IllegalArgumentException if user is null
     */
    public void login(AdminUser user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot login null user");
        }
        
        this.currentUser = user;
        logger.info("User logged in: {} (Role: {})", user.getUsername(), user.getRole());
    }

    /**
     * Logout the current user, clearing the session.
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getUsername());
            this.currentUser = null;
        }
    }

    /**
     * Get the currently logged-in admin user.
     * 
     * @return The current AdminUser, or null if no one is logged in
     */
    public AdminUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if there is a user currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if the current user has a specific role.
     * 
     * @param role The role to check
     * @return true if user is logged in and has the specified role
     */
    public boolean hasRole(ca.senecacollege.malibuluminahotel.models.enums.UserRole role) {
        return isLoggedIn() && currentUser.getRole() == role;
    }

    /**
     * Get the username of the current user.
     * 
     * @return Username, or "Guest" if not logged in
     */
    public String getCurrentUsername() {
        return isLoggedIn() ? currentUser.getUsername() : "Guest";
    }
}
