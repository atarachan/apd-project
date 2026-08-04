package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.models.enums.UserRole;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AdminUser entity representing administrative users in the system.
 * Matches ERD specification: adminID, username, password, role.
 * Supports authentication and administrative functions.
 * 
 * @author Malibu Lumina Hotel Team
 * @version 1.0
 */
@Entity
@Table(name = "AdminUsers")
public class AdminUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id", unique = true, nullable = false)
    private Long adminID;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password; // BCrypt hashed password

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * Default constructor initializes timestamps and active status.
     */
    public AdminUser() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    /**
     * Parameterized constructor for creating an admin user.
     * 
     * @param username Unique username for login
     * @param password BCrypt hashed password
     * @param role User role (ADMIN or MANAGER)
     * @param fullName Full name of the admin user
     */
    public AdminUser(String username, String password, UserRole role, String fullName) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    /**
     * Login method - validates credentials via AuthenticationService.
     * This is a business logic placeholder as per ERD method specification.
     * Actual authentication is handled by AuthenticationService.
     * 
     * @return true if authentication succeeds
     */
    public boolean login() {
        // Actual implementation delegated to AuthenticationService
        // This method signature kept for ERD compliance
        return this.isActive;
    }

    /**
     * Apply discount method - allows admins to apply discounts to bills.
     * Implementation to be completed in future phases.
     * Method signature kept for ERD compliance.
     */
    public void applyDiscount() {
        // TODO: Implement discount logic in Phase 4 (Admin Features)
        // Will integrate with Bill entity
    }

    /**
     * Updates the last login timestamp to current time.
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getAdminID() {
        return adminID;
    }

    public void setAdminID(Long adminID) {
        this.adminID = adminID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "adminID=" + adminID +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
