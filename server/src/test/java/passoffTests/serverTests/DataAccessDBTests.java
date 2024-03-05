package passoffTests.serverTests;

import dataAccess.DataAccessDB;
import exceptions.DataAccessException;
import model.AuthData;
import model.User;
import org.junit.jupiter.api.*;

public class DataAccessDBTests {

    private DataAccessDB dataAccessDB = new DataAccessDB();
    private User testUser = new User("Test", "pass", "mail@mail.org");
    private AuthData authDataInit;

    @BeforeEach
    public void init() {
        try {
            dataAccessDB.clear();
            authDataInit = dataAccessDB.registerUser(testUser);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Can get an existing user from DB")
    public void getUserPos() {
        try {
            Assertions.assertEquals(testUser, dataAccessDB.getUser(testUser));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Nonexistent user can't be found")
    public void getUserNeg() {
        try {
            Assertions.assertNull(dataAccessDB.getUser(new User("Burrito", "pass", "mail@mail.org")));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("User can log in with username and pass")
    public void userIsAuthorizedPos() {
        try {
            Assertions.assertTrue(dataAccessDB.userIsAuthorized(testUser));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("User with a session can login")
    public void loginUserPos() {
        try {
            Assertions.assertEquals(testUser.username(), dataAccessDB.loginUser(testUser).username());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("User loses access when they log out")
    public void logoutUserPos() {
        try {
            Assertions.assertNotNull(dataAccessDB.getAuthDataFromToken(authDataInit));
            dataAccessDB.logoutUser(authDataInit);
            Assertions.assertNull(dataAccessDB.getAuthDataFromToken(authDataInit));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Can get an existing authToken from auth")
    public void getAuthDataFromTokenPos() {
        try {
            Assertions.assertEquals(testUser.username(), dataAccessDB.getAuthDataFromToken(authDataInit).username());
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
