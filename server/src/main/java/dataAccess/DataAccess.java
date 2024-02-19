package dataAccess;

import authData.AuthData;
import authTokenGenerator.AuthTokenGenerator;
import user.User;
import gameData.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class DataAccess {

    private ArrayList<User> userDB = new ArrayList<>();
    private ArrayList<AuthData> authDB = new ArrayList<>();
    private ArrayList<GameData> gameDB = new ArrayList<>();

    public User getUser(User user) {
        // CHANGEME - check just username
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

    public Object clear() {
        userDB = new ArrayList<>();
        authDB = new ArrayList<>();
        gameDB = new ArrayList<>();
        return null;
    }
}
