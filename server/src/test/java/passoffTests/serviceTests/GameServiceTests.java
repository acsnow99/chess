package passoffTests.serviceTests;

import authData.AuthData;
import dataAccess.DataAccessException;
import gameData.GameData;
import gameService.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import otherExceptions.MissingDataException;
import otherExceptions.UnauthorizedException;
import registrationService.RegistrationService;
import sessionService.SessionService;
import user.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private RegistrationService registrationService = new RegistrationService();
    private SessionService sessionService = new SessionService();
    private GameService gameService = new GameService();
    private User regularUser = new User("kevin23", "okokokok99", "okay@gmail.com");

    @BeforeEach
    public void init() {
        try {
            registrationService.clearDatabase();
            registrationService.registerUser(regularUser);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("Default game data returns correctly")
    public void getGameData() {
        ArrayList<GameData> result = null;
        var game = new GameData(12345, "Al", "Darin", "The Game");
        var expected = new ArrayList<GameData>();
        expected.add(game);
        try {
            AuthData authData = sessionService.loginUser(regularUser);
            result = gameService.getGames(authData);
            assertEquals(expected, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Unauthorized gameData request")
    public void unauthorizedGameDataRequest() {
        try {
            AuthData authData = sessionService.loginUser(regularUser);
            AuthData authDataFake = new AuthData(authData.username(), "notarealtoken");
            assertThrows(UnauthorizedException.class, () -> gameService.getGames(authDataFake));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
