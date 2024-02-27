package passoffTests.serviceTests;

import model.AuthData;
import dataAccess.DataAccess;
import exceptions.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import exceptions.UnauthorizedException;
import services.RegistrationService;
import services.SessionService;
import model.User;

import static org.junit.jupiter.api.Assertions.*;

public class SessionServiceTests {

    private DataAccess dataAccess = new DataAccess();
    private RegistrationService registrationService = new RegistrationService();
    private SessionService sessionService = new SessionService();
    private User regularUser = new User("kevin23", "okokokok99", "okay@gmail.com");

    @BeforeEach
    public void init() {
        try {
            registrationService.clearDatabase(dataAccess);
        } catch (DataAccessException e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("Can login existing user")
    public void loginSimple() {
        try {
            registrationService.registerUser(dataAccess, regularUser);
            assertNotNull(sessionService.loginUser(dataAccess, regularUser).username());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("Allows user obj to not include email (since server will not provide it)")
    public void noEmailLogin() {
        try {
            var user = new User("auser", "apassword", null);
            registrationService.registerUser(dataAccess, user);
            assertDoesNotThrow(() -> sessionService.loginUser(dataAccess, user));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("Does not allow login without proper name and pass")
    public void unauthorizedLogin() {
        try {
            registrationService.registerUser(dataAccess, regularUser);
            var fakeUser = new User("notauser", "notapassword", null);
            assertThrows(UnauthorizedException.class, () -> sessionService.loginUser(dataAccess, fakeUser));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("Can logout existing user (depends on login functionality working as expected)")
    public void logoutSimple() {
        try {
            registrationService.registerUser(dataAccess, regularUser);
            AuthData result = sessionService.loginUser(dataAccess, regularUser);
            assertNotNull(result.username());
            var authData = new AuthData(null, result.authToken());
            sessionService.logoutUser(dataAccess, authData);
            assertThrows(UnauthorizedException.class, () -> sessionService.logoutUser(dataAccess, authData));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("User can't logout without having logged in")
    public void logoutWithoutUsers() {
        try {
            var authData = new AuthData(regularUser.username(), "as38987y59872");
            assertThrows(UnauthorizedException.class, () -> sessionService.logoutUser(dataAccess, authData));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("User can login again after having logged out, then they can log in again")
    public void loginAgain() {
        try {
            registrationService.registerUser(dataAccess, regularUser);

            AuthData result = sessionService.loginUser(dataAccess, regularUser);
            assertNotNull(result.username());

            var authData = new AuthData(null, result.authToken());
            assertDoesNotThrow(() -> sessionService.logoutUser(dataAccess, authData));
            assertThrows(UnauthorizedException.class, () -> sessionService.logoutUser(dataAccess, authData));

            result = sessionService.loginUser(dataAccess, regularUser);
            assertNotNull(result.username());

            var authData1 = new AuthData(null, result.authToken());
            assertDoesNotThrow(() -> sessionService.logoutUser(dataAccess, authData1));
            assertThrows(UnauthorizedException.class, () -> sessionService.logoutUser(dataAccess, authData1));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("User can login and get a new AuthToken when that same username is already logged in")
    public void loginTwiceInARow() {
        try {
            registrationService.registerUser(dataAccess, regularUser);

            AuthData result = sessionService.loginUser(dataAccess, regularUser);
            assertNotNull(result.username());

            AuthData result1 = sessionService.loginUser(dataAccess, regularUser);
            assertNotNull(result.username());

            assertNotEquals(result.authToken(), result1.authToken());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
