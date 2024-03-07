package dataAccessTests;

import dataAccess.DataAccessDB;
import exceptions.AlreadyTakenException;
import exceptions.NotFoundException;
import model.AuthData;
import model.GameData;
import model.User;
import org.junit.jupiter.api.*;
import requests.JoinGameRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataAccessTests {

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
    @DisplayName("User is authorized with username and pass")
    public void userIsAuthorizedPos() {
        try {
            Assertions.assertTrue(dataAccessDB.userIsAuthorized(testUser));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Invalid username, pass is not authorized")
    public void userIsAuthorizedNeg() {
        try {
            Assertions.assertFalse(dataAccessDB.userIsAuthorized(new User("notauser", "passy", "mail")));
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
    @DisplayName("User can log in")
    public void loginUserPos0() {
        try {
            var newUser = new User("newuser", "passpass", "mailmail");
            var authData = dataAccessDB.loginUser(newUser);
            Assertions.assertTrue(dataAccessDB.authDataIsAuthorized(authData));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("User with an existing session can login; both sessions valid")
    public void loginUserPos1() {
        try {
            var authData = dataAccessDB.loginUser(testUser);
            Assertions.assertTrue(dataAccessDB.authDataIsAuthorized(authData));
            Assertions.assertTrue(dataAccessDB.authDataIsAuthorized(authDataInit));
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
    @DisplayName("Nothing changes if an invalid user logs out")
    public void logoutUserNeg() {
        try {
            Assertions.assertNotNull(dataAccessDB.getAuthDataFromToken(authDataInit));
            dataAccessDB.logoutUser(new AuthData("notauser", "notatoken"));
            Assertions.assertNotNull(dataAccessDB.getAuthDataFromToken(authDataInit));
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
    @DisplayName("Can't get authData with nonexistent authToken'")
    public void getAuthDataFromTokenNeg() {
        try {
            Assertions.assertNull(dataAccessDB.getAuthDataFromToken(new AuthData("fakeuserhaker", "notatoken")));
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
    public void joinGamePos0() {
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

    @Test
    @DisplayName("User can join as a watcher")
    public void joinGamePos1() {
        try {
            dataAccessDB.joinGame(authDataInit, new JoinGameRequest(null, gameInit.gameID()));
            var games = dataAccessDB.getGames();
            var gameFirst = games.getFirst();
            assertEquals(authDataInit.username(), gameFirst.watchers().getFirst());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("User can't join nonexistent game")
    public void joinGameNeg0() {
        try {
            long gameID = 12345678;
            Assertions.assertThrows(NotFoundException.class, () ->
                    dataAccessDB.joinGame(authDataInit, new JoinGameRequest("WHITE", gameID)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("User can't join if a spot is taken")
    public void joinGameNeg1() {
        try {
            dataAccessDB.joinGame(authDataInit, new JoinGameRequest("WHITE", gameInit.gameID()));
            Assertions.assertThrows(AlreadyTakenException.class, () ->
                    dataAccessDB.joinGame(new AuthData("otheruser", null),
                            new JoinGameRequest("WHITE", gameInit.gameID())));
            var games = dataAccessDB.getGames();
            var gameFirst = games.getFirst();
            assertEquals(authDataInit.username(), gameFirst.whiteUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
