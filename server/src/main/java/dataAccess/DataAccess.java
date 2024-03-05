package dataAccess;

import exceptions.AlreadyTakenException;
import exceptions.NotFoundException;
import model.AuthData;
import generators.AuthTokenGenerator;
import exceptions.DataAccessException;
import requests.JoinGameRequest;
import model.User;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public interface DataAccess {
    User getUser(User user) throws DataAccessException;

    AuthData registerUser(User user);

    AuthData loginUser(User user) throws DataAccessException;

    boolean userIsAuthorized(User user) throws DataAccessException;


    void logoutUser(AuthData authData) throws DataAccessException;

    boolean userDBContainsUsername(String username);

    // NOTE: authData's username may be null. This function only compares authTokens only
    AuthData getAuthDataFromToken(AuthData authData) throws DataAccessException;

    boolean authDataIsAuthorized(AuthData authData);

    ArrayList<GameData> getGames();

    void createGame(String gameName, long gameID) throws DataAccessException;

    void joinGame(AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, NotFoundException, AlreadyTakenException;


    Object clear() throws DataAccessException;
}
