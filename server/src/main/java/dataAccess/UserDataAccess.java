package dataAccess;

import authData.AuthData;
import authTokenGenerator.AuthTokenGenerator;
import user.User;

import java.util.ArrayList;

public class UserDataAccess {

    private ArrayList<User> userDB = new ArrayList<>();
    private ArrayList<AuthData> authDB = new ArrayList<>();

    public User getUser(User user) {
        // CHANGEME - check just username
        if (userDB.contains(user)) {
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

}
