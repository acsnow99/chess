package clientTests;

import exceptions.HttpResponseException;
import model.AuthData;
import model.User;
import org.junit.jupiter.api.*;
import serverFacade.ServerFacade;

import server.Server;
import serverFacade.*;


public class ServerFacadeTests {

    private static Server server;
    private static int port;
    private ServerFacadeSession facadeSession;
    private ServerFacadeRegistration facadeRegister;
    private ServerFacadeGame facadeGame;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDBBeforeTest() {
        facadeSession = new ServerFacadeSession("http://127.0.0.1:" + port);
        facadeRegister = new ServerFacadeRegistration("http://127.0.0.1:" + port);
        facadeGame = new ServerFacadeGame("http://127.0.0.1:" + port);
        try {
            facadeRegister.clearDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Test
    @DisplayName("Clear database with no errors")
    public void clearDB() {
        try {
            var facade = new ServerFacadeRegistration("http://127.0.0.1:" + port);
            Assertions.assertDoesNotThrow(facade::clearDatabase);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Clear database errors when trying to access invalid url")
    public void clearDBNeg() {
        try {
            var facade = new ServerFacadeRegistration("http://127.0.0.1:" + 902984);
            Assertions.assertThrows(HttpResponseException.class, facade::clearDatabase);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Register user runs without error")
    public void registerUser() {
        try {
            var facade = new ServerFacadeRegistration("http://127.0.0.1:" + port);
            var user = new User("user", "pass", "mail@mail.mail");
            Assertions.assertDoesNotThrow(() -> facade.register(user));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Register user errors if username is already taken")
    public void registerUserNeg() {
        try {
            var facade = new ServerFacadeRegistration("http://127.0.0.1:" + port);
            var user = new User("user", "pass", "mail@mail.mail");
            Assertions.assertDoesNotThrow(() -> facade.register(user));
            Assertions.assertThrows(HttpResponseException.class, () -> facade.register(user));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Register returns valid authData")
    public void registerUserGetName() {
        try {
            var facade = new ServerFacadeRegistration("http://127.0.0.1:" + port);
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facade.register(user);
            Assertions.assertEquals(user.username(), authData.username());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Login with no errors")
    public void loginUser() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            facadeRegister.register(user);
            Assertions.assertDoesNotThrow(() -> facadeSession.login(user));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Login errors if username or password is incorrect")
    public void loginUserNeg() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            facadeRegister.register(user);
            Assertions.assertThrows(HttpResponseException.class, () -> facadeSession.login(new User(user.username(), "notapassword", null)));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Login returns valid authData")
    public void loginUserGetName() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            facadeRegister.register(user);
            var authData = facadeSession.login(user);
            Assertions.assertEquals(user.username(), authData.username());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Login multiple times in a row")
    public void loginMultiple() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            facadeRegister.register(user);
            var authData = facadeSession.login(user);
            Assertions.assertDoesNotThrow(() -> facadeSession.login(user));
            facadeRegister.clearDatabase();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Logout works with no errors")
    public void logoutUser() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            Assertions.assertDoesNotThrow(() -> facadeSession.logout(authData));
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Logout errors if authData is invalid")
    public void logoutUserNeg() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            Assertions.assertDoesNotThrow(() -> facadeSession.logout(authData));
            Assertions.assertThrows(HttpResponseException.class, () -> facadeSession.logout(authData));

        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Create a game runs with no errors")
    public void createGame() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            Assertions.assertDoesNotThrow(() -> facadeGame.createGame(authData, "testGame"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Cannot create a game without a name for the game")
    public void createGameNeg() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            Assertions.assertThrows(HttpResponseException.class, () -> facadeGame.createGame(authData, null));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Get games runs with no errors")
    public void getGames() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            facadeGame.createGame(authData, "testGame");
            Assertions.assertDoesNotThrow(() -> facadeGame.getGames(authData));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Cannot get games without authorization")
    public void getGamesNeg() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            facadeGame.createGame(authData, "testGame");
            Assertions.assertThrows(HttpResponseException.class, () -> facadeGame.getGames(null));
            Assertions.assertThrows(HttpResponseException.class, () -> facadeGame.getGames(new AuthData("notauser", "faketoken")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Get games returns correct list")
    public void getGamesAndCheck() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            facadeGame.createGame(authData, "testGame");
            var gamesList = facadeGame.getGames(authData);
            Assertions.assertEquals("testGame", gamesList.getFirst().gameName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Joining game runs with no errors")
    public void joinGame() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            long gameID = facadeGame.createGame(authData, "testGame");
            Assertions.assertDoesNotThrow(() -> facadeGame.joinGame(authData, "WHITE", gameID));
            Assertions.assertDoesNotThrow(() -> facadeGame.joinGame(authData, "BLACK", gameID));
            Assertions.assertDoesNotThrow(() -> facadeGame.joinGame(authData, null, gameID));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Cannot join game without authorization, or without correct gameID")
    public void joinGameNeg() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            long gameID = facadeGame.createGame(authData, "testGame");
            Assertions.assertThrows(HttpResponseException.class, () -> facadeGame.joinGame(new AuthData("notauser", "notatoken"), "WHITE", gameID));
            Assertions.assertThrows(HttpResponseException.class, () -> facadeGame.joinGame(authData, "WHITE", 340905093));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Joining game actually joins the user to the game")
    public void joinGameCheck() {
        try {
            var user = new User("user", "pass", "mail@mail.mail");
            var authData = facadeRegister.register(user);
            long gameID = facadeGame.createGame(authData, "testGame");
            facadeGame.joinGame(authData, null, gameID);

            var gameData = facadeGame.getGames(authData);
            Assertions.assertEquals(gameData.getFirst().watchers().getFirst(), "user");

            facadeGame.joinGame(authData, "WHITE", gameID);
            gameData = facadeGame.getGames(authData);
            Assertions.assertEquals(gameData.getFirst().whiteUsername(), "user");

            facadeGame.joinGame(authData, "BLACK", gameID);
            gameData = facadeGame.getGames(authData);
            Assertions.assertEquals(gameData.getFirst().blackUsername(), "user");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }
}
