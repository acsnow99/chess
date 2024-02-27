package dataAccess;

import exceptions.AlreadyTakenException;
import exceptions.NotFoundException;
import model.AuthData;
import generators.AuthTokenGenerator;
import com.google.gson.stream.JsonReader;
import exceptions.DataAccessException;
import requests.JoinGameRequest;
import model.User;
import model.GameData;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class DataAccess {
    private ArrayList<AuthData> authDB = new ArrayList<>();
    private ArrayList<GameData> gameDB = new ArrayList<>();
    private ArrayList<User> userDB = new ArrayList<>();


    public User getUser(User user) {
        if (userDBContainsUsername(user.username())) {
            return user;
        } else {
            return null;
        }
    }

    public AuthData registerUser(User user) {
        userDB.add(user);
        return loginUser(user);
    }

    public AuthData loginUser(User user) {
        String authToken = new AuthTokenGenerator().generateToken();
        AuthData authDataResult = new AuthData(user.username(), authToken);
        // TO-DO: check for existing token based on authToken
        authDB.add(authDataResult);
        return authDataResult;
    }

    public boolean userIsAuthorized(User user) {
        for (var userEntry : userDB) {
            if (Objects.equals(user.username(), userEntry.username()) && Objects.equals(user.password(), userEntry.password())) {
                return true;
            }
        }
        return false;
    }


    public void logoutUser(AuthData authData) throws DataAccessException {
        for (var auth : authDB) {
            if (Objects.equals(auth.authToken(), authData.authToken())) {
                authDB.remove(auth);
                break;
            }
        }
    }

    private boolean userDBContainsUsername(String username) {
        for (var user : userDB) {
            if (Objects.equals(user.username(), username)) {
                return true;
            }
        }
        return false;
    }

    // NOTE: authData's username may be null. This function only compares authTokens only
    public AuthData getAuthDataFromToken(AuthData authData) {
        for (var auth : authDB) {
            if (Objects.equals(auth.authToken(), authData.authToken())) {
                return auth;
            }
        }
        return null;
    }

    public boolean authDataIsAuthorized(AuthData authData) {
        if (getAuthDataFromToken(authData) != null) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<GameData> getGames() {
        return gameDB;
    }

    public void createGame(String gameName, long gameID) throws DataAccessException {
        var newGame = new GameData(gameID, null, null, gameName, new ArrayList<String>());
        gameDB.add(newGame);
    }

    public void joinGame(AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, NotFoundException, AlreadyTakenException {
        var foundGame = false;
        for (var game : gameDB) {
            if (game.gameID() == joinGameRequest.gameID()) {
                foundGame = true;
                GameData newGame;
                if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
                    if (game.whiteUsername() != null && !Objects.equals(game.whiteUsername(), authData.username())) {
                        throw new AlreadyTakenException("Error: Already taken");
                    }
                    newGame = new GameData(joinGameRequest.gameID(), authData.username(), game.blackUsername(), game.gameName(), game.watchers());
                } else if (Objects.equals(joinGameRequest.playerColor(), "BLACK")) {
                    if (game.blackUsername() != null && !Objects.equals(game.blackUsername(), authData.username())) {
                        throw new AlreadyTakenException("Error: Already taken");
                    }
                    newGame = new GameData(joinGameRequest.gameID(), game.whiteUsername(), authData.username(), game.gameName(), game.watchers());
                } else {
                    var newWatchers = game.watchers();
                    newWatchers.add(authData.username());
                    newGame = new GameData(joinGameRequest.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), newWatchers);
                }
                gameDB.remove(game);
                gameDB.add(newGame);
                break;
            }
        }
        if (!foundGame) {
            throw new NotFoundException("Error: Could not find game");
        }
    }


    public Object clear() {
        userDB = new ArrayList<>();
        authDB = new ArrayList<>();
        gameDB = new ArrayList<>();
        return null;
    }
}
