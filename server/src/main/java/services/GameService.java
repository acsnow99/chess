package services;

import exceptions.MissingDataException;
import exceptions.NotFoundException;
import model.AuthData;
import dataAccess.DataAccess;
import exceptions.DataAccessException;
import model.GameData;
import generators.GameIDGenerator;
import exceptions.UnauthorizedException;
import requests.CreateGameRequest;
import requests.JoinGameRequest;

import java.util.ArrayList;

public class GameService {

    public ArrayList<GameData> getGames(DataAccess dataAccess, AuthData authData) throws DataAccessException, UnauthorizedException {
        if (dataAccess.authDataIsAuthorized(authData)) {
            return dataAccess.getGames();
        } else {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

    public long createGame(DataAccess dataAccess, CreateGameRequest createGameRequest, AuthData authData) throws DataAccessException, UnauthorizedException, MissingDataException {
        if (createGameRequest.gameName() == null || createGameRequest.gameName().equals("")) {
            throw new MissingDataException("Error: bad request");
        }
        if (dataAccess.authDataIsAuthorized(authData)) {
            var gameID = new GameIDGenerator().generateGameID();
            dataAccess.createGame(createGameRequest.gameName(), gameID);
            return gameID;
        } else {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

    public void joinGame(DataAccess dataAccess, AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, UnauthorizedException, NotFoundException {
        if (dataAccess.authDataIsAuthorized(authData)) {
            var authDataComplete = dataAccess.getAuthDataFromToken(authData);
            dataAccess.joinGame(authDataComplete, joinGameRequest);
        } else {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }
}
