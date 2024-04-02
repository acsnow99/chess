package dataAccess;

import chess.ChessMove;
import exceptions.AlreadyTakenException;
import exceptions.NotFoundException;
import exceptions.UnauthorizedException;
import model.AuthData;
import exceptions.DataAccessException;
import requests.JoinGameRequest;
import model.User;
import model.GameData;

import java.util.ArrayList;

public interface DataAccess {
    void databaseInit() throws DataAccessException;

    User getUser(User user) throws DataAccessException;

    AuthData registerUser(User user) throws DataAccessException;

    AuthData loginUser(User user) throws DataAccessException;

    boolean userIsAuthorized(User user) throws DataAccessException;


    void logoutUser(AuthData authData) throws DataAccessException;

    boolean userDBContainsUsername(User user) throws DataAccessException;

    // NOTE: authData's username may be null. This function only compares authTokens only
    AuthData getAuthDataFromToken(AuthData authData) throws DataAccessException;

    boolean authDataIsAuthorized(AuthData authData) throws DataAccessException;

    ArrayList<GameData> getGames() throws DataAccessException;

    void createGame(String gameName, long gameID) throws DataAccessException;

    void joinGame(AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, NotFoundException, AlreadyTakenException;

    void makeMoveGame(AuthData authData, long gameID, ChessMove move) throws DataAccessException, NotFoundException;


    Object clear() throws DataAccessException;
}
