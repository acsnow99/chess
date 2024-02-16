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
    @DisplayName("Register regular User")
    public void registerRegularUser() {
        AuthData result = registrationService.registerUser(regularUser);

        assertEquals(regularUser.username(), result.username());
    }

}
