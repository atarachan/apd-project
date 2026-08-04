package ca.senecacollege.malibuluminahotel.security;

import ca.senecacollege.malibuluminahotel.models.AdminUser;

import java.util.Optional;

public interface IAuthenticationService {

    Optional<AdminUser> authenticate(String username, String password);

    void logout();

    Optional<AdminUser> getCurrentUser();

    boolean isLoggedIn();
}
