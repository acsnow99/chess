package passoffTests.serverTests;

import dataAccess.DataAccessDB;
import exceptions.DataAccessException;
import model.User;
import org.junit.jupiter.api.*;

public class DataAccessDBTests {

    private DataAccessDB dataAccessDB = new DataAccessDB();
    private User testUser = new User("Test", "pass", "mail@mail.org");

    @BeforeEach
    public void init() {
        dataAccessDB.clear();
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
    public void userIsAuthorized() {
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
    @DisplayName("Can get an existing authToken from auth")
    public void getAuthDataFromTokenPos() {
        try {
            var authData = dataAccessDB.loginUser(testUser);
            Assertions.assertEquals(testUser.username(), dataAccessDB.getAuthDataFromToken(authData).username());
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
