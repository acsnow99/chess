package services;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import exceptions.*;
import model.AuthData;
import dataAccess.DataAccess;
import model.GameData;
import generators.GameIDGenerator;
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

    public GameData getGameByID(DataAccess dataAccess, long gameID) throws DataAccessException {
        ArrayList<GameData> gamesList = dataAccess.getGames();
        GameData gameResult = null;
        for (var game : gamesList) {
            if (game.gameID() == gameID) {
                gameResult = game;
            }
        }
        return gameResult;
    }

    public long createGame(DataAccess dataAccess, CreateGameRequest createGameRequest, AuthData authData) throws DataAccessException, UnauthorizedException, MissingDataException {
        if (createGameRequest == null || createGameRequest.gameName() == null || createGameRequest.gameName().equals("")) {
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

    public void joinGame(DataAccess dataAccess, AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, UnauthorizedException, NotFoundException, AlreadyTakenException {
        if (dataAccess.authDataIsAuthorized(authData)) {
            var authDataComplete = dataAccess.getAuthDataFromToken(authData);
            dataAccess.joinGame(authDataComplete, joinGameRequest);
        } else {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

    public String makeMoveGame(DataAccess dataAccess, AuthData authData, long gameID, ChessMove move) throws DataAccessException, UnauthorizedException, NotFoundException, InvalidMoveException {
        if (dataAccess.authDataIsAuthorized(authData)) {
            var authDataComplete = dataAccess.getAuthDataFromToken(authData);
            return dataAccess.makeMoveGame(authDataComplete, gameID, move);
        } else {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

    public void removePlayer(DataAccess dataAccess, String authToken, long gameID) {
        try {
            dataAccess.removePlayer(new AuthData("", authToken), gameID);
        } catch (Exception ignored) {

        }
    }

    public void setGameFinished(DataAccess dataAccess, long gameID) throws DataAccessException, NotFoundException {
        dataAccess.endGame(gameID);
    }
}
