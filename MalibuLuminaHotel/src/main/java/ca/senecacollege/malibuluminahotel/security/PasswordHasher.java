package ca.senecacollege.malibuluminahotel.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing using BCrypt.
 * BCrypt is a one-way hashing function designed for password security.
 * It includes salt generation and cost factor for protection against brute force attacks.
 * 
 * @author Malibu Lumina Hotel Team
 * @version 1.0
 */
public class PasswordHasher {

    /**
     * Cost factor for BCrypt (higher = more secure but slower).
     * Default of 12 provides good balance between security and performance.
     */
    private static final int COST_FACTOR = 12;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private PasswordHasher() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    /**
     * Hash a plaintext password using BCrypt.
     * Automatically generates a salt and applies the cost factor.
     * 
     * @param plainPassword The plaintext password to hash
     * @return BCrypt hashed password string (includes salt)
     * @throws IllegalArgumentException if password is null or empty
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST_FACTOR));
    }

    /**
     * Check if a plaintext password matches a BCrypt hash.
     * Safe against timing attacks.
     * 
     * @param plainPassword The plaintext password to check
     * @param hashedPassword The BCrypt hash to compare against
     * @return true if password matches, false otherwise
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Plaintext password cannot be null or empty");
        }
        
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }
}
