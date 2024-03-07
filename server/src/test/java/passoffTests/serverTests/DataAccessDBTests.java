package passoffTests.serverTests;

import dataAccess.DataAccessDB;
import exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import model.User;
import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataAccessDBTests {

    private DataAccessDB dataAccessDB = new DataAccessDB();
    private User testUser = new User("Test", "pass", "mail@mail.org");
    private AuthData authDataInit;
    private GameData gameInit = new GameData(1, null, null, "GameInit", null);

    @BeforeEach
    public void init() {
        try {
            dataAccessDB.databaseInit();
            dataAccessDB.clear();
            authDataInit = dataAccessDB.registerUser(testUser);
            dataAccessDB.createGame("GameInit", 1);
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
    @DisplayName("Get existing username from DB")
    public void getUsernamePos() {
        try {
            Assertions.assertTrue(dataAccessDB.userDBContainsUsername(testUser));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Get nonexistent username from DB")
    public void getUsernameNeg() {
        try {
            Assertions.assertFalse(dataAccessDB.userDBContainsUsername(new User("Notauser", null, null)));
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

    @Test
    @DisplayName("Existing authData is authorized")
    public void authDataIsAuthorizedPos() {
        try {
            Assertions.assertTrue(dataAccessDB.authDataIsAuthorized(authDataInit));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Nonexistent authData is not authorized")
    public void authDataIsAuthorizedNeg() {
        try {
            Assertions.assertFalse(dataAccessDB.authDataIsAuthorized(new AuthData("notarealuser", "notarealtoken")));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Can get all game data")
    public void getGamesPos() {
        try {
            dataAccessDB.createGame("newgame", 5687);
            var gamesList = dataAccessDB.getGames();
            Assertions.assertEquals(gameInit.gameName(), gamesList.get(0).gameName());
            Assertions.assertEquals("newgame", gamesList.get(1).gameName());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("getGames returns null if no entries")
    public void getGamesNeg() {
        try {
            dataAccessDB.clear();
            var gamesList = dataAccessDB.getGames();
            Assertions.assertEquals(0, gamesList.size());
            dataAccessDB.createGame("newgame", 5687);
            gamesList = dataAccessDB.getGames();
            Assertions.assertEquals("newgame", gamesList.get(0).gameName());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("User can join a game")
    public void joinGamePos() {
        try {
            var gameName = "New Game";
            long gameID = 12345678;
            dataAccessDB.createGame(gameName, gameID);
            dataAccessDB.joinGame(authDataInit, new JoinGameRequest("WHITE", gameID));
            var games = dataAccessDB.getGames();
            var gameFirst = games.get(1);
            assertEquals(authDataInit.username(), gameFirst.whiteUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
