package dataAccess;

import authData.AuthData;
import authTokenGenerator.AuthTokenGenerator;
import com.google.gson.stream.JsonReader;
import user.User;
import gameData.GameData;

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
        for (var auth : authDB) {
            if (Objects.equals(auth.username(), user.username())) {
                return auth;
            }
        }
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

    public Object clear() throws DataAccessException {
        writeToLocalDBFile(userDBFileName, emptyDBString);
        writeToLocalDBFile(authDBFileName, emptyDBString);
        return null;
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

    public ArrayList<GameData> getGames() throws DataAccessException {
        return gamesReadJsonFromLocalDBFile(gamesDBFileName);
    }
}
