package passoffTests.serviceTests;

import model.AuthData;
import dataAccess.DataAccess;
import exceptions.DataAccessException;
import org.junit.jupiter.api.*;
import exceptions.MissingDataException;
import services.RegistrationService;
import model.User;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTests {

    private DataAccess dataAccess = new DataAccess();
    private RegistrationService registrationService = new RegistrationService();
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
    @DisplayName("Regular user's name returns correctly from service")
    public void registerUserReturnsName() {
        AuthData result = null;
        try {
            result = registrationService.registerUser(dataAccess, regularUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertNotNull(result);
        assertEquals(regularUser.username(), result.username());
    }

    @Test
    @DisplayName("Request missing username, email, or password")
    public void missingDataRegister() {
        AuthData result = null;
        try {
            var missingPassUser = new User("aka", "", "email");
            assertThrows(MissingDataException.class, () -> registrationService.registerUser(dataAccess, missingPassUser));
            var missingPassUser1 = new User("aka", null, "email");
            assertThrows(MissingDataException.class, () -> registrationService.registerUser(dataAccess, missingPassUser1));
            var missingNameUser = new User("", "what", "email");
            assertThrows(MissingDataException.class, () -> registrationService.registerUser(dataAccess, missingNameUser));
            var missingNameUser1 = new User(null, "what", "email");
            assertThrows(MissingDataException.class, () -> registrationService.registerUser(dataAccess, missingNameUser1));
            var missingEmailUser = new User("okay", "what", "");
            assertThrows(MissingDataException.class, () -> registrationService.registerUser(dataAccess, missingEmailUser));
            var missingEmailUser1 = new User("okay", "what", null);
            assertThrows(MissingDataException.class, () -> registrationService.registerUser(dataAccess, missingEmailUser1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Same user can't be registered twice")
    public void registerSameUserTwice() {
        try {
            AuthData result = registrationService.registerUser(dataAccess, regularUser);
            assertNull(registrationService.registerUser(dataAccess, regularUser));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Same username can't be registered twice")
    public void registerSameNameTwice() {
        try {
            AuthData result = registrationService.registerUser(dataAccess, regularUser);
            var otherUser = new User(regularUser.username(), "otherpass", "email@email.com");
            assertNull(registrationService.registerUser(dataAccess, otherUser));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Register two unique users")
    public void registerTwoUsers() {
        try {
            AuthData result = registrationService.registerUser(dataAccess, regularUser);
            var otherUser = new User("otherguy", "otherpass", "email@email.com");
            registrationService.registerUser(dataAccess, otherUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Clearing Database removes all user entries")
    public void clearDatabase() {
        try {
            registrationService.registerUser(dataAccess, regularUser);
            registrationService.clearDatabase(dataAccess);
            AuthData result = registrationService.registerUser(dataAccess, regularUser);
            assertNotNull(result.username());
            assertEquals(result.username(), regularUser.username());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
