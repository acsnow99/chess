package dataAccess;

import authData.AuthData;
import authTokenGenerator.AuthTokenGenerator;
import user.User;
import gameData.GameData;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class DataAccess {

    private ArrayList<User> userDB = new ArrayList<>();
    private String userDBFileName = "db/user.json";
    private ArrayList<AuthData> authDB = new ArrayList<>();
    private ArrayList<GameData> gameDB = new ArrayList<>();

    public User getUser(User user) {
        if (userDBContainsUsername(user.username())) {
            return user;
        } else {
            return null;
        }
    }

    public AuthData registerUser(User user) throws DataAccessException {
        userDB.add(user);
        String dbAsJson = new Gson().toJson(userDB);
        writeToLocalDBFile(userDBFileName, dbAsJson);
        // TO-DO create file if it doesn't exist
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
        for (User user : userDB) {
            if (Objects.equals(user.username(), username)) {
                return true;
            }
        }
        return false;
    }

    public Object clear() throws DataAccessException {
        var emptyDBString = "";
        userDB = new ArrayList<>();
        writeToLocalDBFile(userDBFileName, emptyDBString);
        authDB = new ArrayList<>();
        gameDB = new ArrayList<>();
        return null;
    }

    private void writeToLocalDBFile(String dbFileName, String textToWrite) throws DataAccessException {
        try (var userFileWriter = new FileWriter(dbFileName)) {
            userFileWriter.write(textToWrite);
        } catch (IOException exception) {
            throw new DataAccessException("Error: could not write to " + dbFileName);
        }
    }
}
