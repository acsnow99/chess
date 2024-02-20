package passoffTests.serviceTests;

import authData.AuthData;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import otherExceptions.UnauthorizedException;
import registrationService.RegistrationService;
import sessionService.SessionService;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

public class SessionServiceTests {

    private RegistrationService registrationService = new RegistrationService();
    private SessionService sessionService = new SessionService();
    private User regularUser = new User("kevin23", "okokokok99", "okay@gmail.com");

    @BeforeEach
    public void init() {
        try {
            registrationService.clearDatabase();
        } catch (DataAccessException e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("Can login existing user")
    public void loginSimple() {
        try {
            registrationService.registerUser(regularUser);
            assertNotNull(sessionService.loginUser(regularUser).username());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("Can logout existing user (depends on login functionality working as expected)")
    public void logoutSimple() {
        try {
            registrationService.registerUser(regularUser);
            AuthData result = sessionService.loginUser(regularUser);
            assertNotNull(result.username());
            var authData = new AuthData(null, result.authToken());
            sessionService.logoutUser(authData);
            assertThrows(UnauthorizedException.class, () -> sessionService.logoutUser(authData));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("User can't logout without having logged in")
    public void logoutWithoutUsers() {
        try {
            var authData = new AuthData(regularUser.username(), "as38987y59872");
            assertThrows(UnauthorizedException.class, () -> sessionService.logoutUser(authData));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
