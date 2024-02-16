package passoffTests.serviceTests;

import authData.AuthData;
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
        AuthData result = registrationService.registerUser(regularUser);

        assertEquals(regularUser.username(), result.username());
    }

    @Test
    @DisplayName("Same user can't be registered twice")
    public void registerTwice() {
        AuthData result = registrationService.registerUser(regularUser);

        assertNull(registrationService.registerUser(regularUser));
    }
    
}
