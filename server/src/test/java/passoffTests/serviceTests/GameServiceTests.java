package passoffTests.serviceTests;

import exceptions.*;
import model.AuthData;
import dataAccess.DataAccess;
import model.GameData;
import services.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.RegistrationService;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import services.SessionService;
import model.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private DataAccess dataAccess = new DataAccess();
    private RegistrationService registrationService = new RegistrationService();
    private SessionService sessionService = new SessionService();
    private GameService gameService = new GameService();
    private User regularUser = new User("kevin23", "okokokok99", "okay@gmail.com");

    @BeforeEach
    public void init() {
        try {
            registrationService.clearDatabase(dataAccess);
            registrationService.registerUser(dataAccess, regularUser);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("Default game data returns correctly")
    public void getGameData() {
        ArrayList<GameData> result = null;
        var game = new GameData(12345, "Al", "Darin", "The Game", new ArrayList<>());
        var expected = new ArrayList<GameData>();
        try {
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            result = gameService.getGames(dataAccess, authData);
            assertEquals(expected, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Unauthorized gameData request")
    public void unauthorizedGameDataRequest() {
        try {
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            AuthData authDataFake = new AuthData(authData.username(), "notarealtoken");
            assertThrows(UnauthorizedException.class, () -> gameService.getGames(dataAccess, authDataFake));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("When DB is empty, getGames returns []")
    public void emptyDBGameDataRequest() {
        try {
            var gameName = "New Game";
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            gameService.createGame(dataAccess, new CreateGameRequest(gameName), authData);
            assertEquals(gameName, gameService.getGames(dataAccess, authData).get(0).gameName());

            registrationService.clearDatabase(dataAccess);

            registrationService.registerUser(dataAccess, regularUser);
            authData = sessionService.loginUser(dataAccess, regularUser);
            assertEquals(0, gameService.getGames(dataAccess, authData).size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Creating a game adds it to database")
    public void addGameToDB() {
        try {
            var gameName = "New Game";
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            gameService.createGame(dataAccess, new CreateGameRequest(gameName), authData);
            assertEquals(gameName, gameService.getGames(dataAccess, authData).get(0).gameName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Unauthorized createGame request")
    public void unauthorizedCreateGameRequest() {
        try {
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            AuthData authDataFake = new AuthData(authData.username(), "notarealtoken");
            assertThrows(UnauthorizedException.class, () -> gameService.createGame(dataAccess, new CreateGameRequest("Malicious game"), authDataFake));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Missing gameName in create game request")
    public void missingDataCreateGame() {
        try {
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            assertThrows(MissingDataException.class, () -> gameService.createGame(dataAccess, new CreateGameRequest(null), authData));
            assertThrows(MissingDataException.class, () -> gameService.createGame(dataAccess, new CreateGameRequest(""), authData));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("User can join a game")
    public void joinGameSuccess() {
        try {
            var gameName = "New Game";
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            var gameID = gameService.createGame(dataAccess, new CreateGameRequest(gameName), authData);
            gameService.joinGame(dataAccess, authData, new JoinGameRequest("WHITE", gameID));
            var games = gameService.getGames(dataAccess, authData);
            var gameFirst = games.getFirst();
            assertEquals(authData.username(), gameFirst.whiteUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Unauthorized join game request")
    public void unauthorizedJoinGame() {
        try {
            var gameName = "New Game";
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            var gameID = gameService.createGame(dataAccess, new CreateGameRequest(gameName), authData);
            var fakeAuth = new AuthData("fakeuser", "notatoken");
            assertThrows(UnauthorizedException.class, () -> gameService.joinGame(dataAccess, fakeAuth, new JoinGameRequest("WHITE", gameID)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Cannot join nonexistent game")
    public void joinNonexistentGame() {
        try {
            var gameName = "New Game";
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);

            var gameID = gameService.createGame(dataAccess, new CreateGameRequest(gameName), authData);
            long fakeGameID = 1234123412;

            assertThrows(NotFoundException.class, () -> gameService.joinGame(dataAccess, authData, new JoinGameRequest("WHITE", fakeGameID)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Cannot join when the color is already taken")
    public void joinFilledSpot() {
        try {
            var gameName = "New Game";
            AuthData authData = sessionService.loginUser(dataAccess, regularUser);
            var otherUser = new User("otheruser", "pass", "mail");
            registrationService.registerUser(dataAccess, otherUser);
            AuthData authData1 = sessionService.loginUser(dataAccess, otherUser);

            var gameID = gameService.createGame(dataAccess, new CreateGameRequest(gameName), authData);

            gameService.joinGame(dataAccess, authData1, new JoinGameRequest("WHITE", gameID));
            assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(dataAccess, authData, new JoinGameRequest("WHITE", gameID)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
