package gameService;

import authData.AuthData;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import gameData.GameData;
import otherExceptions.UnauthorizedException;

import java.util.ArrayList;

public class GameService {

    private DataAccess dataAccess = new DataAccess();

    public ArrayList<GameData> getGames(AuthData authData) throws DataAccessException, UnauthorizedException {
        if (dataAccess.authDataIsAuthorized(authData)) {
            return dataAccess.getGames();
        } else {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

}
