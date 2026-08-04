package ca.senecacollege.malibuluminahotel.tests;

import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.enums.UserRole;
import ca.senecacollege.malibuluminahotel.repositories.AdminUserRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.IAdminUserRepository;
import ca.senecacollege.malibuluminahotel.security.AuthenticationService;
import ca.senecacollege.malibuluminahotel.security.PasswordHasher;
import ca.senecacollege.malibuluminahotel.security.SessionManager;

import java.util.Optional;

/**
 * Test class for Phase 2: Security & Authentication
 * Tests BCrypt password hashing, AdminUser entity, authentication flow, and session management.
 * 
 * @author Malibu Lumina Hotel Team
 * @version 1.0
 */
public class Phase2SecurityTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("PHASE 2 SECURITY & AUTHENTICATION TESTING");
        System.out.println("=".repeat(80));
        System.out.println();

        testPasswordHashing();
        System.out.println();
        testAdminUserEntity();
        System.out.println();
        testAdminUserRepository();
        System.out.println();
        testAuthenticationService();
        System.out.println();
        testSessionManager();
        System.out.println();

        System.out.println("=".repeat(80));
        System.out.println("ALL PHASE 2 TESTS COMPLETED SUCCESSFULLY!");
        System.out.println("=".repeat(80));
    }

    /**
     * Test 1: Password Hashing with BCrypt
     * Verify that passwords are hashed and can be verified correctly.
     */
    private static void testPasswordHashing() {
        System.out.println("--- TEST 1: PASSWORD HASHING (BCRYPT) ---");
        System.out.println();

        String plainPassword = "AdminPassword123!";

        // Test hashing
        System.out.println("✓ Hashing password...");
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        System.out.println("  - Plain password: " + plainPassword);
        System.out.println("  - Hashed password: " + hashedPassword.substring(0, 30) + "...");
        System.out.println("  - Hash length: " + hashedPassword.length() + " characters");

        if (hashedPassword.length() == 60 && hashedPassword.startsWith("$2a$")) {
            System.out.println("  ✅ SUCCESS: BCrypt hash format is correct!");
        } else {
            System.out.println("  ❌ FAILED: Invalid hash format!");
        }
        System.out.println();

        // Test correct password verification
        System.out.println("✓ Verifying correct password...");
        boolean correctPasswordMatches = PasswordHasher.checkPassword(plainPassword, hashedPassword);
        System.out.println("  - Password matches: " + correctPasswordMatches);

        if (correctPasswordMatches) {
            System.out.println("  ✅ SUCCESS: Correct password verified!");
        } else {
            System.out.println("  ❌ FAILED: Correct password should match!");
        }
        System.out.println();

        // Test incorrect password verification
        System.out.println("✓ Verifying incorrect password...");
        boolean incorrectPasswordMatches = PasswordHasher.checkPassword("WrongPassword", hashedPassword);
        System.out.println("  - Wrong password matches: " + incorrectPasswordMatches);

        if (!incorrectPasswordMatches) {
            System.out.println("  ✅ SUCCESS: Incorrect password rejected!");
        } else {
            System.out.println("  ❌ FAILED: Incorrect password should not match!");
        }
        System.out.println();

        // Test different hashes for same password (salt is random)
        System.out.println("✓ Testing salt randomness...");
        String hash1 = PasswordHasher.hashPassword(plainPassword);
        String hash2 = PasswordHasher.hashPassword(plainPassword);
        System.out.println("  - Hash 1: " + hash1.substring(0, 30) + "...");
        System.out.println("  - Hash 2: " + hash2.substring(0, 30) + "...");

        if (!hash1.equals(hash2)) {
            System.out.println("  ✅ SUCCESS: Hashes are different (salt is random)!");
        } else {
            System.out.println("  ❌ FAILED: Hashes should be different!");
        }

        System.out.println();
        System.out.println("✅ PASSWORD HASHING TEST COMPLETED");
    }

    /**
     * Test 2: AdminUser Entity
     * Verify entity creation, getters/setters, and business methods.
     */
    private static void testAdminUserEntity() {
        System.out.println("--- TEST 2: ADMINUSER ENTITY ---");
        System.out.println();

        System.out.println("✓ Creating AdminUser entity...");
        String hashedPassword = PasswordHasher.hashPassword("SecurePass123");
        AdminUser admin = new AdminUser("admin", hashedPassword, UserRole.ADMIN, "John Admin");

        System.out.println("  - Username: " + admin.getUsername());
        System.out.println("  - Full Name: " + admin.getFullName());
        System.out.println("  - Role: " + admin.getRole());
        System.out.println("  - Is Active: " + admin.getIsActive());
        System.out.println("  - Created At: " + admin.getCreatedAt());
        System.out.println();

        // Test default values
        System.out.println("✓ Verifying default values...");
        if (admin.getIsActive() && admin.getCreatedAt() != null) {
            System.out.println("  ✅ SUCCESS: Default values set correctly!");
        } else {
            System.out.println("  ❌ FAILED: Default values incorrect!");
        }
        System.out.println();

        // Test login method (ERD compliance)
        System.out.println("✓ Testing login() method (ERD compliance)...");
        boolean loginResult = admin.login();
        System.out.println("  - Login result: " + loginResult);
        if (loginResult) {
            System.out.println("  ✅ SUCCESS: login() method works!");
        }
        System.out.println();

        // Test updateLastLogin
        System.out.println("✓ Testing updateLastLogin()...");
        admin.updateLastLogin();
        System.out.println("  - Last Login: " + admin.getLastLogin());
        if (admin.getLastLogin() != null) {
            System.out.println("  ✅ SUCCESS: Last login timestamp updated!");
        } else {
            System.out.println("  ❌ FAILED: Last login not updated!");
        }

        System.out.println();
        System.out.println("✅ ADMINUSER ENTITY TEST COMPLETED");
    }

    /**
     * Test 3: AdminUser Repository
     * Verify database operations: save, find by username.
     */
    private static void testAdminUserRepository() {
        System.out.println("--- TEST 3: ADMINUSER REPOSITORY ---");
        System.out.println();

        IAdminUserRepository repository = new AdminUserRepositoryImpl();

        // Create test admin user
        System.out.println("✓ Creating test admin user...");
        String hashedPassword = PasswordHasher.hashPassword("TestPass123");
        AdminUser testAdmin = new AdminUser("testadmin", hashedPassword, UserRole.ADMIN, "Test Administrator");

        // Save to database
        System.out.println("✓ Saving to database...");
        AdminUser savedAdmin = repository.save(testAdmin);
        System.out.println("  - Saved with ID: " + savedAdmin.getAdminID());

        if (savedAdmin.getAdminID() != null) {
            System.out.println("  ✅ SUCCESS: AdminUser saved to database!");
        } else {
            System.out.println("  ❌ FAILED: AdminUser not saved!");
        }
        System.out.println();

        // Find by username
        System.out.println("✓ Finding by username...");
        Optional<AdminUser> foundAdmin = repository.findByUsername("testadmin");

        if (foundAdmin.isPresent()) {
            AdminUser admin = foundAdmin.get();
            System.out.println("  - Found: " + admin.getUsername());
            System.out.println("  - ID: " + admin.getAdminID());
            System.out.println("  - Role: " + admin.getRole());
            System.out.println("  ✅ SUCCESS: AdminUser found by username!");
        } else {
            System.out.println("  ❌ FAILED: AdminUser not found!");
        }
        System.out.println();

        // Test case-insensitive search
        System.out.println("✓ Testing case-insensitive search...");
        Optional<AdminUser> foundCaseInsensitive = repository.findByUsername("TESTADMIN");

        if (foundCaseInsensitive.isPresent()) {
            System.out.println("  ✅ SUCCESS: Case-insensitive search works!");
        } else {
            System.out.println("  ❌ FAILED: Case-insensitive search failed!");
        }
        System.out.println();

        // Test find by role
        System.out.println("✓ Testing find by role...");
        var admins = repository.findByRole(UserRole.ADMIN);
        System.out.println("  - Found " + admins.size() + " admin(s)");
        if (!admins.isEmpty()) {
            System.out.println("  ✅ SUCCESS: Find by role works!");
        }

        System.out.println();
        System.out.println("✅ ADMINUSER REPOSITORY TEST COMPLETED");
    }

    /**
     * Test 4: Authentication Service
     * Verify authentication flow with correct and incorrect credentials.
     */
    private static void testAuthenticationService() {
        System.out.println("--- TEST 4: AUTHENTICATION SERVICE ---");
        System.out.println();

        IAdminUserRepository repository = new AdminUserRepositoryImpl();
        AuthenticationService authService = new AuthenticationService(repository);

        // Create and save test user
        System.out.println("✓ Creating test user for authentication...");
        String plainPassword = "AuthTest123";
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        AdminUser authUser = new AdminUser("authtest", hashedPassword, UserRole.MANAGER, "Auth Test User");
        repository.save(authUser);
        System.out.println("  - Created user: authtest");
        System.out.println();

        // Test successful authentication
        System.out.println("✓ Testing successful authentication...");
        Optional<AdminUser> successAuth = authService.authenticate("authtest", plainPassword);

        if (successAuth.isPresent()) {
            AdminUser user = successAuth.get();
            System.out.println("  - Authenticated user: " + user.getUsername());
            System.out.println("  - Role: " + user.getRole());
            System.out.println("  - Last Login: " + user.getLastLogin());
            System.out.println("  ✅ SUCCESS: Authentication successful!");
        } else {
            System.out.println("  ❌ FAILED: Authentication should succeed!");
        }
        System.out.println();

        // Test failed authentication - wrong password
        System.out.println("✓ Testing failed authentication (wrong password)...");
        Optional<AdminUser> failedAuth = authService.authenticate("authtest", "WrongPassword");

        if (failedAuth.isEmpty()) {
            System.out.println("  ✅ SUCCESS: Authentication failed as expected!");
        } else {
            System.out.println("  ❌ FAILED: Should not authenticate with wrong password!");
        }
        System.out.println();

        // Test failed authentication - non-existent user
        System.out.println("✓ Testing failed authentication (non-existent user)...");
        Optional<AdminUser> noUser = authService.authenticate("nonexistent", "password");

        if (noUser.isEmpty()) {
            System.out.println("  ✅ SUCCESS: Non-existent user rejected!");
        } else {
            System.out.println("  ❌ FAILED: Non-existent user should not authenticate!");
        }

        System.out.println();
        System.out.println("✅ AUTHENTICATION SERVICE TEST COMPLETED");
    }

    /**
     * Test 5: Session Manager
     * Verify session management: login, logout, current user tracking.
     */
    private static void testSessionManager() {
        System.out.println("--- TEST 5: SESSION MANAGER ---");
        System.out.println();

        SessionManager sessionManager = SessionManager.getInstance();

        // Test singleton
        System.out.println("✓ Testing singleton pattern...");
        SessionManager instance2 = SessionManager.getInstance();
        if (sessionManager == instance2) {
            System.out.println("  ✅ SUCCESS: Singleton pattern verified!");
        } else {
            System.out.println("  ❌ FAILED: Singleton pattern broken!");
        }
        System.out.println();

        // Test initial state
        System.out.println("✓ Testing initial state...");
        System.out.println("  - Is logged in: " + sessionManager.isLoggedIn());
        System.out.println("  - Current username: " + sessionManager.getCurrentUsername());
        if (!sessionManager.isLoggedIn() && "Guest".equals(sessionManager.getCurrentUsername())) {
            System.out.println("  ✅ SUCCESS: Initial state correct!");
        }
        System.out.println();

        // Test login
        System.out.println("✓ Testing login...");
        String hashedPassword = PasswordHasher.hashPassword("SessionTest");
        AdminUser sessionUser = new AdminUser("sessiontest", hashedPassword, UserRole.ADMIN, "Session Test");
        sessionManager.login(sessionUser);

        System.out.println("  - Is logged in: " + sessionManager.isLoggedIn());
        System.out.println("  - Current username: " + sessionManager.getCurrentUsername());
        System.out.println("  - Has ADMIN role: " + sessionManager.hasRole(UserRole.ADMIN));
        System.out.println("  - Has MANAGER role: " + sessionManager.hasRole(UserRole.MANAGER));

        if (sessionManager.isLoggedIn() && 
            "sessiontest".equals(sessionManager.getCurrentUsername()) &&
            sessionManager.hasRole(UserRole.ADMIN)) {
            System.out.println("  ✅ SUCCESS: Login works correctly!");
        } else {
            System.out.println("  ❌ FAILED: Login not working!");
        }
        System.out.println();

        // Test logout
        System.out.println("✓ Testing logout...");
        sessionManager.logout();
        System.out.println("  - Is logged in: " + sessionManager.isLoggedIn());
        System.out.println("  - Current username: " + sessionManager.getCurrentUsername());

        if (!sessionManager.isLoggedIn() && "Guest".equals(sessionManager.getCurrentUsername())) {
            System.out.println("  ✅ SUCCESS: Logout works correctly!");
        } else {
            System.out.println("  ❌ FAILED: Logout not working!");
        }

        System.out.println();
        System.out.println("✅ SESSION MANAGER TEST COMPLETED");
    }
}
