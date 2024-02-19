package passoffTests.serviceTests;

import authData.AuthData;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.*;
import registrationService.RegistrationService;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTests {

    private RegistrationService registrationService = new RegistrationService();
    private User regularUser = new User("kevin23", "okokokok99", "okay@gmail.com");

    @Test
    @DisplayName("Regular user's name returns correctly from service")
    public void registerUserReturnsName() {
        AuthData result = null;
        try {
            result = registrationService.registerUser(regularUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals(regularUser.username(), result.username());
    }

    @Test
    @DisplayName("Same user can't be registered twice")
    public void registerSameUserTwice() {
        try {
            AuthData result = registrationService.registerUser(regularUser);
            assertNull(registrationService.registerUser(regularUser));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Same username can't be registered twice")
    public void registerSameNameTwice() {
        try {
            AuthData result = registrationService.registerUser(regularUser);
            var otherUser = new User(regularUser.username(), "otherpass", "email@email.com");
            assertNull(registrationService.registerUser(otherUser));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Same username can't be registered twice")
    public void registerTwoUsers() {
        try {
            AuthData result = registrationService.registerUser(regularUser);
            var otherUser = new User("otherguy", "otherpass", "email@email.com");
            registrationService.registerUser(otherUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
