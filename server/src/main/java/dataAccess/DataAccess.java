package dataAccess;

import authData.AuthData;
import authTokenGenerator.AuthTokenGenerator;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
import user.User;
import gameData.GameData;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class DataAccess {

    private String emptyDBString = "[]";
    private String userDBFileName = "db/user.json";
    private ArrayList<AuthData> authDB = new ArrayList<>();
    private ArrayList<GameData> gameDB = new ArrayList<>();

    public User getUser(User user) throws DataAccessException {
        if (userDBContainsUsername(user.username())) {
            return user;
        } else {
            return null;
        }
    }

    public AuthData registerUser(User user) throws DataAccessException {
        var db = readJsonFromLocalDBFile(userDBFileName);
        db.add(user);
        var dbAsJson = new Gson().toJson(db);
        writeToLocalDBFile(userDBFileName, dbAsJson);
        return loginUser(user);
    }

    public AuthData loginUser(User user) {
        String authToken = new AuthTokenGenerator().generateToken();
        AuthData authDataResult = new AuthData(user.username(), authToken);
        // CHANGEME - check for existing token based on authToken only, not username
        while (authDB.contains(authDataResult)) {
            authToken = new AuthTokenGenerator().generateToken();
            authDataResult = new AuthData(user.username(), authToken);
        }
        authDB.add(authDataResult);
        return authDataResult;
    }

    private boolean userDBContainsUsername(String username) {
        try {
            var userDB = readJsonFromLocalDBFile(userDBFileName);
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
        var emptyDBString = "[]";
        writeToLocalDBFile(userDBFileName, emptyDBString);
        authDB = new ArrayList<>();
        gameDB = new ArrayList<>();
        return null;
    }

    private void writeToLocalDBFile(String dbFileName, String textToWrite) throws DataAccessException {
        try (var userFileWriter = new FileWriter(dbFileName)) {
            userFileWriter.write(textToWrite);
        } catch (IOException exception) {
            throw new DataAccessException("Error: could not write " + textToWrite + " to " + dbFileName);
        }
    }

    private ArrayList<User> readJsonFromLocalDBFile(String dbFileName) throws DataAccessException {
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
}
