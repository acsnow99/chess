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

    private String emptyDBString = "[]";
    private String userDBFileName = "db/user.json";
    private String authDBFileName = "db/auth.json";
    private String gamesDBFileName = "db/games.json";


    public User getUser(User user) throws DataAccessException {
        if (userDBContainsUsername(user.username())) {
            return user;
        } else {
            return null;
        }
    }

    public AuthData registerUser(User user) throws DataAccessException {
        var db = userReadJsonFromLocalDBFile(userDBFileName);
        db.add(user);
        var dbAsJson = new Gson().toJson(db);
        writeToLocalDBFile(userDBFileName, dbAsJson);
        return loginUser(user);
    }

    public AuthData loginUser(User user) throws DataAccessException {
        var authDB = authReadJsonFromLocalDBFile(authDBFileName);
        String authToken = new AuthTokenGenerator().generateToken();
        AuthData authDataResult = new AuthData(user.username(), authToken);
        // TO-DO: check for existing token based on authToken
        authDB.add(authDataResult);
        writeToLocalDBFile(authDBFileName, new Gson().toJson(authDB));
        return authDataResult;
    }

    public boolean userIsAuthorized(User user) throws DataAccessException {
        var userDB = userReadJsonFromLocalDBFile(userDBFileName);
        for (var userEntry : userDB) {
            if (Objects.equals(user.username(), userEntry.username()) && Objects.equals(user.password(), userEntry.password())) {
                return true;
            }
        }
        return false;
    }


    public void logoutUser(AuthData authData) throws DataAccessException {
        var authDB = authReadJsonFromLocalDBFile(authDBFileName);
        var authDBResult = new ArrayList<AuthData>();
        for (var auth : authDB) {
            if (!Objects.equals(auth.authToken(), authData.authToken())) {
                authDBResult.add(auth);
            }
        }
        writeToLocalDBFile(authDBFileName, new Gson().toJson(authDBResult));
    }

    private boolean userDBContainsUsername(String username) {
        // TO-DO: Make function throw exception
        try {
            var userDB = userReadJsonFromLocalDBFile(userDBFileName);
            for (var user : userDB) {
                if (Objects.equals(user.username(), username)) {
                    return true;
                }
            }
            return false;
        } catch (DataAccessException exception) {
            return false;
        }
    }

    // NOTE: authData's username may be null. This function only compares authTokens only
    public AuthData getAuthDataFromToken(AuthData authData) throws DataAccessException {
        var authDB = authReadJsonFromLocalDBFile(authDBFileName);
        for (var auth : authDB) {
            if (Objects.equals(auth.authToken(), authData.authToken())) {
                return auth;
            }
        }
        return null;
    }

    public boolean authDataIsAuthorized(AuthData authData) throws DataAccessException {
        if (getAuthDataFromToken(authData) != null) {
            return true;
        } else {
            return false;
        }
    }

    private void writeToLocalDBFile(String dbFileName, String textToWrite) throws DataAccessException {
        try (var userFileWriter = new FileWriter(dbFileName)) {
            userFileWriter.write(textToWrite);
        } catch (IOException exception) {
            throw new DataAccessException("Error: could not write " + textToWrite + " to " + dbFileName);
        }
    }

    private ArrayList<User> userReadJsonFromLocalDBFile(String dbFileName) throws DataAccessException {
        try (var fileReader = new JsonReader(new FileReader(dbFileName))) {
            var usersInDB = new ArrayList<User>();
            fileReader.beginArray();
            while (fileReader.hasNext()) {
                usersInDB.add(new Gson().fromJson(fileReader, User.class));
            }
            fileReader.endArray();
            return usersInDB;
        } catch (IOException exception) {
            throw new DataAccessException("Error: could not read from " + dbFileName);
        }
    }


    private ArrayList<AuthData> authReadJsonFromLocalDBFile(String dbFileName) throws DataAccessException {
        try (var fileReader = new JsonReader(new FileReader(dbFileName))) {
            var authInDB = new ArrayList<AuthData>();
            fileReader.beginArray();
            while (fileReader.hasNext()) {
                authInDB.add(new Gson().fromJson(fileReader, AuthData.class));
            }
            fileReader.endArray();
            return authInDB;
        } catch (IOException exception) {
            throw new DataAccessException("Error: could not read from " + dbFileName);
        }
    }


    public ArrayList<GameData> getGames() throws DataAccessException {
        return gamesReadJsonFromLocalDBFile(gamesDBFileName);
    }

    public void createGame(String gameName, long gameID) throws DataAccessException {
        var gameData = gamesReadJsonFromLocalDBFile(gamesDBFileName);
        var newGame = new GameData(gameID, null, null, gameName, new ArrayList<String>());
        gameData.add(newGame);
        writeToLocalDBFile(gamesDBFileName, new Gson().toJson(gameData));
    }

    public void joinGame(AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, NotFoundException, AlreadyTakenException {
        var gamesDB = getGames();
        var foundGame = false;
        for (var game : gamesDB) {
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
                gamesDB.remove(game);
                gamesDB.add(newGame);
                writeToLocalDBFile(gamesDBFileName, new Gson().toJson(gamesDB));
                break;
            }
        }
        if (!foundGame) {
            throw new NotFoundException("Error: Could not find game");
        }
    }

    private ArrayList<GameData> gamesReadJsonFromLocalDBFile(String dbFileName) throws DataAccessException {
        try (var fileReader = new JsonReader(new FileReader(dbFileName))) {
            var authInDB = new ArrayList<GameData>();
            fileReader.beginArray();
            while (fileReader.hasNext()) {
                authInDB.add(new Gson().fromJson(fileReader, GameData.class));
            }
            fileReader.endArray();
            return authInDB;
        } catch (IOException exception) {
            throw new DataAccessException("Error: could not read from " + dbFileName);
        }
    }


    public Object clear() throws DataAccessException {
        writeToLocalDBFile(userDBFileName, emptyDBString);
        writeToLocalDBFile(authDBFileName, emptyDBString);
        writeToLocalDBFile(gamesDBFileName, emptyDBString);
        return null;
    }
}
